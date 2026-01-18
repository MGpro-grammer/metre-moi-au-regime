package be.esi.prj.service;

import be.esi.prj.dto.FoodDto;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This service connects to OpenFoodFacts to get food information.
 * It can search for food using EAN codes or food names from the internet.
 */
public class OpenFoodFactsService {
    private static final String API_BASE_URL_EANCODE = "https://world.openfoodfacts.net/api/v2/";
    private static final String API_BASE_URL_NAME = "https://world.openfoodfacts.org/cgi/";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String FIELDS = "product_name,code,nutriments,serving_quantity,product_quantity,nutrition_data_per";

    /**
     * Searches for food using its EAN code (barcode number).
     * Connects to OpenFoodFacts website to get the food information.
     * @param eanCode the barcode number to search for
     * @return the food information if found, or empty if not found
     * @throws Exception if there is a problem connecting to the internet
     */
    public static Optional<FoodDto> findFoodByEanCode(String eanCode) throws Exception {
        URI uri = new URI(API_BASE_URL_EANCODE + "product/" + eanCode + "?fields=" + FIELDS);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 || response.body().contains("\"status\":0")) {
            return Optional.empty();
        }

        String productJson = getJsonObject(response.body(), "product");
        return Optional.ofNullable(parseProductFromJson(productJson));
    }

    /**
     * Searches for food using its name.
     * Gets a list of foods that match the search term from OpenFoodFacts.
     * @param name the name of the food to search for
     * @param pageSize how many results to get per page
     * @param page which page of results to get
     * @return a list of foods that match the search name
     * @throws Exception if there is a problem connecting to the internet
     */
    public static List<FoodDto> findFoodByName(String name, int pageSize, int page) throws Exception {
        String pageSizeParam = "&page_size=" + (pageSize > 0 ? String.valueOf(pageSize) : "20");
        String pageParam = "&page=" + (page > 0 ? String.valueOf(page) : "1");
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        URI uri = new URI(API_BASE_URL_NAME + "search.pl?search_terms=" + encodedName + "&search_simple=1&action=process&json=1&fields=" + FIELDS + pageSizeParam + pageParam);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return Collections.emptyList();
        }

        return parseProductListResponse(response.body());
    }

    /**
     * Converts the response from OpenFoodFacts into a list of food objects.
     * Reads the JSON data and creates FoodDto objects for each product.
     * @param responseBody the JSON response from OpenFoodFacts
     * @return a list of food objects
     */
    private static List<FoodDto> parseProductListResponse(String responseBody) {
        List<FoodDto> foodList = new ArrayList<>();
        String arrayContent = getJsonArrayContent(responseBody, "products");

        if (arrayContent == null) {
            return foodList;
        }

        int balance = 0;
        int start = 0;
        for (int i = 0; i < arrayContent.length(); i++) {
            if (arrayContent.charAt(i) == '{') {
                if (balance == 0) start = i;
                balance++;
            } else if (arrayContent.charAt(i) == '}') {
                balance--;
                if (balance == 0) {
                    foodList.add(parseProductFromJson(arrayContent.substring(start, i + 1)));
                }
            }
        }
        return foodList;
    }

    /**
     * Converts JSON data about one product into a FoodDto object.
     * Extracts the name, code, and nutritional values from the JSON.
     * @param productJson the JSON data for one product
     * @return a FoodDto object with the product information
     */
    private static FoodDto parseProductFromJson(String productJson) {
        if (productJson == null) {
            return null;
        }
        String nutrimentsJson = getJsonObject(productJson, "nutriments");
        String productName = getStringValue(productJson, "product_name");
        String unit = deduceUnit(productName);

        return new FoodDto(
                -1,
                productName,
                getStringValue(productJson, "code"),
                unit,
                safeParseDouble(getStringValue(nutrimentsJson, "energy-kcal_100g")),
                safeParseDouble(getStringValue(nutrimentsJson, "proteins_100g")),
                safeParseDouble(getStringValue(nutrimentsJson, "fat_100g")),
                safeParseDouble(getStringValue(nutrimentsJson, "carbohydrates_100g")),
                safeParseDouble(getStringValue(productJson, "serving_quantity")),
                extractNumericValue(getStringValue(productJson, "product_quantity"))
        );
    }

    /**
     * Guesses what unit to use for the food (grams or milliliters).
     * Looks at the product name to see if it's a liquid or solid food.
     * @param productName the name of the product
     * @return "ml" for liquids, "g" for solids
     */
    private static String deduceUnit(String productName) {
        if (productName == null) {
            return "g";
        }
        String regex = "(\\d+\\s?ml|\\d+\\s?cl|\\d+\\s?l)";
        return productName.toLowerCase().matches(".*" + regex + ".*") ? "ml" : "g";
    }

    /**
     * Gets a text value from JSON data using a key.
     * Searches for the key and returns the text that comes after it.
     * @param json the JSON text to search in
     * @param key the name of the value to find
     * @return the text value, or null if not found
     */
    private static String getStringValue(String json, String key) {
        if (json == null) return null;
        String search = "\"" + key + "\":";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1) return null;

        int start = keyIndex + search.length();
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;

        if (start < json.length() && json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            if (end != -1) {
                return json.substring(start + 1, end);
            }
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            String value = json.substring(start, end).trim();
            return value.equals("null") ? null : value;
        }
        return null;
    }

    /**
     * Gets a JSON object from inside another JSON using a key.
     * Finds the object that starts with { and ends with }.
     * @param json the JSON text to search in
     * @param key the name of the object to find
     * @return the JSON object as text
     */
    private static String getJsonObject(String json, String key) {
        return getJsonBlock(json, key, '{', '}');
    }

    /**
     * Gets the content inside a JSON array.
     * Finds the array and returns what's between [ and ].
     * @param json the JSON text to search in
     * @param key the name of the array to find
     * @return the content inside the array
     */
    private static String getJsonArrayContent(String json, String key) {
        String block = getJsonBlock(json, key, '[', ']');
        if (block != null && block.length() > 1) {
            return block.substring(1, block.length() - 1);
        }
        return null;
    }

    /**
     * Gets a block of JSON that starts and ends with specific characters.
     * Finds balanced pairs of start and end characters.
     * @param json the JSON text to search in
     * @param key the name of the block to find
     * @param startChar the character that starts the block
     * @param endChar the character that ends the block
     * @return the complete JSON block
     */
    private static String getJsonBlock(String json, String key, char startChar, char endChar) {
        if (json == null) {
            return null;
        }
        int keyIndex = json.indexOf("\"" + key + "\":");
        if (keyIndex == -1) {
            return null;
        }
        int startIndex = json.indexOf(startChar, keyIndex);
        if (startIndex == -1) {
            return null;
        }
        int balance = 1;
        for (int i = startIndex + 1; i < json.length(); i++) {
            if (json.charAt(i) == startChar) balance++;
            else if (json.charAt(i) == endChar) balance--;
            if (balance == 0) {
                return json.substring(startIndex, i + 1);
            }
        }
        return null;
    }

    /**
     * Converts a text number to a Double safely.
     * Returns 0.0 if the text is not a valid number.
     * @param value the text that should contain a number
     * @return the number as Double, or 0.0 if invalid
     */
    private static Double safeParseDouble(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Finds the first number in a text.
     * Looks for digits and decimal points to extract a number.
     * @param text the text that contains a number
     * @return the first number found, or 0.0 if no number found
     */
    private static Double extractNumericValue(String text) {
        if (text == null) {
            return 0.0;
        }
        Matcher matcher = Pattern.compile("(\\d*\\.?\\d+)").matcher(text);
        return matcher.find() ? safeParseDouble(matcher.group(1)) : 0.0;
    }
}
