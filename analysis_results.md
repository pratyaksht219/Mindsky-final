# Screening Service & MLScreeningEngine Analysis

This document provides a comprehensive analysis of the logic, scoring mechanisms, and evaluation flows driving the mental health screening functionality across the `Screening` (Java/Spring) and `MLScreeningEngine` (Python/FastAPI) microservices.

## 1. High-Level Architecture & Flow

The screening process is a conversational turn-based system orchestrated by `ScreeningFlowOrchestratorImpl`. 

1. **Initialization**: A user starts a screening session via the `/api/screening/start` endpoint, creating a stateful session and receiving an initial prompt.
2. **Conversation Loop**: For each user response sent to `/api/screening/answer`:
   - The Java `Screening` service forwards the text to the Python `MLScreeningEngine`.
   - The ML engine returns similarity scores for various mental health domains.
   - The Java service applies a mapping weight and accumulates these scores in the session state.
   - An `EmergencyDetector` checks for immediate crisis signals.
   - The `KeywordScreeningEngine` evaluates if enough data has been collected to make a routing decision.
3. **Termination**: The session either short-circuits due to an emergency, completes by routing the user to a specific clinical questionnaire (e.g., PHQ-9), or defaults to a generic fallback questionnaire (DASS-21) if the maximum turn limit is reached.

---

## 2. MLScreeningEngine (Python / Semantic Search)

The `MLScreeningEngine` acts as a zero-shot text classifier using semantic embeddings.

- **Model**: It uses the `sentence-transformers/all-MiniLM-L6-v2` model to encode text into dense vector representations.
- **Prototypes**: For 9 different clinical domains (Anxiety, Depression, ADHD, Sleep, Stress, Social Anxiety, Trauma, General Distress, Social Support), there is a predefined list of 15 prototype sentences (e.g., *"I feel empty and emotionally numb"* for Depression).
- **Embedding Generation**: At startup, it computes a "prototype vector" for each domain by taking the mathematical mean (`np.mean`) of the embeddings of all 15 sentences for that domain.
- **Classification**: When user text is received, it encodes the text and computes the **Cosine Similarity** between the user's text vector and each of the 9 prototype vectors. It returns a dictionary of these similarity scores (ranging from -1.0 to 1.0, but practically 0.0 to 1.0).

---

## 3. Signal Accumulation (Java Service)

Once the `Screening` service receives the similarity scores, the `MLSignalMapper` processes them:

1. **Top Signal Extraction**: It only looks at the domain with the highest similarity score.
2. **Thresholding**: The top score must be strictly greater than `0.45` to be considered a valid signal. If it's lower, the turn is essentially ignored from a scoring perspective (though the turn counter still increments).
3. **Weighting**: If the score exceeds `0.45`, it is passed to `ScreeningSignalState`. Here, the similarity score is multiplied by a `TURN_WEIGHT` of `3.0`.
   - *Example*: A similarity of `0.50` becomes a weighted score of `1.50`.
4. **Accumulation**: This weighted score is added to the running total for that specific domain in `domainSignalScores`.

> [!NOTE] 
> The codebase shows that the previous "Keyword Extractor" and traditional "Signal Mapper" components have been commented out in `ScreeningFlowOrchestratorImpl`. The system relies **entirely** on the ML engine and the emergency detector.

---

## 4. Evaluation & Routing Logic

The `DefaultKeywordScreeningEngine` evaluates the accumulated state at the end of each turn.

### Constraints
- **Minimum Turns**: `MIN_SCREENING_TURNS = 3`. The system will always ask at least 3 questions.
- **Maximum Turns**: `MAX_SCREENING_TURNS = 5`. The system will never ask more than 5 questions.

### Decision Rules
1. **Emergency Short-Circuit**: If the `EmergencyDetector` flags the message as `HIGH` risk, the screening immediately halts and returns a `CRISIS` status.
2. **Threshold Routing**: If the turn count is $\ge 3$, the system checks the highest accumulated domain score. If the score is $\ge 4.0$ (`ROUTING_THRESHOLD`), the screening concludes and routes the user to the corresponding questionnaire (e.g., `phq9` for Depression).
3. **Max Turns Fallback**: If the turn count reaches 5 and no domain has hit the `4.0` threshold, the screening concludes and routes the user to the fallback questionnaire (`dass21`).

### Mathematical Implications of the Scoring
For a domain to hit the `4.0` routing threshold within the minimum 3 turns, the average accumulated score per turn must be `1.33`.
Because the `TURN_WEIGHT` is `3.0`, the average raw similarity from the ML engine needs to be `1.33 / 3.0 = 0.444`. 
However, since the absolute minimum threshold to record *any* score is `0.45`, **if a user hits the threshold for the same domain for all 3 turns, they are mathematically guaranteed to be routed** (e.g., `0.45 * 3.0 * 3 = 4.05`). 

If a user gives mixed signals (e.g., 2 turns of Depression, 1 turn of Anxiety) or vague signals (similarity $< 0.45$), the system will likely require the 4th or 5th turn to reach the `4.0` threshold, or simply fall back to DASS-21.
