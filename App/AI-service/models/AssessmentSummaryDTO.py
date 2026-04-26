from pydantic import BaseModel, Field
from typing import Optional

class AssessmentSummaryDTO(BaseModel):
    finalScore: float = Field(..., description="Computed questionnaire score")
    severityLabel: str = Field(..., description="Severity classification")

    clinicalDescription: Optional[str] = Field(
        None,
        description="Clinical interpretation from scoring rules"
    )

    minScore: Optional[float] = None
    maxScore: Optional[float] = None
    
    class Config:
        extra = "forbid"