/******************************************************************************
 * Filename    = CloudPost.java
 * Author      =
 * Product     = cloud-function-app
 * Project     = Comm-Uni-Cator
 * Description = Defines custom Azure Function App API for storing a record.
 *****************************************************************************/

package functionapp;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import datastructures.Entity;
import datastructures.Response;
import interfaces.IdbConnector;


/**
 * Azure Function App API for storing a record.
 */
public class CloudPost extends CloudHelper {
    /**
     * Handles HTTP POST requests to insert a record into the cloud database.
     *
     * @param request The incoming HTTP request containing the JSON body.
     * @param context The Azure Functions execution context for logging.
     * @return An HTTP response indicating success or failure of the operation.
     * @throws JsonProcessingException If JSON parsing fails for the input body.
     */

    @FunctionName("CloudPost")
    public HttpResponseMessage runCloudPost(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws JsonProcessingException {
        context.getLogger().info("Java HTTP trigger processed a request.");
        try {
            final String jsonBody = request.getBody().orElse("");
            final Entity entityRequest = getObjectMapper().readValue(jsonBody, Entity.class);

            final IdbConnector dbConnector = dbConnectorFactory.getDbConnector();
            final JsonNode data = dbConnector.postData(entityRequest);

            final Response response = new Response(200, "success", NullNode.getInstance());

            return handleResponse(response, request);

        } catch (Exception e) {
            return handleError(request);
        }
    }
}
