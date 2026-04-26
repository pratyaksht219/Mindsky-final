from pydantic import BaseModel, Field
from typing import List, Optional


class ComponentInsightDTO(BaseModel):
    componentId: str = Field(..., description="Component identifier")

    componentName: str = Field(
        ..., description="Human readable component name"
    )

    score: float = Field(..., description="Component score")

    interpretationHint: Optional[str] = Field(
        None,
        description="Short hint about component interpretation"
    )

    contributingQuestionIds: Optional[List[str]] = []

    class Config:
        extra = "forbid"