package cosmosoperations;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datastructures.Entity;
import datastructures.Response;
import interfaces.IdbConnector;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Provides CRUD operations for Azure Cosmos DB.
 * Each container represents a table, and each document represents a record.
 */
public class CosmosOperations implements IdbConnector {

    /** Cosmos DB client. */
    private CosmosClient client;

    /** Cosmos DB database instance. */
    private CosmosDatabase database;

    /** JSON object mapper for serialization. */
    private final ObjectMapper mapper = new ObjectMapper();

    /** Endpoint URL for the Cosmos DB account. */
    private String endpoint;

    /** Authorization key for Cosmos DB. */
    private String key;

    /** Database name in Cosmos DB. */
    private String databaseName;

    /** Success status code constant. */
    private static final int HTTP_OK = 200;

    /** Default throughput value for new containers. */
    private static final int DEFAULT_THROUGHPUT = 400;

    /**
     * Initializes the Cosmos client using credentials from the .env file.
     */
    @Override
    public void init() {
        try {
            final Dotenv dotenv = Dotenv.load();
            endpoint = dotenv.get("COSMOS_ENDPOINT");
            key = dotenv.get("COSMOS_KEY");
            databaseName = dotenv.get("COSMOS_DATABASE");

            client = new CosmosClientBuilder()
                    .endpoint(endpoint)
                    .key(key)
                    .consistencyLevel(ConsistencyLevel.EVENTUAL)
                    .contentResponseOnWriteEnabled(true)
                    .buildClient();

            database = client.getDatabase(databaseName);
        } catch (final Exception e) {
            System.out.println("Error initializing Cosmos client: " + e.getMessage());
        }
    }

    /**
     * Retrieves data from a specified container.
     * @param request The entity containing the query details.
     * @return A Response containing matching documents or fields.
     */
    @Override
    public Response getData(final Entity request) {
        final CosmosContainer container = database.getContainer(request.module() + "_" + request.table());
        if (hasId(request)) {
            return fetchById(container, request);
        }
        return fetchAll(container, request);
    }

    /**
     * Checks if the given request has a valid ID.
     * @param request The entity to check.
     * @return True if the ID is not null or empty, false otherwise.
     */
    private boolean hasId(final Entity request) {
        return request.id() != null && !request.id().isEmpty();
    }

    /**
     * Fetches a single document by its ID.
     * @param container The Cosmos container.
     * @param request The entity containing ID and type.
     * @return Response with the document or specific field.
     */
    private Response fetchById(final CosmosContainer container, final Entity request) {
        final CosmosItemResponse<JsonNode> itemResponse =
                container.readItem(request.id(), new PartitionKey(request.id()), JsonNode.class);
        final JsonNode doc = itemResponse.getItem();

        if (request.type() != null && !request.type().isEmpty()) {
            final JsonNode data = doc.path("data").path(request.type());
            return new Response(HTTP_OK, "Field retrieved successfully.", data);
        }

        return new Response(HTTP_OK, "Document retrieved successfully.", doc);
    }

    /**
     * Fetches multiple documents with optional filters.
     * @param container The Cosmos container.
     * @param request The entity with query parameters.
     * @return Response with list of retrieved documents.
     */
    private Response fetchAll(final CosmosContainer container, final Entity request) {
        final String query = buildQuery(request);

        List<JsonNode> results = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class)
                .stream()
                .sorted((a, b) -> Long.compare(b.path("timestamp").asLong(), a.path("timestamp").asLong()))
                .collect(Collectors.toList());

        if (request.lastN() > 0 && results.size() > request.lastN()) {
            results = results.subList(0, request.lastN());
        }

        if (request.type() != null && !request.type().isEmpty()) {
            results = results.stream()
                    .map(r -> r.path("data").path(request.type()))
                    .collect(Collectors.toList());
        }

        return new Response(HTTP_OK, "Documents retrieved successfully.", mapper.valueToTree(results));
    }

    /**
     * Builds a Cosmos DB SQL query based on time range.
     * @param request The entity containing the time range.
     * @return The generated SQL query.
     */
    private String buildQuery(final Entity request) {
        if (request.timeRange() != null) {
            return String.format(
                    "SELECT * FROM c WHERE c.timestamp BETWEEN %f AND %f",
                    request.timeRange().fromTime(),
                    request.timeRange().toTime()
            );
        }
        return "SELECT * FROM c";
    }

    /**
     * Inserts a new document into the container.
     * @param request The entity containing the data to insert.
     * @return Response indicating insertion status.
     */
    @Override
    public Response postData(final Entity request) {
        final CosmosContainer container = database.getContainer(request.module() + "_" + request.table());
        final ObjectNode document = mapper.createObjectNode();

        document.put("id", request.id());
        document.put("timestamp", System.currentTimeMillis());
        document.set("data", request.data());

        container.createItem(document);
        return new Response(HTTP_OK, "Document inserted successfully.", null);
    }

    /**
     * Creates a new container if it doesn't exist.
     * @param request The entity specifying the table name.
     * @return Response indicating creation status.
     */
    @Override
    public Response createData(final Entity request) {
        final String tableName = request.module() + "_" + request.table();
        final boolean exists = database.readAllContainers()
                .stream()
                .anyMatch(c -> c.getId().equals(tableName));

        if (exists) {
            return new Response(HTTP_OK, "Container '" + tableName + "' already exists.", null);
        }

        final CosmosContainerProperties containerProperties =
                new CosmosContainerProperties(tableName, "/id");

        database.createContainerIfNotExists(containerProperties);
        return new Response(HTTP_OK, "Container '" + tableName + "' created successfully.", null);
    }

    /**
     * Deletes a specific document or an entire container.
     * @param request The entity containing table and optional ID.
     * @return Response indicating deletion result.
     */
    @Override
    public Response deleteData(final Entity request) {
        final String tableName = request.module() + "_" + request.table();
        final CosmosContainer container = database.getContainer(tableName);

        if (request.id() == null || request.id().isEmpty()) {
            container.delete();
            return new Response(HTTP_OK, "Container '" + tableName + "' deleted successfully.", null);
        }

        container.deleteItem(request.id(), new PartitionKey(request.id()), new CosmosItemRequestOptions());
        return new Response(HTTP_OK, "Document deleted successfully.", null);
    }

    /**
     * Updates a document with new data.
     * @param request The entity containing ID and updated data.
     * @return Response indicating update result.
     */
    @Override
    public Response updateData(final Entity request) {
        final CosmosContainer container = database.getContainer(request.module() + "_" + request.table());
        final JsonNode updatedData = request.data();

        final ObjectNode document = mapper.createObjectNode();
        document.put("id", request.id());
        document.put("timestamp", System.currentTimeMillis());
        document.set("data", updatedData);

        container.replaceItem(document, request.id(), new PartitionKey(request.id()), new CosmosItemRequestOptions());
        return new Response(HTTP_OK, "Document updated successfully.", null);
    }
}
