package functionapp;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import org.junit.jupiter.api.Test;

import datastructures.Response;

class CloudHelperTest {

    private final CloudHelper testHelper = new CloudHelper();

    private HttpRequestMessage<Optional<String>> mockRequest() {
        HttpRequestMessage<Optional<String>> request = mock(HttpRequestMessage.class);
        doAnswer(invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new FakeResponseBuilder().status(status);
        }).when(request).createResponseBuilder(any(HttpStatus.class));
        return request;
    }

    /** Normal success path */
    @Test
    void handleResponseTest() {
        HttpRequestMessage<Optional<String>> request = mockRequest();
        Response testResponse = new Response(200, "success", null);

        HttpResponseMessage response = testHelper.handleResponse(testResponse, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

//    /** Force exception inside handleResponse() */
//    @Test
//    void handleResponseExceptionTest() throws Exception {
//        HttpRequestMessage<Optional<String>> request = mockRequest();
//
//        ObjectMapper mockMapper = mock(ObjectMapper.class);
//        when(mockMapper.writeValueAsString(any())).thenThrow(new RuntimeException("forced exception"));
//
//        // Replace private objectMapper field using reflection
//        Field f = CloudHelper.class.getDeclaredField("objectMapper");
//        f.setAccessible(true);
//        f.set(testHelper, mockMapper);
//
//        Response testResponse = new Response(200, "fail", null);
//        HttpResponseMessage response = testHelper.handleResponse(testResponse, request);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
//    }

    /** Normal BAD_REQUEST JSON path */
    @Test
    void handleErrorTest() {
        HttpRequestMessage<Optional<String>> request = mockRequest();

        HttpResponseMessage response = testHelper.handleError(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

//    /** Force exception inside handleError() */
//    @Test
//    void handleErrorExceptionTest() throws Exception {
//        HttpRequestMessage<Optional<String>> request = mockRequest();
//
//        ObjectMapper mockMapper = mock(ObjectMapper.class);
//        when(mockMapper.writeValueAsString(any())).thenThrow(new RuntimeException("forced exception"));
//
//        // Replace private field again
//        Field f = CloudHelper.class.getDeclaredField("objectMapper");
//        f.setAccessible(true);
//        f.set(testHelper, mockMapper);
//
//        HttpResponseMessage response = testHelper.handleError(request);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
//        assertTrue(response.getBody().toString().contains("Invalid request"));
//    }
}
