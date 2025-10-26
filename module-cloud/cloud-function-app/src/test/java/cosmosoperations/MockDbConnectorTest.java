package cosmosoperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datastructures.Entity;
import datastructures.Response;
import interfaces.IdbConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class MockDbConnectorTest {

    private final IdbConnector mockDbConnector = new MockDbConnector();
    private Entity testEntity;

    @BeforeEach
    void createTestEntity() {
        testEntity  = new Entity("TestModule", "TestTable", "TestId", null, -1, null, null);
    }
    @Test
    void initTest() {
        MockDbConnector spyConnector = org.mockito.Mockito.spy(new MockDbConnector());
        spyConnector.init();
        org.mockito.Mockito.verify(spyConnector, org.mockito.Mockito.times(1)).init();
    }

    @Test
    void getDataTest() {
        Response testResponse = mockDbConnector.getData(testEntity);
        assertInstanceOf(Response.class, testResponse);
        assertEquals(200, testResponse.status_code());
        assertEquals("success", testResponse.message());

        ObjectNode testObject = new ObjectMapper().createObjectNode();
        testObject.put("operation", "GET");
        testObject.put("module", "TestModule");
        testObject.put("table", "TestTable");
        testObject.put("id", "TestId");

        assertEquals(testObject, testResponse.data());
    }

    @Test
    void postDataTest() {

        Response testResponse = mockDbConnector.postData(testEntity);
        assertInstanceOf(Response.class, testResponse);
        assertEquals(200, testResponse.status_code());
        assertEquals("success", testResponse.message());

        ObjectNode testObject = new ObjectMapper().createObjectNode();
        testObject.put("operation", "POST");
        testObject.put("module", "TestModule");
        testObject.put("table", "TestTable");
        testObject.put("id", "TestId");

        assertEquals(testObject, testResponse.data());
    }

    @Test
    void createDataTest() {

        Response testResponse = mockDbConnector.createData(testEntity);
        assertInstanceOf(Response.class, testResponse);
        assertEquals(200, testResponse.status_code());
        assertEquals("success", testResponse.message());

        ObjectNode testObject = new ObjectMapper().createObjectNode();
        testObject.put("operation", "CREATE");
        testObject.put("module", "TestModule");
        testObject.put("table", "TestTable");
        testObject.put("id", "TestId");

        assertEquals(testObject, testResponse.data());
    }

    @Test
    void deleteDataTest() {

        Response testResponse = mockDbConnector.deleteData(testEntity);
        assertInstanceOf(Response.class, testResponse);
        assertEquals(200, testResponse.status_code());
        assertEquals("success", testResponse.message());

        ObjectNode testObject = new ObjectMapper().createObjectNode();
        testObject.put("operation", "DELETE");
        testObject.put("module", "TestModule");
        testObject.put("table", "TestTable");
        testObject.put("id", "TestId");

        assertEquals(testObject, testResponse.data());
    }

    @Test
    void updateDataTest() {

        Response testResponse = mockDbConnector.updateData(testEntity);
        assertInstanceOf(Response.class, testResponse);
        assertEquals(200, testResponse.status_code());
        assertEquals("success", testResponse.message());

        ObjectNode testObject = new ObjectMapper().createObjectNode();
        testObject.put("operation", "UPDATE");
        testObject.put("module", "TestModule");
        testObject.put("table", "TestTable");
        testObject.put("id", "TestId");

        assertEquals(testObject, testResponse.data());
    }
}