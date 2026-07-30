package com.musiccatalog.controller;

import com.musiccatalog.dto.AnalyticsResponse;
import com.musiccatalog.model.User;
import com.musiccatalog.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics(@AuthenticationPrincipal User user) {
        AnalyticsResponse analytics = analyticsService.getAnalytics(user.getId());
        return ResponseEntity.ok(analytics);
    }
}
