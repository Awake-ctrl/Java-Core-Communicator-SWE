package crashhandler;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;


/**
 * Object which is the Azure-OpenAI client.
 * Used to provide insights from exceptions.
 */
class InsightProvider {

    /** OpenAI client. */
    private final Client client;

    /** Deployment model. */
    private final String deploymentModel = "gemini-2.5-flash";

    /** Constructor for Insight Provider. */
    InsightProvider() {
        this.client = new Client();
    }

    /** Function to get the AI insights on crashes and exceptions.
     * @param crashData CrashData from cosmosDB.
     * @return AI Response.
     */
    public String getInsights(final String crashData) {

        GenerateContentResponse response = null;
        try {
            response = client.models.generateContent(
                    deploymentModel,
                    "Analyze this crash/exception:" + crashData,
                    null
            );
        } catch (Exception e) {
            return "No response, NOJOY" + e.getMessage();
        }

        return response.text();
    }
}
