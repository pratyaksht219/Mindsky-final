from typing import Dict, List, Optional
from pydantic import BaseModel, Field
from models.assessment.QuestionResult import QuestionResult


class AssessmentBreakdown(BaseModel):
    questionnaireId: str = Field(
        ..., alias="questionnaireId"
    )

    # Per-question responses with context
    questionResults: List[QuestionResult] = Field(
        ..., alias="questionResults"
    )

    # Subscale results (DASS-21, LSAS-SR)
    subscaleScores: Optional[Dict[str, int]] = Field(
        default=None,
        alias="subscaleScores"
    )

    class Config:
        populate_by_name = True