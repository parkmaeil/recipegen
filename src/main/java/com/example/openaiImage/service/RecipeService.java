package com.example.openaiImage.service;

import com.example.openaiImage.entity.Recipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class RecipeService {

    private final ChatModel chatModel;
    private final String googleApiKey = "Google API 키를 입력하세요";  // Google API 키를 입력하세요
    private final String googleCx = "Google Custom Search Engine CX 키를 입력하세요"; // Google Custom Search Engine CX 키를 입력하세요
    private final RestTemplate restTemplate = new RestTemplate();
    public RecipeService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }
    // 레시피 생성 메서드
    public String createRecipe(Recipe recipe) {
        String template = """
            제목: 요리 제목을 제공해 주세요.
            다음 재료를 사용하여 요리법을 만들고 싶습니다: {ingredients}.
            선호하는 요리 유형은 {cuisine}입니다.
            다음 식이 제한을 고려해 주세요: {dietaryRestrictions}.
            재료 목록과 조리법을 포함한 상세한 요리법을 제공해 주세요.
    """;
        PromptTemplate promptTemplate = new PromptTemplate(template);
        Map<String, Object> params = Map.of(
                "ingredients", recipe.getIngredients(), // 재료
                "cuisine", recipe.getCuisine(), // 요리
                "dietaryRestrictions", recipe.getDietaryRestrictions() // 식이제한
        );

        Prompt prompt = promptTemplate.create(params);
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    // Google Custom Search API를 사용하여 관련 URL 검색
    public List<String> searchRecipeUrls(String query) throws IOException {

        // API URI 생성
        URI apiUrl = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/customsearch/v1")
                .queryParam("key", googleApiKey)
                .queryParam("cx", googleCx)
                .queryParam("q", query)
                .build()
                .toUri();
        System.out.println(apiUrl.toString());
        // API 호출
        String response = restTemplate.getForObject(apiUrl, String.class);

        // JSON 응답 파싱
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        System.out.println(jsonResponse.toString());

        JsonArray itemsArray = jsonResponse.getAsJsonArray("items");

        // itemsArray가 null인지 확인
        List<String> urls = new ArrayList<>();
        if (itemsArray != null) {
            for (JsonElement item : itemsArray) {
                urls.add(item.getAsJsonObject().get("link").getAsString());
            }
        } else {
            System.out.println("No search results found for the query: " + query);
        }
        return urls;
    }
    // 레시피와 링크를 함께 제공하는 메서드
    public Map<String, Object> createRecipeWithUrls(Recipe recipe) throws IOException {
        String recipeContent = createRecipe(recipe); // 레시피 설명 생성
        List<String> urls = searchRecipeUrls(recipe.getIngredients()); // Google Custom Search API로 URL 검색
        return Map.of("recipe", recipeContent, "urls", urls);
    }
}

