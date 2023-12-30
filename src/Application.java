import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

public class Application {

    public static final String OPENAI_API_KEY = "OPENAI_API_KEY";

    public static void main(String[] args) throws IOException {
        // Geben Sie den gewünschten Text ein, um eine Generierung anzufordern
        String prompt = "Wie geht es dir?";

        Request request = createRequestForPrompt(prompt);

        // Senden Sie die Anfrage und erhalten Sie die Antwort
        OkHttpClient client = new OkHttpClient();
        sendQuestion(client, request);
    }

    @NotNull
    private static Request createRequestForPrompt(String prompt) {
        // Setzen Sie Ihren OpenAI API-Schlüssel hier ein
        String apiKey = getApiKey();

        // THis is

        // Erstellen Sie eine Anfrage an die OpenAI API
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"prompt\": \"" + prompt + "\", \"max_tokens\": 100}");

        return new Request.Builder()
                .url("https://api.openai.com/v1/engines/text-davinci-003/completions")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    private static void sendQuestion(OkHttpClient client, Request request) throws IOException {
        String responseBody;
        try (Response response = client.newCall(request).execute()) {
            if(response == null  || response.body() == null) {
                System.out.println("Response failed !");
                return;
            }

            responseBody = response.body().string();
        }

        // Analysieren Sie die Antwort und drucken Sie den generierten Text
        JSONObject jsonObject = new JSONObject(responseBody);
        String generatedText = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text");
        System.out.println(generatedText);
    }

    private static String getApiKey() {
        String variableValue = System.getenv(OPENAI_API_KEY);

        if (variableValue != null) {
            System.out.println("Der Wert der Variable " + OPENAI_API_KEY + " ist: " + variableValue);
        } else {
            System.out.println("Die Variable " + OPENAI_API_KEY + " existiert nicht oder hat keinen Wert.");
        }

        return variableValue;
    }
}