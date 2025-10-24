/******************************************************************************
 * Filename    = DbConnectorFactory.java
 * Author      = Nikhil S Thomas
 * Product     = cloud-function-app
 * Project     = Comm-Uni-Cator
 * Description = Defines a factory class  for providing an instance of the
 *               database connector used for cloud database operations.
 *****************************************************************************/

package cosmosoperations;

import interfaces.IdbConnector;

/**
 * Factory class for creating and managing instances of {@link IdbConnector}.
 */
public class DbConnectorFactory {

    /** Singleton instance of the database connector. */
    private static IdbConnector dbConnector;

    /**
     * Returns a shared instance of the database connector.
     * If the connector is not yet initialized, it creates a new one,
     * initializes it, and returns it.
     *
     * @return The initialized {@link IdbConnector} instance.
     */
    public static IdbConnector getDbConnector() {
        if (dbConnector == null) {
            dbConnector = new MockDbConnector();
            dbConnector.init();
        }
        return dbConnector;
    }
}
