package functionapps;

import java.util.Optional;
import java.util.List;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import models.ChatLoadResponse;
import models.ChatLoadRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.ChatMessageDTO;

/**
 * Azure Functions to load a chat.
 * Takes ChatLoadRequest as input and returns ChatLoadResponse as output
 */
public class LoadChat {
    /**
     * Jackson ObjectMapper instance used for JSON serialization/deserialization.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTTP-triggered Azure Function for loading a chat.
     *
     * @param request The HTTP request message containing JSON body of type ChatLoadRequest.
     * @param context The execution context of the function.
     * @return HttpResponseMessage containing JSON serialized ChatLoadResponse.
     */
    @FunctionName("LoadChat")
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
            final ChatLoadRequest chatLoadRequest = objectMapper.readValue(jsonBody, ChatLoadRequest.class);

            context.getLogger().info("Storing JSON Object in CosmoDB");
            // Implementation to be done.
            final List<ChatMessageDTO> chatMessages = List.of(new ChatMessageDTO("Test1", "Test Messages", "Test Attachments", chatLoadRequest.fromTimestamp()));
            context.getLogger().info("Stored JSON Object in CosmoDB");

            final ChatLoadResponse chatLoadResponse = new ChatLoadResponse(true, chatMessages, "Success");
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(objectMapper.writeValueAsString(chatLoadResponse))
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            final ChatLoadResponse errorResponse = new ChatLoadResponse(false, List.of(), "Success");
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
