/******************************************************************************
 * Filename    = CloudUpdate.java
 * Author      = Nikhil S Thomas
 * Product     = cloud-function-app
 * Project     = Comm-Uni-Cator
 * Description = Defines custom Azure Function App API for updating a record.
 *****************************************************************************/

package functionapp;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import cosmosoperations.DbConnectorFactory;
import datastructures.Entity;
import datastructures.Response;
import interfaces.IdbConnector;


/**
 * Azure Function App API for updating a record.
 */
public class CloudUpdate extends CloudHelper {
    /**
     * Handles HTTP PUT requests to update a record in the cloud database.
     *
     * @param request The incoming HTTP request containing the JSON body.
     * @param context The Azure Functions execution context for logging.
     * @return An HTTP response indicating success or failure of the operation.
     * @throws JsonProcessingException If JSON parsing fails for the input body.
     */
    @FunctionName("CloudUpdate")
    public HttpResponseMessage runCloudUpdate(
            @HttpTrigger(name = "req", methods = HttpMethod.PUT, authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws JsonProcessingException {
        context.getLogger().info("Java HTTP trigger processed a request.");
        try {
            final String jsonBody = request.getBody().orElse("");
            final Entity entityRequest = getObjectMapper().readValue(jsonBody, Entity.class);

            final IdbConnector dbConnector = DbConnectorFactory.getDbConnector("mock");
            final Response response = dbConnector.updateData(entityRequest);

            return handleResponse(response, request);

        } catch (Exception e) {
            return handleError(request);
        }
    }
}
