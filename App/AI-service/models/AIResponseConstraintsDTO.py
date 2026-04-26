from pydantic import BaseModel, Field
from typing import List, Dict, Optional, Any
from datetime import datetime

class AIResponseConstraintsDTO(BaseModel):
    allowDiagnosis: bool = Field(
        False,
        description="AI must not produce diagnosis"
    )

    allowTreatmentAdvice: bool = Field(
        False,
        description="AI must not prescribe treatment"
    )

    requireDisclaimer: bool = Field(
        True,
        description="Response must include disclaimer"
    )

    maxResponseLength: int = Field(
        500,
        description="Maximum allowed response length"
    )

    class Config:
        extra = "forbid"