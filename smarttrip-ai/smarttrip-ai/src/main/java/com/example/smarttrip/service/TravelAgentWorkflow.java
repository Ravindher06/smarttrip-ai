package com.example.smarttrip.service;

import com.example.smarttrip.model.TravelState;
import com.example.smarttrip.model.TripItinerary;
import jakarta.annotation.PostConstruct;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * Planner -> Budget validator -> (approve: END | reject: back to planner).
 * Each retry makes the mock plan cheaper; MAX_ATTEMPTS prevents endless loops.
 */
@Service
public class TravelAgentWorkflow {

    private static final int MAX_ATTEMPTS = 5;
    private static final double BASE_COST_PER_DAY = 450.0;
    private static final double COST_REDUCTION_PER_RETRY = 0.85;

    private CompiledGraph<TravelState> compiledGraph;

    @PostConstruct
    public void initGraph() throws GraphStateException {
        StateGraph<TravelState> graph = new StateGraph<>(TravelState::new);

        graph.addNode("itineraryPlanner", node_async(state -> {
            int attempt = state.attempts() + 1;
            List<String> logs = new ArrayList<>(state.logs());
            logs.add("ItineraryPlanner (attempt " + attempt + "): drafting plan for " + state.destination());

            List<String> activities = new ArrayList<>();
            for (int day = 1; day <= state.durationDays(); day++) {
                activities.add("Day " + day + ": " + activityFor(day, state.destination()));
            }

            double cost = state.durationDays() * BASE_COST_PER_DAY
                    * Math.pow(COST_REDUCTION_PER_RETRY, attempt - 1);
            cost = Math.round(cost * 100.0) / 100.0;

            return Map.<String, Object>of(
                    TravelState.LOGS, logs,
                    TravelState.ACTIVITIES, activities,
                    TravelState.ESTIMATED_COST, cost,
                    TravelState.ATTEMPTS, attempt);
        }));

        graph.addNode("budgetValidator", node_async(state -> {
            List<String> logs = new ArrayList<>(state.logs());
            boolean approved = state.estimatedCost() <= state.budget();
            logs.add("BudgetValidator: cost $" + state.estimatedCost()
                    + " vs limit $" + state.budget() + " -> " + (approved ? "approved" : "rejected"));

            return Map.<String, Object>of(
                    TravelState.LOGS, logs,
                    TravelState.BUDGET_APPROVED, approved);
        }));

        graph.addEdge(START, "itineraryPlanner");
        graph.addEdge("itineraryPlanner", "budgetValidator");

        graph.addConditionalEdges("budgetValidator",
                edge_async(state -> {
                    if (state.budgetApproved()) return "approve";
                    if (state.attempts() >= MAX_ATTEMPTS) return "give_up";
                    return "reject";
                }),
                Map.of(
                        "approve", END,
                        "give_up", END,
                        "reject", "itineraryPlanner"));

        this.compiledGraph = graph.compile();
    }

    public TripItinerary runWorkflow(String destination, int budget, int duration) {
        Map<String, Object> input = Map.of(
                TravelState.DESTINATION, destination,
                TravelState.BUDGET, budget,
                TravelState.DURATION_DAYS, duration,
                TravelState.LOGS, List.of("Workflow started"),
                TravelState.BUDGET_APPROVED, false,
                TravelState.ATTEMPTS, 0);

        TravelState result = compiledGraph.invoke(input)
                .orElseThrow(() -> new IllegalStateException("Workflow produced no result"));

        return new TripItinerary(
                result.destination(),
                result.budget(),
                result.durationDays(),
                result.activities(),
                result.estimatedCost(),
                result.budgetApproved(),
                result.attempts(),
                result.logs());
    }

    private String activityFor(int day, String destination) {
        String[] ideas = {
                "Explore the city centre of " + destination,
                "Museum visit and local dining",
                "Day trip to a nearby attraction",
                "Markets, shopping and street food",
                "Parks, viewpoints and a relaxed evening"
        };
        return ideas[(day - 1) % ideas.length];
    }
}
