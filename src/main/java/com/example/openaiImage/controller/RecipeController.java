package com.example.openaiImage.controller;

import com.example.openaiImage.entity.Recipe;
import com.example.openaiImage.service.RecipeService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/recipe")
    public Map<String, Object> recipe(@RequestBody Recipe recipe) throws IOException {
        return recipeService.createRecipeWithUrls(recipe);
    }
}
