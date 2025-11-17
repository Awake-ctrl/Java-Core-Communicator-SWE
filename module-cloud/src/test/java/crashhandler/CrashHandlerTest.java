package crashhandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrashHandlerTest {

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
//        Thread.sleep(10000);
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

        Thread.sleep(10000);
    }
}