/******************************************************************************
 * Filename    = IdbConnector.java
 * Author      =
 * Product     = cloud-function-app
 * Project     = Comm-Uni-Cator
 * Description = Defines an interface for database operations in the cloud module.
 *****************************************************************************/

package interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import datastructures.Entity;

/**
 * Interface for performing CRUD operations on cloud database entities.
 */
public interface IdbConnector {
    JsonNode getData(Entity request);

    JsonNode postData(Entity request);

    JsonNode createData(Entity request);

    JsonNode deleteData(Entity request);

    JsonNode updateData(Entity request);
}
