package atmos.app;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AtmosAppApi {
    public static JSONObject getWeatherData(String locationName) {
        // Substitui espaços por "+" no nome da cidade
        locationName = locationName.replace(" ", "+");

        // Chave de API do OpenWeatherMap
        String apiKey = "c9ef07505151747c033064bcfb80c6ac";

        // URL da API com o nome da cidade e chave de API
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + locationName +
                "&appid=" + apiKey + "&units=metric&lang=pt";

        try {
            // Conecta à API
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Verifica se a resposta foi bem-sucedida
            if (conn.getResponseCode() != 200) {
                System.out.println("Erro: Não foi possível conectar à API, código de resposta: " + conn.getResponseCode());
                return null;
            }

            // Armazena a resposta JSON da API
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            // Analisa o JSON da resposta
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());

            // Verifica se a resposta contém os campos esperados
            if (resultJsonObj.containsKey("main") && resultJsonObj.containsKey("weather") && resultJsonObj.containsKey("wind")) {
                // Extração das informações
                JSONObject main = (JSONObject) resultJsonObj.get("main");

                // Tratamento de temperatura e umidade para evitar ClassCastException
                Object tempObject = main.get("temp");
                double temperature = tempObject instanceof Number ? ((Number) tempObject).doubleValue() : 0.0;

                Object humidityObject = main.get("humidity");
                long humidity = humidityObject instanceof Number ? ((Number) humidityObject).longValue() : 0L;

                // Acessando o array 'weather' e pegando a primeira condição
                JSONArray weatherArray = (JSONArray) resultJsonObj.get("weather");
                JSONObject weatherObject = (JSONObject) weatherArray.get(0); // Acessa o primeiro item do array
                String weatherCondition = (String) weatherObject.get("description");

                JSONObject wind = (JSONObject) resultJsonObj.get("wind");
                // Verificar se o campo windSpeed está presente e se tem valor válido
                Object windSpeedObject = wind.get("speed");
                double windSpeed = (windSpeedObject instanceof Number) ? ((Number) windSpeedObject).doubleValue() : 0.0;

                // Organiza os dados em um JSONObject
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_condition", weatherCondition);
                weatherData.put("humidity", humidity); // A umidade agora está sendo passada corretamente
                weatherData.put("windspeed", windSpeed); // Valor do vento garantido

                return weatherData;
            } else {
                System.out.println("Erro: A resposta da API não contém os campos esperados.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao processar os dados da API: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // Cria a conexão
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Define o método de requisição como GET
            conn.setRequestMethod("GET");

            // Conecta à API
            conn.connect();
            return conn;
        } catch (IOException e) {
            System.out.println("Erro ao conectar à API: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
