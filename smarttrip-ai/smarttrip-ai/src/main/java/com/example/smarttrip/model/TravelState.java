package com.example.smarttrip.model;

import org.bsc.langgraph4j.state.AgentState;

import java.util.List;
import java.util.Map;

/**
 * State object that flows through the LangGraph nodes.
 * Values live in the underlying data map; typed accessors make nodes readable.
 */
public class TravelState extends AgentState {

    public static final String DESTINATION = "destination";
    public static final String BUDGET = "budget";
    public static final String DURATION_DAYS = "durationDays";
    public static final String LOGS = "systemLogs";
    public static final String ACTIVITIES = "dailyActivities";
    public static final String ESTIMATED_COST = "estimatedTotalCost";
    public static final String BUDGET_APPROVED = "budgetApproved";
    public static final String ATTEMPTS = "attempts";

    public TravelState(Map<String, Object> initData) {
        super(initData);
    }

    public String destination() {
        return this.<String>value(DESTINATION).orElse("");
    }

    public int budget() {
        return this.<Integer>value(BUDGET).orElse(0);
    }

    public int durationDays() {
        return this.<Integer>value(DURATION_DAYS).orElse(1);
    }

    public List<String> logs() {
        return this.<List<String>>value(LOGS).orElse(List.of());
    }

    public List<String> activities() {
        return this.<List<String>>value(ACTIVITIES).orElse(List.of());
    }

    public double estimatedCost() {
        return this.<Double>value(ESTIMATED_COST).orElse(0.0);
    }

    public boolean budgetApproved() {
        return this.<Boolean>value(BUDGET_APPROVED).orElse(false);
    }

    public int attempts() {
        return this.<Integer>value(ATTEMPTS).orElse(0);
    }
}
