package crashhandler;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
//import com.azure.core.credential.AzureKeyCredential;

import java.util.List;

/**
 * Object which is the Azure-OpenAI client.
 * Used to provide insights from exceptions.
 */
class InsightProvider {

    /** OpenAI client. */
    private final OpenAIClient client;

    /** Endpoint for the azure AI. */
    private final String aiEndPoint = "https://communicate.openai.azure.com/";

    /** API Key for Azure AI. */

    /** Deployment model. */
    private final String deploymentModel = "gpt-4o";

    /** Constructor for Insight Provider. */
    InsightProvider() {
        this.client = new OpenAIClientBuilder()
                .endpoint(aiEndPoint)
//                .credential(new AzureKeyCredential()) -> provide key in cred.
                .buildClient();
    }

    /** Function to get the AI insights on crashes and exceptions.
     * @param crashData CrashData from cosmosDB.
     * @return AI Response.
     */
    public String getInsights(final String crashData) {
        final var messages = List.of(
                new ChatRequestSystemMessage("""
                    This is a crash analyzer."""),
                new ChatRequestUserMessage("""
                    Analyze what went wrong:
                    %s""".formatted(crashData))
        );

        final var chatOptions = new ChatCompletionsOptions(messages);
        final ChatCompletions response;
        try {
            response = client.getChatCompletions(deploymentModel, chatOptions);
        } catch (Exception e) {
            return "Clould not fetch analysis, no-joy" + e.getMessage();
        }

        return response.getChoices().get(0).getMessage().getContent();

    }
}
