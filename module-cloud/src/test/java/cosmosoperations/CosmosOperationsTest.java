package cosmosoperations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datastructures.Entity;
import datastructures.Response;
import datastructures.TimeRange;
import interfaces.IdbConnector;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CosmosOperationsTest {

    private static IdbConnector cosmosDbConnector;
    private static Entity testEntity;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Entity deleteContainerEntity;

    @BeforeAll
    static void setupClass() {
        cosmosDbConnector = new CosmosOperations();
        cosmosDbConnector.init();

        ObjectNode dataNode = mapper.createObjectNode();
        dataNode.put("data1", 0);
        dataNode.put("data2", 1);

        testEntity = new Entity(
                "TestModule",
                "TestTable",
                "TestId",
                null,
                -1,
                null,
                dataNode
        );
        deleteContainerEntity = new Entity(
                "TestModule",
                "TestTable",
                null, null, -1, null, null
        );
        cosmosDbConnector.createData(testEntity);
    }

    @AfterAll
    static void tearDownClass() {
        try {
            cosmosDbConnector.deleteData(deleteContainerEntity);
        } catch (Exception e) {
            // Ignore errors, we are just cleaning up
            System.out.println("Cleanup warning (AfterAll): " + e.getMessage());
        }
    }

    @AfterEach
    void tearDownTest() {
        try {
            cosmosDbConnector.deleteData(testEntity);
        } catch (Exception e) {
            // Ignore errors, we are just cleaning up
            System.out.println("Cleanup warning (AfterEach): " + e.getMessage());
        }
    }

    @BeforeEach
    void setupTest() {
        cosmosDbConnector.postData(testEntity);
    }

    @Test
    void getDataTest() {
        Response response = cosmosDbConnector.getData(testEntity);

        assertInstanceOf(Response.class, response);
        assertEquals(200, response.status_code());
        assertTrue(
                response.message().contains("retrieved successfully"),
                "Expected retrieval success message"
        );
    }

    @Test
    void postData_ConflictTest() {
        Response response = cosmosDbConnector.postData(testEntity);

        assertEquals(409, response.status_code());
        assertTrue(response.message().contains("already exists"),
                "Expected conflict message for duplicate ID");
    }

    @Test
    void postDataTest() {
        cosmosDbConnector.deleteData(testEntity);

        Response response = cosmosDbConnector.postData(testEntity);

        assertInstanceOf(Response.class, response);
        assertEquals(200, response.status_code());
        assertEquals("Document inserted successfully.", response.message());
        assertNull(response.data(), "Insert operation should return null data");
    }

    @Test
    void createDataTest() {
        Response response = cosmosDbConnector.createData(testEntity);

        assertInstanceOf(Response.class, response);
        assertEquals(200, response.status_code());
        assertTrue(response.message().contains("already exists"),
                "Expected container creation or existence message"
        );
    }

    @Test
    void deleteDataTest() {
        Response response = cosmosDbConnector.deleteData(testEntity);

        assertInstanceOf(Response.class, response);
        assertEquals(200, response.status_code());
        assertTrue(
                response.message().contains("deleted successfully"),
                "Expected deletion success message"
        );
    }

    @Test
    void deleteData_SpecificFieldTest() {
        Entity deleteFieldEntity = new Entity(
                testEntity.module(), testEntity.table(), testEntity.id(),
                "data1", -1, null, null
        );
        Response deleteResponse = cosmosDbConnector.deleteData(deleteFieldEntity);
        assertEquals(200, deleteResponse.status_code());
        assertTrue(deleteResponse.message().contains("Field 'data1' deleted"));

        Response getResponse = cosmosDbConnector.getData(testEntity);
        JsonNode fetchedData = getResponse.data().get("data");

        assertFalse(fetchedData.has("data1"), "Field 'data1' should have been deleted");
        assertTrue(fetchedData.has("data2"), "Field 'data2' should still exist");
    }

    @Test
    void deleteContainerTest() {
        Response response = cosmosDbConnector.deleteData(deleteContainerEntity);

        assertInstanceOf(Response.class, response);
        assertEquals(200, response.status_code());
        assertTrue(
                response.message().contains("deleted successfully"),
                "Expected container deletion success message"
        );
        cosmosDbConnector.createData(testEntity);
    }

    @Test
    void updateDataTest() {
        ObjectNode updatedData = mapper.createObjectNode();
        updatedData.put("data1", 10);
        updatedData.put("data2", 20);

        Entity updateEntity = new Entity(
                "TestModule",
                "TestTable",
                "TestId",
                null,
                -1,
                null,
                updatedData
        );

        Response response = cosmosDbConnector.updateData(updateEntity);

        assertInstanceOf(Response.class, response);
        assertEquals(200, response.status_code());
        assertEquals("Document updated successfully.", response.message());
        assertNull(response.data(), "Update operation should return null data");
    }

    @Test
    void updateData_SpecificFieldTest() {
        ObjectNode dataWrapper = mapper.createObjectNode();
        dataWrapper.put("data1", 999);

        Entity updateFieldEntity = new Entity(
                testEntity.module(), testEntity.table(), testEntity.id(),
                "data1", -1, null, dataWrapper
        );
        Response updateResponse = cosmosDbConnector.updateData(updateFieldEntity);
        assertEquals(200, updateResponse.status_code());

        Response getResponse = cosmosDbConnector.getData(testEntity);
        JsonNode fetchedData = getResponse.data().get("data");

        assertEquals(999, fetchedData.get("data1").asInt());
        assertEquals(1, fetchedData.get("data2").asInt());
    }

    @Test
    void updateData_AddNewFieldTest() {
        ObjectNode dataWrapper = mapper.createObjectNode();
        dataWrapper.put("data3", 888);

        Entity updateNewFieldEntity = new Entity(
                testEntity.module(), testEntity.table(), testEntity.id(),
                "data3", -1, null, dataWrapper
        );
        Response updateResponse = cosmosDbConnector.updateData(updateNewFieldEntity);
        assertEquals(200, updateResponse.status_code());

        Response getResponse = cosmosDbConnector.getData(testEntity);
        JsonNode fetchedData = getResponse.data().get("data");

        assertTrue(fetchedData.has("data3"));
        assertEquals(888, fetchedData.get("data3").asInt());
    }

    @Test
    void getDataTest_FetchById_WithType() {
        Entity entityWithType = new Entity(
                testEntity.module(),
                testEntity.table(),
                testEntity.id(),
                "data1",
                -1,
                null,
                null
        );

        Response response = cosmosDbConnector.getData(entityWithType);
        assertEquals(200, response.status_code());
        assertEquals(0, response.data().asInt());
    }

    @Test
    void getDataTest_FetchAll_NoId() {
        Entity fetchAllEntity = new Entity(
                testEntity.module(),
                testEntity.table(),
                null,
                null, -1, null, null
        );

        Response response = cosmosDbConnector.getData(fetchAllEntity);
        assertEquals(200, response.status_code());
        assertTrue(response.data().isArray());
        assertEquals(1, response.data().size());
        assertEquals(testEntity.id(), response.data().get(0).path("id").asText());
    }

    @Test
    void getDataTest_FetchAll_WithTimeRange() {
        Entity fetchAllEntity = new Entity(
                testEntity.module(), testEntity.table(),
                null, null, -1, null, null
        );
        Response getResponse = cosmosDbConnector.getData(fetchAllEntity);
        double timestamp = getResponse.data().get(0).path("timestamp").asDouble();

        TimeRange timeRange = new TimeRange(timestamp - 10000.0f, timestamp + 10000.0f);

        Entity queryEntity = new Entity(
                testEntity.module(), testEntity.table(),
                null, null, -1, timeRange, null
        );

        Response queryResponse = cosmosDbConnector.getData(queryEntity);
        assertEquals(200, queryResponse.status_code());
        assertTrue(queryResponse.data().isArray());
        assertEquals(1, queryResponse.data().size());
    }

    @Test
    void getDataTest_FetchAll_WithLastNAndType() throws InterruptedException {
        Thread.sleep(10);
        ObjectNode dataNode2 = mapper.createObjectNode().put("data1", 100);
        Entity testEntity2 = new Entity(
                testEntity.module(), "TestTable", "TestId2",
                null, -1, null, dataNode2
        );
        cosmosDbConnector.postData(testEntity2);

        Entity fetchAllEntity = new Entity(
                testEntity.module(), testEntity.table(),
                null, "data1", 1, null, null
        );

        Response response = cosmosDbConnector.getData(fetchAllEntity);
        assertEquals(200, response.status_code());
        assertTrue(response.data().isArray());
        assertEquals(1, response.data().size());
        assertEquals(100, response.data().get(0).asInt());

        cosmosDbConnector.deleteData(testEntity2);
    }
}
