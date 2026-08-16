# Event

An event is a structured representation of an observable occurrence within an application system or its surrounding infrastructure.

An event represents something that happened at a particular point in time, such as:

* An application error
* A database query failure
* An HTTP request
* A deployment
* A timeout
* A container failure
* A security-related occurrence
* A performance anomaly

In BugLens, events are the fundamental units of information used for **filtering, querying, correlation, and analysis**.

## Why does BugLens need Events?

Raw monitoring data is usually tied to its original source and format.

For example:

* Java application logs
* Nginx access logs
* Docker logs
* Database logs
* Infrastructure monitoring data

Each source can represent information differently.

BugLens therefore needs a common conceptual unit that allows information to be processed through its pipeline consistently.

Hence, **Event becomes the common language between BugLens components**.

```text
Different Monitoring Sources
            │
            ▼
          Parser
            │
            ▼
        Normalizer
            │
            ▼
           Event
            │
       ┌────┼────┐
       ▼    ▼    ▼
     Query Correlation Analysis
```

## What is an Event?

An Event should contain enough information to answer several basic questions:

* What happened?
* When did it happen?
* Where did it happen?
* What system or service produced it?
* What context surrounds it?

Conceptually:

```text
Event
├── Identity
├── Time
├── Source
├── Occurrence
├── Context
└── Metadata
```

These categories represent the conceptual information associated with an event. The exact implementation and schema are defined separately in the Event Model architecture.

## Event Identity

Every event should have a unique identifier within BugLens.

This allows other components to refer to a specific event and distinguish it from other events.

An event may also contain identifiers originating from its source, such as:

* Request ID
* Transaction ID
* Trace ID
* Log ID
* Session ID

These identifiers provide contextual information and may later be used by the Correlation Engine.

They should not automatically be treated as the identity of the BugLens event itself.

## Event Time

Every event should contain the time at which the represented occurrence happened.

Time is important for:

* Ordering events
* Constructing timelines
* Restricting queries
* Identifying temporal relationships
* Investigating failure sequences
* Supporting event correlation

For example:

```text
10:31:04  Database connection established
10:31:07  HTTP request received
10:31:09  Database timeout
10:31:10  Request failed
10:31:11  Service returned HTTP 500
```

The temporal ordering of these events can provide useful information even before explicit relationships between them are established.

## Source and Origin

An event should retain information about where it came from.

Examples include:

* Service
* Host
* Process
* Environment
* Container
* Database
* Application

A normalized event should not lose its origin.

Knowing that an error occurred is useful, but knowing **where the error originated** is essential for investigating system behavior and correlating events across components.

## Event Type

Events should be classifiable according to what happened.

Examples include:

* `HTTP_REQUEST`
* `DATABASE_ERROR`
* `SERVICE_RESTART`
* `TIMEOUT`
* `APPLICATION_EXCEPTION`
* `DEPLOYMENT`

Event types provide a way to categorize events and can be used by the Query and Correlation Engines.

The final event taxonomy is not yet defined and should evolve as BugLens's supported data sources and use cases become clearer.

## Context

Context contains information that can help BugLens understand an event and its surroundings.

Examples include:

* Request ID
* Trace ID
* Transaction ID
* Session ID
* User ID
* Container ID
* Thread ID
* Process ID

Context is particularly important for the **Correlation Engine**.

For example:

```text
Event A
request_id = R123

Event B
request_id = R123
```

The shared request ID provides evidence that the two events may belong to the same request flow.

However, the presence of contextual information does not automatically mean that two events are related. The Correlation Engine determines relationships using one or more available signals.

## Metadata

Events may contain additional information that does not fit the core event structure.

For example:

```text
HTTP status      → 500
Database         → orders
Retry count      → 3
Response time    → 2400 ms
```

Metadata allows BugLens to preserve source-specific information without continuously expanding the core Event structure.

This allows the common Event model to remain relatively stable while still supporting information from different monitoring sources.

## Event Lifecycle

An event does not necessarily exist in its final form when BugLens first receives monitoring data.

It passes through several stages:

```text
Raw Monitoring Data
        │
        ▼
      Parsed Data
        │
        ▼
   Normalized Event
        │
        ▼
    Stored Event
        │
        ▼
Correlated Event Context
        │
        ▼
   Analysis Context
```

These stages do not necessarily represent different event objects or database records.

They represent the **processing lifecycle of information within BugLens**.

The Parser interprets the source data, the Normalizer produces a common Event representation, the Event Store persists it, and subsequent components enrich or associate it with additional information.

## Event vs. Log Entry

A log entry and a BugLens Event are related but are not the same concept.

A **log entry** is source-specific information produced by an application or infrastructure component.

An **Event** is BugLens's structured representation of an observable occurrence.

Conceptually:

```text
Log Entry
    │
    ▼
  Parser
    │
    ▼
Parsed Representation
    │
    ▼
Normalizer
    │
    ▼
BugLens Event
```

Furthermore, BugLens should not be restricted to traditional logs.

Future event sources may include:

* Metrics
* Traces
* Monitoring APIs
* Deployment systems
* Infrastructure systems
* External monitoring services

Therefore, an Event should be considered a broader concept than a log entry.

## Event vs. Relationship

An Event represents **something that happened**.

A Relationship represents **a connection between events or other entities**.

For example:

```text
Event A
"Database connection timeout"
        │
        │ related_to
        ▼
Event B
"HTTP request failed"
```

The timeout and HTTP failure are two separate events.

The `related_to` connection between them is a relationship, not another event.

This distinction is fundamental to the Correlation Engine and the Graph Model.

## Event Principles

BugLens Events should follow several general principles:

### 1. Events should preserve their origin

Normalization should provide consistency without unnecessarily discarding information about the original source.

### 2. Events should be independently identifiable

Every event should be distinguishable from other events within BugLens.

### 3. Events should be time-aware

The occurrence time of an event is fundamental to ordering, querying, correlation, and analysis.

### 4. Events should be extensible

The Event model should support additional contextual and source-specific information without requiring constant changes to its core structure.

### 5. Events should remain independent from storage

An Event is a BugLens concept, not a MongoDB document.

The storage implementation should represent the Event rather than define what an Event is.

### 6. Events should not contain inferred relationships by default

The initial Event represents observed information.

Relationships and higher-level conclusions should be established by subsequent BugLens components, particularly the Correlation and Analysis Engines.

---

## Summary

An Event is the fundamental unit through which BugLens represents observable occurrences from application systems and their surrounding infrastructure.

It provides a common representation for information originating from different monitoring sources and allows BugLens to perform consistent:

```text
Filtering
   ↓
Querying
   ↓
Correlation
   ↓
Analysis
```

The Event concept therefore forms the foundation for the rest of the BugLens architecture.