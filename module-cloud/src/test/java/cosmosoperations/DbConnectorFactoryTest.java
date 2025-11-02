package cosmosoperations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DbConnectorFactoryTest {
    @BeforeEach
    void resetFactory() {
        DbConnectorFactory.resetInstance();
    }

    @Test
    void getDefaultDbConnector() {
        assertInstanceOf(MockDbConnector.class, DbConnectorFactory.getDbConnector("mock"));
        assertInstanceOf(MockDbConnector.class, DbConnectorFactory.getDbConnector(""));
    }

    @Test
    void getCosmoDbConnector() {
        assertInstanceOf(CosmosOperations.class, DbConnectorFactory.getDbConnector("cosmo"));
    }
}