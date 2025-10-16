package functionapps;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import models.RegisterRequest;
import models.RegisterResponse;

/**
 * Azure Function to create a chat.
 * Accepts ChatNewRequest as input and returns ChatNewResponse as output.
 */
public class Register {
    /**
     * Jackson ObjectMapper instance used for JSON serialization/deserialization.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTTP-triggered Azure Function for creating a chat.
     *
     * @param request The HTTP request message containing JSON body of ChatNewRequest.
     * @param context The execution context of the function.
     * @return HttpResponseMessage containing JSON serialized ChatNewResponse.
     */
    @FunctionName("Register")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = HttpMethod.POST,
                    authLevel = AuthorizationLevel.FUNCTION,
                    dataType = "binary") final HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            final String body = request.getBody().orElse("");
            final RegisterRequest registerRequest = objectMapper.readValue(body, RegisterRequest.class);

            // Implementation will go here

            final RegisterResponse response = new RegisterResponse(true, "xidar", "hi");

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(response))
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error parsing request: " + e.getMessage());
            final RegisterResponse response = new RegisterResponse(false, "xidar", "bad");
            try {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", "application/json")
                        .body(objectMapper.writeValueAsString(response))
                        .build();
            } catch (Exception ex) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Invalid request")
                        .build();
            }
        }
    }
}
