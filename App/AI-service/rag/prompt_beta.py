from typing import List, Dict, Any
from models.AiServiceRequest import AiServiceRequest
from models.RequestMetadataDTO import RequestMetadataDTO
from models.AssessmentSummaryDTO import AssessmentSummaryDTO
from models.ComponentInsightDTO import ComponentInsightDTO
from models.RiskSignalDTO import RiskSignalDTO
from models.AIResponseConstraintsDTO import AIResponseConstraintsDTO
from rag.recommendations import DOMAIN_RECOMMENDATIONS

QUESTIONNAIRE_TO_DOMAIN = {
    "gad7": "ANXIETY",
    "phq9": "DEPRESSION",
    "asrs": "ADHD",
    "psqi": "SLEEP",
    "pss10": "STRESS",
    "lsas": "SOCIAL_ANXIETY",
    "pcl5": "TRAUMA",
    "pcptsd": "TRAUMA",
    "k10": "DISTRESS_GENERAL",
    "dass21": "DISTRESS_GENERAL",
    "mspss": "SOCIAL_SUPPORT"
}


def build_user_prompt(
    request: AiServiceRequest,
    retrievedContext: List[str],
) -> str:
    """
    Builds the dynamic user prompt for the LLM using AiServiceRequest.
    """

    # --- Extract core sections ---
    metadata = request.metadata
    summary = request.assessment
    components = request.components
    risks = request.riskSignals
    constraints = request.constraints
    contextHints = request.contextHints

    # --- Basic fields ---
    questionnaireId = metadata.questionnaireId
    questionnaireName = metadata.questionnaireId
    totalScore = summary.finalScore
    severityLabel = summary.severityLabel
    clinicalDescription = summary.clinicalDescription or "Not specified"

    # --- Component insights formatting ---
    if components:
        componentText = "\n".join(
            f"- {c.componentName} (Score: {c.score})"
            + (f" → {c.interpretationHint}" if c.interpretationHint else "")
            for c in components
        )
    else:
        componentText = "No component-level insights available."

    # --- Risk signals formatting ---
    if risks:
        riskText = "\n".join(
            f"- {r.signalId} | Level: {r.level}"
            + (f" → {r.clinicalNote}" if r.clinicalNote else "")
            for r in risks
        )
    else:
        riskText = "No significant risk signals detected."

    # --- Context hints ---
    contextHintText = (
        "\n".join(f"- {k}: {v}" for k, v in contextHints.items())
        if contextHints else "No additional context hints."
    )

    # --- RAG context ---
    contextText = (
        "\n\n".join(retrievedContext)
        if retrievedContext
        else "No additional clinical context provided."
    )

    # --- Targeted Recommendations ---
    domain = QUESTIONNAIRE_TO_DOMAIN.get(questionnaireId.lower())
    if domain and domain in DOMAIN_RECOMMENDATIONS:
        recs = "\n".join(f"- {rec}" for rec in DOMAIN_RECOMMENDATIONS[domain])
        targeted_recs_section = f"\nTARGETED CLINICAL RECOMMENDATIONS:\n{recs}\n"
    else:
        targeted_recs_section = ""

    return f"""
ASSESSMENT SUMMARY:
Questionnaire: {questionnaireName} ({questionnaireId})
Total Score: {totalScore}
Severity Level: {severityLabel}
Clinical Description: {clinicalDescription}

COMPONENT INSIGHTS:
{componentText}

RISK SIGNALS:
{riskText}

ADDITIONAL CONTEXT:
{contextHintText}

CLINICAL CONTEXT (RAG):
{contextText}
{targeted_recs_section}

TASK:
Based on the assessment data and context above:

1. Write a clear, supportive summary of what these results may indicate
2. Explain the severity level in simple, everyday language
3. Highlight key behavioral or emotional patterns
4. Provide only the suggested coping strategies listed in 'TARGETED CLINICAL RECOMMENDATIONS'
5. Acknowledge any risk signals with sensitivity (if present)
6. End with a supportive note encouraging professional help if needed

IMPORTANT CONSTRAINTS:
- Do NOT provide diagnosis
- Do NOT give medical or treatment advice
- Stay grounded strictly in provided data
- Keep response within {constraints.maxResponseLength} words
- Include disclaimer if required: {constraints.requireDisclaimer}

Return the response in the exact JSON format specified.
""".strip()