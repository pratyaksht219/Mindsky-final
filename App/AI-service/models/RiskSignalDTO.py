from pydantic import BaseModel, Field
from typing import Optional


class RiskSignalDTO(BaseModel):
    signalId: str = Field(
        ..., description="Risk signal identifier"
    )

    level: str = Field(
        ..., description="NONE | LOW | MODERATE | HIGH"
    )

    sourceComponentId: Optional[str] = None

    clinicalNote: Optional[str] = Field(
        None,
        description="Short factual note about the signal"
    )

    class Config:
        extra = "forbid"