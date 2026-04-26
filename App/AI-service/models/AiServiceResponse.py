from pydantic import BaseModel, Field
from typing import List


class AiServiceResponse(BaseModel):
    """
    Structured response returned by the AI analysis service.
    """

    summary: str = Field(
        ...,
        description="High-level explanation of the assessment results",
        min_length=20
    )

    severityExplanation: str = Field(
        ...,
        description="What the severity level generally means in everyday terms",
        min_length=20
    )

    keyFindings: List[str] = Field(
        ...,
        description="Key patterns or findings reflected in the responses",
        min_items=1
    )

    recommendations: List[str] = Field(
        ...,
        description="Actionable coping strategies or next steps",
        min_items=1
    )

    reassurance: str = Field(
        ...,
        description="Supportive and calming reassurance message",
        min_length=10
    )

    class Config:
        extra = "forbid"