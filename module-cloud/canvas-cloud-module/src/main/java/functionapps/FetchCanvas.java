package functionapps;

import java.util.Optional;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import models.CanvasFetchResponse;
import models.CanvasFetchRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Azure Functions to load a canvas.
 * Takes CanvasFetchRequest as input and returns CanvasFetchResponse as output
 */
public class FetchCanvas {
    /**
     * Jackson ObjectMapper instance used for JSON serialization/deserialization.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTTP-triggered Azure Function for loading a canvas.
     *
     * @param request The HTTP request message containing JSON body of type CanvasFetchRequest.
     * @param context The execution context of the function.
     * @return HttpResponseMessage containing JSON serialized CanvasFetchResponse.
     */
    @FunctionName("FetchCanvas")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = HttpMethod.POST,
                    authLevel = AuthorizationLevel.FUNCTION,
                    dataType = "binary") final HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            final String jsonBody = request.getBody().orElse("");
            final CanvasFetchRequest canvasFetchRequest = objectMapper.readValue(jsonBody, CanvasFetchRequest.class);

            context.getLogger().info("Fetching JSON Object from CosmoDB");
            // Implementation to be done.

            context.getLogger().info("Fetching complete...");

            final CanvasFetchResponse canvasFetchResponse = new CanvasFetchResponse(new String[]{"Success"});
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(objectMapper.writeValueAsString(canvasFetchResponse))
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            final CanvasFetchResponse errorResponse = new CanvasFetchResponse(new String[]{"Success"});
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
    }
}
