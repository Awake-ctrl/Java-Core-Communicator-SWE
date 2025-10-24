package cosmosoperations;

import com.azure.cosmos.*;
import datastructures.Entity;
import datastructures.Response;
import interfaces.IdbConnector;

public class CosmosOperations implements IdbConnector {

    CosmosClient client;
    final String databaseName = "SE";
    CosmosDatabase database;
    final String containerName = "Project";
    public void init() {
        try {
            client = new CosmosClientBuilder()
                    .endpoint("https://kallepally.documents.azure.com:443/")
                    .key("key")
                    .consistencyLevel(ConsistencyLevel.EVENTUAL)
                    .contentResponseOnWriteEnabled(true)
                    .buildClient();

            CosmosDatabase db =  client.getDatabase(databaseName);

            CosmosContainer container = db.getContainer(containerName);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public Response getData(Entity request) {
        return null;
    }

    @Override
    public Response postData(Entity request) {
        return null;
    }

    @Override
    public Response createData(Entity request) {
        return null;
    }

    @Override
    public Response deleteData(Entity request) {
        return null;
    }

    @Override
    public Response updateData(Entity request) {
        return null;
    }
}
