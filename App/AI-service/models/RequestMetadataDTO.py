from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime


class RequestMetadataDTO(BaseModel):
    requestId: str = Field(..., description="Traceability ID")
    questionnaireId: str = Field(..., description="gad7, phq9, psqi etc")
    questionnaireName: Optional[str] = Field(
        None, description="Human readable questionnaire name"
    )
    language: str = Field(..., description="Language code e.g. en")
    createdAt: datetime
    
    class Config:
        extra = "forbid"
