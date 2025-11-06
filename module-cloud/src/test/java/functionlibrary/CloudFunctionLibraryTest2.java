package test;

import datastructures.Entity;
import datastructures.Response;
import datastructures.TimeRange;
import functionlibrary.CloudFunctionLibrary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CloudFunctionLibraryTest2{

    public static void main(String[] args) throws Exception {
        CloudFunctionLibrary cloudLib = new CloudFunctionLibrary();
        ObjectMapper mapper = new ObjectMapper();

        // Step 1️⃣: Create test data (key1, key2, key3)
        ObjectNode data = mapper.createObjectNode();
        data.put("key1", 1);
        data.put("key2", 2);
        data.put("key3", 3);

        Entity entity = new Entity(
                "TestModule",
                "ai",
                "credentials",
                "credential1",
                -1,
                new TimeRange(0, 0),
                data
        );

        System.out.println("====== CREATE ======");
        Response createRes = cloudLib.cloudCreate(entity);
        System.out.println(createRes);

        System.out.println("====== GET ======");
        Response getRes = cloudLib.cloudGet(entity);
        System.out.println(getRes);

        System.out.println("====== UPDATE ======");
        // Update data
        ObjectNode updatedData = mapper.createObjectNode();
        updatedData.put("key1", 100);
        updatedData.put("key2", 200);
        updatedData.put("key3", 300);

        Entity updatedEntity = new Entity(
                "TestModule",
                "ai",
                "credentials",
                "credential1",
                -1,
                new TimeRange(0, 0),
                updatedData
        );

        Response updateRes = cloudLib.cloudUpdate(updatedEntity);
        System.out.println(updateRes);

        System.out.println("====== GET AFTER UPDATE ======");
        Response getAfterUpdateRes = cloudLib.cloudGet(entity);
        System.out.println(getAfterUpdateRes);

        System.out.println("====== DELETE ======");
        Response deleteRes = cloudLib.cloudDelete(entity);
        System.out.println(deleteRes);
    }
}
