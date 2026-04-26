from pydantic import BaseModel, Field
from typing import Any, List, Dict
from models.assessment.AssessmentBreakdown import AssessmentBreakdown
from models.assessment.AssessmentResult import AssessmentResult
from models.RequestMetadataDTO import RequestMetadataDTO
from models.AssessmentSummaryDTO import AssessmentSummaryDTO
from models.ComponentInsightDTO import ComponentInsightDTO
from models.RiskSignalDTO import RiskSignalDTO
from models.AIResponseConstraintsDTO import AIResponseConstraintsDTO

class AiServiceRequest(BaseModel):

    metadata: RequestMetadataDTO = Field(
        ..., alias="requestMetadata"
    )

    assessment: AssessmentSummaryDTO = Field(
        ..., alias="assessmentSummaryDTO"
    )

    components: List[ComponentInsightDTO] = Field(
        ..., alias="componentInsights"
    )

    riskSignals: List[RiskSignalDTO] = Field(
        ..., alias="riskSignals"
    )


    contextHints: Dict[str, Any] = Field(
        default_factory=dict, 
        alias="contextHints", 
        description=(
            "Additional contextual information that may be relevant for the AI analysis. "
            "Values can be strings, numbers, or complex objects."
        )
    )

    constraints: AIResponseConstraintsDTO = Field(
        ..., alias="aiResponseConstraints"
    )



    class Config:
        populate_by_name = True