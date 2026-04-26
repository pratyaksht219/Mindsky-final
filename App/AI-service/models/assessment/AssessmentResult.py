from pydantic import BaseModel, Field
from typing import Optional, Dict, Any

class AssessmentResult(BaseModel):
    questionnaireId: str = Field(..., alias="questionnaireId")
    totalScore: int = Field(..., alias="totalScore")
    severityLabel: str = Field(..., alias="severityLabel")
    assessmentType: str = Field(..., alias="assessmentType")

    subscaleScores: Optional[Dict[str, int]] = Field(
        default=None, alias="subscaleScores"
    )
    flags: Optional[Dict[str, Any]] = None

    class Config:
        populate_by_name = True