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

import models.ChatNewResponse;
import models.ChatNewRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Azure Functions to create new chat.
 * Takes ChatNewRequest as input and returns ChatNewResponse as output
 */
public class NewChat {
    /**
     * Jackson ObjectMapper instance used for JSON serialization/deserialization.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTTP-triggered Azure Function for creating a chat.
     *
     * @param request The HTTP request message containing JSON body of type ChatNewRequest.
     * @param context The execution context of the function.
     * @return HttpResponseMessage containing JSON serialized ChatNewResponse.
     */
    @FunctionName("NewChat")
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
            final ChatNewRequest chatNewRequest = objectMapper.readValue(jsonBody, ChatNewRequest.class);

            context.getLogger().info("Storing JSON Object in CosmoDB");
            // Implementation to be done.
            context.getLogger().info("Stored JSON Object in CosmoDB");

            final ChatNewResponse chatNewResponse = new ChatNewResponse(true, "Success");
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(objectMapper.writeValueAsString(chatNewResponse))
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            final ChatNewResponse errorResponse = new ChatNewResponse(false, "Invalid request");
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
