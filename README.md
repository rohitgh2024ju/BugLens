### BugLens Overview

# What is BugLens?

- BugLens is a initial open-source project designed to address a specific problem. It aims for filtering and correlating events from monitoring data to help understand failures and system behavior. This project is initially started with Java and its ecosystem/framework for backend. Whereas the frontend dashboard will be built with react.

# Why we need BugLens?

- Modern application systems generate a large amount of monitoring data from different sources such as application logs, databases, services, containers, and infrastructure. When a failure occurs, these sources can produce thousands of events within a short period of time.

The challenge is not simply finding an error. The real challenge is determining:

Which events are relevant to the failure?
Which events are related to each other?
What happened before the failure?
How did the failure propagate through the system?
Which event was the actual cause and which were only consequences?

Traditional log searching and filtering can help developers locate individual events, but understanding the relationships between events often requires manually piecing together information from different sources.

BugLens aims to reduce this complexity by transforming raw monitoring data into structured events, filtering relevant information, and correlating related events so that developers can better understand system behavior and investigate failures.

# How does BugLens work?

- BugLens processes monitoring data through a series of stages. Each stage has a specific responsibility, transforming raw monitoring data into structured, queryable, and correlated events.

Monitoring Data
      │
      ▼
Parser Engine
      │
      ▼
Normalizer
      │
      ▼
Event Store
      │
      ▼
Query Layer
      │
      ▼
Correlation Engine
      │
      ▼
Analysis Engine
      │
      ▼
     API
      │
      ├──────────► React Dashboard
      ├──────────► CLI
      └──────────► Reports

1. Parser Engine
Reads monitoring data from different sources and formats and converts it into a structured representation that BugLens can understand.

2. Normalizer
Transforms the parsed data into BugLens's common event model, providing a consistent structure regardless of where the event originated.

3. Event Store
Stores normalized events so that they can be efficiently retrieved and processed later.

4. Query Layer
Provides a controlled interface for retrieving, filtering, and analyzing stored events without exposing the underlying storage implementation directly.

5. Correlation Engine
Identifies relationships between events using available contextual information such as timestamps, services, request identifiers, traces, and other relevant signals.

6. Analysis Engine
Uses the correlated events and their relationships to help identify patterns, failure chains, anomalies, and potentially relevant causes.

7. Developer / Dashboard
Presents the resulting information in a form that developers can use to investigate and understand system behavior.

# Technology Stack

## Backend
- Java
- Spring Boot
- MongoDB

## Frontend
- React

# Project Status

- Early Development

BugLens is currently in its architectural and foundational
development stage. Core components are being designed and
implemented incrementally.

# Roadmap

Phase 1 — Foundation
□ Event model
□ Parser
□ Normalizer
□ Event storage

Phase 2 — Query
□ Query layer
□ Filtering
□ Time-based queries

Phase 3 — Correlation
□ Relationship model
□ Correlation engine
□ Event graph

Phase 4 — Analysis
□ Failure chains
□ Root-cause assistance
□ Anomaly analysis

Phase 5 — Dashboard
□ React dashboard
□ Timeline visualization
□ Event graph visualization

# Documentation

## Documentation

For detailed information about BugLens architecture,
concepts, design decisions, and development, see the
[documentation](docs/).