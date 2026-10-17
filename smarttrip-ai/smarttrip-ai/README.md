# SmartTrip AI

Agentic travel planner built with Spring Boot 3 and LangGraph4j.

## Workflow
`itineraryPlanner -> budgetValidator -> (approved: END | rejected: back to planner, max 5 attempts)`

## Run
Requires Java 17+ and Maven 3.9+.

```bash
mvn spring-boot:run
```

## Try it
```
http://localhost:8080/api/trip/plan?destination=Paris&budget=2000&duration=5
```

## Roadmap
- Replace mock planner with Spring AI / LangChain4j LLM calls
- Router node and flight/hotel search agent with Spring Bean tools
- PostgreSQL persistence (Spring Data JPA), SSE/WebSocket streaming of node transitions
- Human-in-the-loop checkpoints

## Push to GitHub
```bash
git init
git add .
git commit -m "Initial commit: SmartTrip AI"
git branch -M main
git remote add origin https://github.com/<your-username>/smarttrip-ai.git
git push -u origin main
```
