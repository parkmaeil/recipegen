package com.example.openaiImage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouteController {

    @GetMapping("/recipeview")
    public String recipeview(){
        return "recipe";
    }
}
