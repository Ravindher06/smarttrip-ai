package com.example.smarttrip.controller;

import com.example.smarttrip.model.TripItinerary;
import com.example.smarttrip.service.TravelAgentWorkflow;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trip")
public class TripController {

    private final TravelAgentWorkflow workflowService;

    public TripController(TravelAgentWorkflow workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping("/plan")
    public ResponseEntity<TripItinerary> planTrip(
            @RequestParam String destination,
            @RequestParam int budget,
            @RequestParam int duration) {
        if (duration < 1 || duration > 30 || budget < 1) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(workflowService.runWorkflow(destination, budget, duration));
    }
}
