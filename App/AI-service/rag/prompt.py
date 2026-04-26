"""
This file defines:
1. SYSTEM_PROMPT: Immutable role + safety definition
2. build_user_prompt(): Dynamic prompt builder using assessment + RAG context
"""
from typing import List, Dict, Any
from models.AiServiceRequest import AiServiceRequest
from models.RequestMetadataDTO import RequestMetadataDTO
from models.AssessmentSummaryDTO import AssessmentSummaryDTO
from models.ComponentInsightDTO import ComponentInsightDTO
from models.RiskSignalDTO import RiskSignalDTO
from models.AIResponseConstraintsDTO import AIResponseConstraintsDTO
# ============================================================
# 1️⃣ SYSTEM PROMPT (IMMUTABLE)
# ============================================================
SYSTEM_PROMPT = """
You are a clinical mental health explanation assistant.

Your role is to:
- Explain mental health questionnaire results in a clear, supportive, and non-judgmental way
- Help users understand what their scores may indicate
- Provide general, educational insights and coping-oriented guidance

STRICT SCOPE RULES:
- You must NOT diagnose medical or psychiatric conditions
- You must NOT prescribe medication or treatment
- You must NOT replace a licensed mental health professional
- You must NOT provide emergency instructions or crisis intervention steps
- You must NOT mention suicide methods or self-harm techniques

SAFETY AND TONE:
- If the assessment indicates severe distress, acknowledge intensity calmly
- Maintain a reassuring, empathetic, and non-alarming tone
- Emergency handling is managed outside your scope

OUTPUT FORMAT — STRICT JSON ONLY:

You MUST return a single valid JSON object.
You MUST NOT include any text before or after the JSON.
You MUST NOT include markdown, explanations, comments, or commentary.

The JSON object MUST contain EXACTLY the following fields
(case-sensitive, camelCase, no deviations allowed):

- summary (string)
- severityExplanation (string)
- keyFindings (array of strings)
- recommendations (array of strings)
- reassurance (string)

FIELD NAME RULES:
- Field names are CASE-SENSITIVE
- camelCase is REQUIRED
- snake_case is INVALID
- Missing fields are INVALID
- Extra fields are INVALID

VALID FIELD NAMES (EXACT):
summary
severityExplanation
keyFindings
recommendations
reassurance

CONTENT RULES:
- Use neutral, respectful, and clinically grounded language
- Do NOT use diagnostic labels
- Do NOT speculate beyond the provided data
- Base all statements on the assessment results and provided context
- Recommendations must be general, non-medical, and non-prescriptive

FAILURE HANDLING:
- Responses that do not EXACTLY match the required JSON schema
  will be rejected and retried automatically.

You will be provided:
- Assessment results
- A structured breakdown of questionnaire responses
- Optional reference material from trusted mental health sources

Respond ONLY with the valid JSON object described above.
""".strip()
# ============================================================
# 2️⃣ USER PROMPT BUILDER
# ============================================================
from typing import List, Dict, Any


def build_user_prompt(
    # assessmentResult: Dict[str, Any],
    # assessmentBreakdown: Dict[str, Any],
    requestMetadata: RequestMetadataDTO,
    assessmentSummaryDTO: AssessmentSummaryDTO,
    componentInsights: List[ComponentInsightDTO],
    riskSignals: List[RiskSignalDTO],
    contextHints: Dict[str, Any],
    aiResponseConstraints: AIResponseConstraintsDTO,
    retrievedContext: List[str],
) -> str:
    """
    Builds the dynamic user prompt for the LLM.
    """

    # --- SAFE EXTRACTION (dict-based) ---
    questionnaireId = assessmentResult.questionnaireId
    totalScore = assessmentResult.totalScore
    severityLabel = assessmentResult.severityLabel
    assessmentType = assessmentResult.assessmentType
    subscaleScores = assessmentResult.subscaleScores
    questionResults = assessmentBreakdown.questionResults

    


    requestMetadata=requestMetadata
    assessmentSummaryDTO=assessmentSummaryDTO
    componentInsights=componentInsights
    riskSignals=riskSignals
    aiResponseConstraints=aiResponseConstraints
    contextHints = contextHints



    # --- Subscale formatting ---
    if isinstance(subscaleScores, dict) and subscaleScores:
        subscaleText = "\n".join(f"- {k}: {v}" for k, v in subscaleScores.items())
    else:
        subscaleText = "Not applicable"

    # --- Question formatting ---
    if questionResults:
        questionsText = "\n".join(
            f"- {q.questionText} | Answer: {q.answerLabel}"
            for q in questionResults
        )
    else:
        questionsText = "No individual responses available."

    # --- RAG context ---
    contextText = (
        "\n\n".join(retrievedContext)
        if retrievedContext
        else "No additional clinical context provided."
    )

    return f"""
ASSESSMENT SUMMARY:
Questionnaire: {questionnaireId}
Total Score: {totalScore}
Severity Level: {severityLabel}
Assessment Type: {assessmentType}

SUBSCALE SCORES (if applicable):
{subscaleText}

RESPONSE BREAKDOWN:
{questionsText}

CLINICAL CONTEXT:
{contextText}

TASK:
Based on the assessment data and clinical context above:

1. Write a clear and supportive summary of what these results may indicate
2. Explain what the severity level generally means in everyday terms
3. Highlight key patterns or findings reflected in the responses
4. Provide general, non-medical coping or self-care recommendations
5. End with a reassuring message emphasizing support and professional guidance

IMPORTANT:
- Do not use diagnostic language
- Do not speculate beyond the data
- Keep explanations practical and grounded
- Use neutral, respectful phrasing

Return the response in the exact JSON format specified.
""".strip()