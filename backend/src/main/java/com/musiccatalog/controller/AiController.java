package com.musiccatalog.controller;

import com.musiccatalog.model.User;
import com.musiccatalog.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations(@AuthenticationPrincipal User user) {
        Map<String, Object> recommendations = aiService.getRecommendations(user.getId());
        return ResponseEntity.ok(recommendations);
    }
}
