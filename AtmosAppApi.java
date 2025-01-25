import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AtmosAppApi {

    private static final String API_KEY = "c9ef07505151747c033064bcfb80c6ac"; // Chave de API do OpenWeatherMap
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String UV_URL = "https://api.openweathermap.org/data/2.5/uvi"; // Novo endpoint para índice UV

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite o nome da cidade: ");
        String locationName = sc.nextLine();

        JSONObject weatherData = getWeatherData(locationName);
        if (weatherData != null) {
            // Exibe as informações detalhadas de clima e previsão
            System.out.println("Informações de clima para " + weatherData.get("location") + ":");
            System.out.println("Temperatura atual: " + weatherData.get("temperature") + "°C");
            System.out.println("Sensação térmica: " + weatherData.get("feels_like") + "°C");
            System.out.println("Condição do tempo: " + weatherData.get("weather_condition"));
            System.out.println("Umidade: " + weatherData.get("humidity") + "%");
            System.out.println("Velocidade do vento: " + weatherData.get("windspeed") + " km/h");
            System.out.println("Temperatura máxima: " + weatherData.get("temp_max") + "°C");
            System.out.println("Temperatura mínima: " + weatherData.get("temp_min") + "°C");
            System.out.println("Pressão atmosférica: " + weatherData.get("pressure") + " hPa");

            // Exibe a previsão para os próximos dias
            JSONArray forecast = (JSONArray) weatherData.get("forecast");
            System.out.println("\nPrevisão para os próximos dias:");
            for (Object day : forecast) {
                JSONObject dayForecast = (JSONObject) day;
                System.out.println(dayForecast.get("dia") + ": Max " + dayForecast.get("temp_max") + "°C, Min " + dayForecast.get("temp_min") + "°C");
            }

            // Exibe a previsão para hoje (manhã, tarde e noite)
            JSONObject todayForecast = (JSONObject) weatherData.get("today_forecast");
            System.out.println("\nPrevisão para hoje:");
            System.out.println("Manhã: " + todayForecast.get("manhã") + "°C");
            System.out.println("Tarde: " + todayForecast.get("tarde") + "°C");
            System.out.println("Noite: " + todayForecast.get("noite") + "°C");

            // Exibe a informação do índice UV
            System.out.println("\nÍndice UV: " + weatherData.get("uv_index"));
        } else {
            System.out.println("Não foi possível obter os dados da cidade.");
        }
    }

    public static JSONObject getWeatherData(String locationName) {
        if (locationName == null || locationName.trim().isEmpty()) {
            System.out.println("Erro: O nome da cidade não pode ser vazio.");
            return null;
        }

        try {
            // Codifica o nome da cidade para evitar problemas com espaços e caracteres especiais
            String encodedLocation = URLEncoder.encode(locationName.trim(), StandardCharsets.UTF_8.toString());
            String urlString = BASE_URL + "?q=" + encodedLocation + "&appid=" + API_KEY + "&units=metric&lang=pt";

            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Erro: Não foi possível conectar à API. Código de resposta: " +
                        (conn != null ? conn.getResponseCode() : "N/A"));
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
            }

            // Exibe o JSON completo retornado para ajudar na depuração
            System.out.println("JSON Retornado pela API: " + resultJson);

            // Valida se o JSON retornado não está vazio
            if (resultJson.length() == 0) {
                System.out.println("Erro: Resposta da API está vazia.");
                return null;
            }

            // Analisa o JSON
            JSONObject weatherData = parseWeatherJson(resultJson.toString());
            if (weatherData == null) {
                System.out.println("Erro: Cidade não encontrada ou erro ao analisar os dados.");
                return null;
            }

            // Adiciona o índice UV
            JSONObject uvData = getUvIndex((Double) weatherData.get("latitude"), (Double) weatherData.get("longitude"));
            if (uvData != null) {
                weatherData.put("uv_index", uvData.get("value"));
            }

            return weatherData;

        } catch (Exception e) {
            System.out.println("Erro ao processar os dados da API: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            System.out.println("Erro ao conectar à API: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject parseWeatherJson(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(json);

            // Validação de mensagem de erro
            if (jsonObj.containsKey("message") && "city not found".equals(jsonObj.get("message"))) {
                return null;
            }

            JSONObject main = (JSONObject) jsonObj.get("main");
            JSONArray weatherArray = (JSONArray) jsonObj.get("weather");
            JSONObject wind = jsonObj.containsKey("wind") ? (JSONObject) jsonObj.get("wind") : null;

            // Coleta e validação de dados
            double temperature = main.containsKey("temp") ? ((Number) main.get("temp")).doubleValue() : 0.0;
            double tempMax = main.containsKey("temp_max") ? ((Number) main.get("temp_max")).doubleValue() : 0.0;
            double tempMin = main.containsKey("temp_min") ? ((Number) main.get("temp_min")).doubleValue() : 0.0;
            double feelsLike = main.containsKey("feels_like") ? ((Number) main.get("feels_like")).doubleValue() : 0.0;  // Sensação térmica
            long humidity = main.containsKey("humidity") ? ((Number) main.get("humidity")).longValue() : 0;
            double pressure = main.containsKey("pressure") ? ((Number) main.get("pressure")).doubleValue() : 0.0;

            String weatherCondition = weatherArray != null && !weatherArray.isEmpty()
                    ? (String) ((JSONObject) weatherArray.get(0)).get("description")
                    : "Não disponível";

            double windSpeed = wind != null && wind.containsKey("speed") ? ((Number) wind.get("speed")).doubleValue() : 0.0;

            JSONObject coordinates = jsonObj.containsKey("coord") ? (JSONObject) jsonObj.get("coord") : null;
            double lat = coordinates != null && coordinates.containsKey("lat") ? ((Number) coordinates.get("lat")).doubleValue() : 0.0;
            double lon = coordinates != null && coordinates.containsKey("lon") ? ((Number) coordinates.get("lon")).doubleValue() : 0.0;

            // Monta os dados para retorno
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("feels_like", feelsLike);  // Adiciona sensação térmica
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windSpeed);
            weatherData.put("temp_max", tempMax);
            weatherData.put("temp_min", tempMin);
            weatherData.put("pressure", pressure);
            weatherData.put("latitude", lat);
            weatherData.put("longitude", lon);

            // Modifica para retornar "clima em cidade, estado"
            String city = (String) jsonObj.get("name");
            JSONObject sys = (JSONObject) jsonObj.get("sys");

            // Aqui está a correção para pegar o estado corretamente
            String state = sys.containsKey("state") ? (String) sys.get("state") : (String) sys.get("country");
            weatherData.put("location", city + ", " + (state != null ? state : "Desconhecido")); // Exibe "Cidade, Estado"

            // Simula previsão futura com dias da semana e data
            JSONArray forecast = new JSONArray();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
            for (int i = 0; i < 4; i++) {  // Agora são 3 dias após o dia de hoje
                JSONObject dayData = new JSONObject();
                LocalDate date = LocalDate.now().plusDays(i);
                DayOfWeek dayOfWeek = date.getDayOfWeek();

                // Tradução dos dias para o português
                String dayName = "";
                switch (dayOfWeek) {
                    case MONDAY: dayName = "Segunda-feira"; break;
                    case TUESDAY: dayName = "Terça-feira"; break;
                    case WEDNESDAY: dayName = "Quarta-feira"; break;
                    case THURSDAY: dayName = "Quinta-feira"; break;
                    case FRIDAY: dayName = "Sexta-feira"; break;
                    case SATURDAY: dayName = "Sábado"; break;
                    case SUNDAY: dayName = "Domingo"; break;
                }

                // Previsão de dias
                dayData.put("dia", dayName);
                dayData.put("temp_max", 30 + i);
                dayData.put("temp_min", 22 + i);
                forecast.add(dayData);
            }

            weatherData.put("forecast", forecast);

            // Previsão para hoje (manhã, tarde, noite)
            JSONObject todayForecast = new JSONObject();
            todayForecast.put("manhã", 25);
            todayForecast.put("tarde", 28);
            todayForecast.put("noite", 22);
            weatherData.put("today_forecast", todayForecast);

            return weatherData;
        } catch (ParseException e) {
            System.out.println("Erro ao analisar os dados JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject getUvIndex(double latitude, double longitude) {
        try {
            String urlString = UV_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
            }

            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(resultJson.toString());
        } catch (Exception e) {
            System.out.println("Erro ao obter índice UV: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
