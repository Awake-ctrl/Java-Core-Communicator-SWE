package cosmosoperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datastructures.Entity;
import datastructures.Response;
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
}