package crashhandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import datastructures.Entity;
import datastructures.Response;
import functionlibrary.CloudFunctionLibrary;
import interfaces.ICrashHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

/**
 * Class which handles logging and storing call stack and other exception
 * details in the application.
 */
public class CrashHandler implements ICrashHandler {

    /** Variable to prevent redundant usage of setDefaultUncaughtExceptionHandler. */
    private static boolean isCreated = false;

    /** Collection to which the logs get stored. */
    private static final String COLLECTION = "Exception";

    /** The Id field which is required while storing data. */
    private static int exceptionId = 1;

    /**
     * Function which starts the exception handler and handles the logging logic.
     */
    public synchronized void startCrashHandler() {
        // Exit if already set to prevent multiple usage.
        if (isCreated) {
            return;
        }

        isCreated = true;

        final CloudFunctionLibrary cloudFunctionLibrary = new CloudFunctionLibrary();
//        final InsightProvider insightProvider = new InsightProvider();

        try {
            final Response responseCreate = cloudFunctionLibrary.cloudCreate(new Entity("CLOUD", "ExceptionLogs", null, null, -1, null, null));
            final Response responseGet = cloudFunctionLibrary.cloudGet(new Entity("CLOUD", "ExceptionLogs", null, null, 1, null, null));
            exceptionId = responseGet.data().get(0).get("id").asInt();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            final String exceptionName = exception.getClass().getName();
            final String timestamp = Instant.now().toString();
            final String exceptionMessage = exception.getMessage();
            final String exceptionString = exceptionToString(exception);
            final String stackJoined = stackToSingleString(exception.getStackTrace());

            final JsonNode exceptionJsonNode = toJsonNode(exceptionName, timestamp, exceptionMessage, exceptionString, stackJoined);

            final Entity exceptionEntity = new Entity(
                    "CLOUD",
                    "ExceptionLogs",
                    Integer.toString(++exceptionId),
                    null,
                    -1,
                    null,
                    exceptionJsonNode
            );

            try {
                final Response responsePost = cloudFunctionLibrary.cloudPost(exceptionEntity);
//              final String response = insightProvider.getInsights(exceptionEntity.toString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static String exceptionToString(final Throwable ex) {
        final StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static String stackToSingleString(final StackTraceElement[] elements) {
        final StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : elements) {
            sb.append(e).append("\n");
        }
        return sb.toString();
    }

    private JsonNode toJsonNode(final String eName, final String timeStamp, final String eMsg, final String eDetails, final String eTrace) {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode jsonNode = mapper.createObjectNode()
                .put("ExceptionName", eName)
                .put("TimeStampUtc", timeStamp)
                .put("ExceptionMessage", eMsg)
                .put("ExceptionDetails", eDetails)
                .put("StackTrace", eTrace);

        return jsonNode;
    }
}