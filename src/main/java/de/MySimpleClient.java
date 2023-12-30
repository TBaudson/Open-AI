package main.java.de;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MySimpleClient {
    private static final Logger logger = LoggerFactory.getLogger(MySimpleClient.class);
    private static final String OPENAI_API_KEY = "OPENAI_API_KEY";
    private static final String API_URL = "https://api.openai.com/v1/engines/text-davinci-003/completions";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public void startRequest() {
        String prompt = "Wie geht es dir?";

        Request request = createRequestForPrompt(prompt);

        try {
            String generatedText = sendQuestion(request);
            if (generatedText != null) {
                logger.info(generatedText);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while sending the question: " + e.getMessage());
        }
    }

    @NotNull
    private Request createRequestForPrompt(String prompt) {
        String apiKey = getApiKey();

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("prompt", prompt);
        jsonBody.addProperty("max_tokens", 100);
        String jsonBodyString = gson.toJson(jsonBody);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(jsonBodyString, mediaType);

        return new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    private String sendQuestion(Request request) throws IOException {
        String responseBody;
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                logger.error("Response failed !");
                return null;
            }

            responseBody = response.body().string();
        }

        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

        JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
        if (choicesArray != null && choicesArray.size() > 0) {
            JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();
            return firstChoice.get("text").getAsString();
        } else {
            logger.error("No choices found in the JSON response.");
            return null;
        }
    }

    private String getApiKey() {
        String variableValue = System.getenv(OPENAI_API_KEY);

        if (variableValue == null) {
            System.err.println("API key not found");
            System.exit(1);
        }

        logger.info("Der Wert der Variable " + OPENAI_API_KEY + " ist: " + variableValue);
        return variableValue;
    }
}
