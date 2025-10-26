package cosmosoperations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DbConnectorFactoryTest {

    @Test
    void getDefaultDbConnector() {
        assertInstanceOf(MockDbConnector.class, DbConnectorFactory.getDbConnector("mock"));
        assertInstanceOf(MockDbConnector.class, DbConnectorFactory.getDbConnector(""));
    }

    @Test
    void getCosmoDbConnector() {
//        assertInstanceOf(CosmosOperations.class, DbConnectorFactory.getDbConnector("cosmo"));
        assertInstanceOf(MockDbConnector.class, DbConnectorFactory.getDbConnector("comso"));
    }
}