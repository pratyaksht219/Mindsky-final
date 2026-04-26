from pydantic import BaseModel, Field
from typing import Optional


class QuestionResult(BaseModel):
    questionId: str = Field(
        ..., alias="questionId"
    )
    questionText: str = Field(
        ..., alias="questionText"
    )
    answerId: int = Field(
        ..., alias="answerId"
    )
    answerLabel: str = Field(
        ..., alias="answerLabel"
    )

    # Scale context (e.g. likert_0_3)
    responseScale: str = Field(
        ..., alias="responseScale"
    )

    # Only for LSAS-SR, otherwise null
    secondaryAnswerId: Optional[int] = Field(
        default=None,
    )