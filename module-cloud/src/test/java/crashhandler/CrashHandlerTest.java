package crashhandler;

import datastructures.Entity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CrashHandlerTest {

    Entity testEntity = new Entity("CLOUD", "TestFailure", null, null, -1, null, null);

    @Test
    void testSingleton() {
        CrashHandler testCrashHandler = new CrashHandler();

        testCrashHandler.startCrashHandler();
        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();

        testCrashHandler.startCrashHandler();
        Thread.UncaughtExceptionHandler duplicateHandler = Thread.getDefaultUncaughtExceptionHandler();

        assertSame(originalHandler, duplicateHandler);
    }

    @Test
    void testStartCrashHandler() throws InterruptedException {
        CrashHandler testCrashHandler = new CrashHandler();

        testCrashHandler.startCrashHandler();
        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread crashingTread = new Thread(() -> {
            throw new RuntimeException("Intentional Crashing...");
        }, "crashingTestThread");
        crashingTread.start();
        crashingTread.join();
    }

    @Test
    void testMultipleCrashes() throws InterruptedException {
        CrashHandler testCrashHandler = new CrashHandler();

        testCrashHandler.startCrashHandler();
        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread crashingTreadMain = new Thread(() -> {
            throw new RuntimeException("Intentional Crashing...");
        }, "crashingTestThreadMain");

        Thread crashingTreadSecondary = new Thread(() -> {
            throw new ArrayStoreException("Yet another intentional crash...");
        }, "crashingTestThreadDuplicate");

        crashingTreadMain.start();
        crashingTreadSecondary.start();
        crashingTreadMain.join();
        crashingTreadSecondary.join();

    }

//    @Test
//    void testCloudCreateFailure() throws InterruptedException, IOException {
//
//        CloudFunctionLibrary mockCloudFunctionLibrary = Mockito.mock(CloudFunctionLibrary.class);
//        when(mockCloudFunctionLibrary.cloudCreate(testEntity)).thenReturn(new CloudResponse(400, "Failure Testing", null));
//
//        CrashHandler testCrashHandler = new CrashHandler();
//
//        testCrashHandler.startCrashHandler();
//        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
//    }
}