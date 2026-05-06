from typing import List

from langchain.schema import Document
from llm.llm_client import Client
from rag.prompt import SYSTEM_PROMPT
from rag.prompt_beta import build_user_prompt
from rag.response_validator import (
    parse_and_validate_llm_response,
    safe_fallback_response
)
from models.AiServiceRequest import AiServiceRequest
from models.AiServiceResponse import AiServiceResponse


# MODEL = "MedAIBase/MedGemma1.5:4b"
# MODEL = "google/gemma-3-27b-it:free"
# MODEL = "meta-llama/llama-3.3-70b-instruct:free"
# MODEL = "openai/gpt-oss-120b:free"
# MODEL = "nousresearch/hermes-3-llama-3.1-405b:free"
# MODEL = "openrouter/free"
# MODEL = "groq/compound"
MODEL = "openai/gpt-oss-120b"
class RAGChain:
    def __init__(self):
        self.client = Client()

    def run(
        self,
        request: AiServiceRequest,
        documents: List[Document]
    ) -> AiServiceResponse:
        """
        Full RAG pipeline execution.
        """

        context = self._format_context(documents)

        systemPrompt = SYSTEM_PROMPT
        userPrompt = build_user_prompt(
            request=request,
            retrievedContext=[context]
        )

        print("user prompt has been built....")
        print(f"""System prompt: {len(SYSTEM_PROMPT)}""")
        print(f"""User prompt: {len(userPrompt)}""")
        print(len(userPrompt)+len(SYSTEM_PROMPT))
        print("======================================================================================")
        print("LLM response is being generated....")
        print("System Prompt:")
        print(systemPrompt)
        print("======================================================================================")
        print("User Prompt:")
        print(userPrompt)
        print("======================================================================================")
        print("Using Model: ", MODEL)


        # try:
        #     raw_response = self.client.chat(
        #         model=MODEL,
        #         messages=[
        #             {"role": "system", "content": systemPrompt},
        #             {"role": "user", "content": userPrompt}
        #         ],
        #         options={"timeout": 60}
        #     )
        #     print("RAW LLM RESPONSE:")
        #     print(raw_response)
        #     raw_text = raw_response["message"]["content"]
            
        #     return parse_and_validate_llm_response(raw_text)

        # except Exception as e:
        #     import traceback
        #     print("======================================================================================")
        #     print(f"OPENROUTER API OR PARSING ERROR: {str(e)}")
        #     traceback.print_exc()
        #     print("======================================================================================")
        #     return safe_fallback_response()

            
        #     USE THE FOLLOWING RETURN STATEMENT WHEN IN DEVELOPMENT MODE 
        #     ----------------------------------------------------------------------

        return AiServiceResponse(
            summary="Your responses indicate a pattern of emotional distress that may be affecting your mood, energy levels, and daily functioning. Several answers suggest that these feelings have been present consistently rather than only occasionally.",
            severityExplanation="A moderately severe level generally means that symptoms are frequent and impactful, but not uncommon. People in this range often notice difficulties in concentration, motivation, and emotional balance that can interfere with everyday activities.",
            keyFindings=[
                "Low mood and reduced interest appear consistently across multiple responses",
                "Fatigue and concentration difficulties are reported more than half the days",
                "No indication of active self-harm thoughts based on the responses provided"
            ],
            recommendations=[
                "Consider discussing these results with a qualified mental health professional for personalized guidance",
                "Maintain regular sleep and daily routines to support emotional stability",
                "Engage in activities that offer small, achievable sources of enjoyment or relaxation"
            ],
            reassurance="Many people experience similar challenges at different points in life. Support is available, and seeking understanding is a positive step forward."
        )


    @staticmethod
    def _format_context(docs: List[Document]) -> str:
        """
        Formats retrieved documents into a single context block.
        """
        blocks = []

        for d in docs:
            source = d.metadata.get("source_type", "unknown")
            blocks.append(
                f"[{source.upper()}]\n{d.page_content}"
            )

        return "\n\n".join(blocks)