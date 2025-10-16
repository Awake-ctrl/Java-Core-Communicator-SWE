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

import com.fasterxml.jackson.databind.ObjectMapper;
import models.CanvasCreateRequest;
import models.CanvasCreateResponse;

/**
 * Azure Functions to create new canvas.
 * Takes CanvasCreateRequest as input and returns CanvasCreateResponse as output
 */
public class CreateCanvas {
    /**
     * Jackson ObjectMapper instance used for JSON serialization/deserialization.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTTP-triggered Azure Function for creating a canvas.
     *
     * @param request The HTTP request message containing JSON body of type CanvasCreateRequest.
     * @param context The execution context of the function.
     * @return HttpResponseMessage containing JSON serialized CanvasCreateResponse.
     */
    @FunctionName("CreateCanvas")
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
            final CanvasCreateRequest canvasCreateRequest = objectMapper.readValue(jsonBody, CanvasCreateRequest.class);

            context.getLogger().info("Storing JSON Object in CosmoDB");
            // Implementation to be done.
            context.getLogger().info("Stored JSON Object in CosmoDB");

            final CanvasCreateResponse canvasCreateResponse = new CanvasCreateResponse(true);
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(objectMapper.writeValueAsString(canvasCreateResponse))
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            final CanvasCreateResponse errorResponse = new CanvasCreateResponse(false);
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
