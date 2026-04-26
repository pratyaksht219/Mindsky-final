import json
from models.AiServiceResponse import AiServiceResponse
from pydantic import ValidationError


def parse_and_validate_llm_response(raw_text: str) -> AiServiceResponse:
    """
    Parses and validates LLM output strictly.
    Raises controlled errors if invalid.
    """

    try:
        raw_text = raw_text.strip()
        if raw_text.startswith("```json"):
            raw_text = raw_text[7:]
        if raw_text.startswith("```"):
            raw_text = raw_text[3:]
        if raw_text.endswith("```"):
            raw_text = raw_text[:-3]
        raw_text = raw_text.strip()
        
        parsed = json.loads(raw_text)
        
        # Soft-fix for open-source models generating snake_case
        if "severity_explanation" in parsed:
            parsed["severityExplanation"] = parsed.pop("severity_explanation")
        if "key_findings" in parsed:
            parsed["keyFindings"] = parsed.pop("key_findings")
            
    except json.JSONDecodeError as e:
        raise ValueError("LLM returned invalid JSON") from e
    try:
        return AiServiceResponse.model_validate(parsed)
    except ValidationError as e:
        raise ValueError(
            f"LLM response schema validation failed: {e}"
        ) from e

def safe_fallback_response() -> AiServiceResponse:
    return AiServiceResponse(
        summary="Your responses have been received and reviewed.",
        severityExplanation=(
            "The assessment indicates areas that may benefit from further attention."
        ),
        keyFindings=[
            "Some responses suggest emotional strain",
        ],
        recommendations=[
            "Consider speaking with a mental health professional",
            "Engage in supportive self-care practices",
        ],
        reassurance=(
            "You are not alone, and support is available."
        ),
    )