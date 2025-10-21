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
import models.CanvasUpdateRequest;
import models.CanvasUpdateResponse;

/**
 * Azure Functions to update a canvas.
 * Takes CanvasUpdateRequest as input and returns CanvasUpdateResponse as output
 */
public class UpdateCanvas {
    /**
     * Jackson ObjectMapper instance used for JSON serialization/deserialization.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTTP-triggered Azure Function for creating a canvas.
     *
     * @param request The HTTP request message containing JSON body of type CanvasUpdateRequest.
     * @param context The execution context of the function.
     * @return HttpResponseMessage containing JSON serialized CanvasUpdateResponse.
     */
    @FunctionName("UpdateCanvas")
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
            final CanvasUpdateRequest canvasUpdateRequest = objectMapper.readValue(jsonBody, CanvasUpdateRequest.class);

            context.getLogger().info("Updating JSON Object in CosmoDB");
            // Implementation to be done.
            context.getLogger().info("Updating complete...");

            final CanvasUpdateResponse canvasUpdateResponse = new CanvasUpdateResponse("true");
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(objectMapper.writeValueAsString(canvasUpdateResponse))
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            final CanvasUpdateResponse errorResponse = new CanvasUpdateResponse("false");
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
