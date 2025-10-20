package cloudclasses;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import com.fasterxml.jackson.databind.ObjectMapper;
import datastructures.Entity;
import datastructures.Response;


/**
 * Azure Functions with HTTP Trigger for cloud module.
 */
public class CloudFunctionApp {
    /**
     * Jackson ObjectMapper instance used for JSON serialization/deserialization.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    private HttpResponseMessage HandleError(HttpRequestMessage<Optional<String>> request) {
        final Response errorResponse = new Response(400, "bad request", NullNode.getInstance());
        try {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(objectMapper.writeValueAsString(errorResponse))
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception ex) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid request")
                    .build();
        }
    }

    private HttpResponseMessage HandleResponse(Response response, HttpRequestMessage<Optional<String>> request) {
        try {
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(objectMapper.writeValueAsString(response))
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            return HandleError(request);
        }
    }

    /**
     * This function listens at endpoint "/api/CloudFunctionApp". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/CloudFunctionApp
     * 2. curl {your host}/api/CloudFunctionApp?name=HTTP%20Query
     */
    @FunctionName("CloudGet")
    public HttpResponseMessage runCloudGet(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws JsonProcessingException {
        context.getLogger().info("Java HTTP trigger processed a request.");
        try {
            final String jsonBody = request.getBody().orElse("");
            final Entity entityRequest = objectMapper.readValue(jsonBody, Entity.class);

            // Implementation

            final JsonNode data = objectMapper.readTree("{\"data\": \"Testdata\"}");
            final Response response = new Response(200, "success", data);

            return HandleResponse(response, request);
        } catch (Exception e) {
            return HandleError(request);
        }
    }

    @FunctionName("CloudPost")
    public HttpResponseMessage runCloudPost(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws JsonProcessingException {
        context.getLogger().info("Java HTTP trigger processed a request.");
        try {
            final String jsonBody = request.getBody().orElse("");
            final Entity entityRequest = objectMapper.readValue(jsonBody, Entity.class);

            // Implementation

            final Response response = new Response(200, "success", NullNode.getInstance());

            return HandleResponse(response, request);

        } catch (Exception e) {
            return HandleError(request);
        }
    }
}
