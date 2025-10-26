package functionapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import cosmosoperations.DbConnectorFactory;
import datastructures.Entity;
import datastructures.Response;
import interfaces.IdbConnector;

import java.util.Optional;

class CloudGetTest extends CloudTestBase {

    @Test
    void runCloudGetTest() throws Exception {
        HttpRequestMessage<Optional<String>> request = mockRequest("{\"module\":\"mod\",\"table\":\"tab\",\"id\":\"123\"}");
        ExecutionContext context = mockContext();

        IdbConnector mockConnector = mock(IdbConnector.class);
        Response mockResponse = new Response(200, "success", null);

        try (MockedStatic<DbConnectorFactory> factoryMock = Mockito.mockStatic(DbConnectorFactory.class)) {
            factoryMock.when(() -> DbConnectorFactory.getDbConnector("mock")).thenReturn(mockConnector);
            when(mockConnector.getData(any(Entity.class))).thenReturn(mockResponse);

            CloudGet cloudGet = new CloudGet();
            var response = cloudGet.runCloudGet(request, context);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatus());
        }
    }

    @Test
    void runCloudGetTestException() throws Exception {
        HttpRequestMessage<Optional<String>> request = mockRequest("invalid json");
        ExecutionContext context = mockContext();

        CloudGet cloudGet = new CloudGet();
        var response = cloudGet.runCloudGet(request, context);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }
}
