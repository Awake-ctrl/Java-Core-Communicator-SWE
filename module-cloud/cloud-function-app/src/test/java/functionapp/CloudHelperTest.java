package functionapp;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.microsoft.azure.functions.*;
import org.junit.jupiter.api.Test;

import datastructures.Response;

class CloudHelperTest {

    private final CloudHelper testHelper = new CloudHelper();

    @Test
    void handleResponseTest() {
        HttpRequestMessage<Optional<String>> request = mock(HttpRequestMessage.class);

        doAnswer(invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new FakeResponseBuilder().status(status);
        }).when(request).createResponseBuilder(any(HttpStatus.class));

        Response testResponse = new Response(200, "success", null);
        HttpResponseMessage response = testHelper.handleResponse(testResponse, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void handleErrorExceptionTest() {
        HttpRequestMessage<Optional<String>> request = mock(HttpRequestMessage.class);

        doAnswer(invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new FakeResponseBuilder().status(status);
        }).when(request).createResponseBuilder(any(HttpStatus.class));

        HttpResponseMessage response = testHelper.handleError(request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

}
