from typing import List
from langchain.schema import Document
from langchain.vectorstores import Chroma


class RAGRetriever:
    def __init__(self, vector_store: Chroma):
        self.vector_store = vector_store

    def retrieve(
        self,
        questionnaireId: str,
        top_k: int = 8
    ) -> List[Document]:
        """
        Retrieve clinically relevant documents for a questionnaire.
        """

        filters = {
            "questionnaire": questionnaireId
        }

        docs = self.vector_store.similarity_search(
            query=questionnaireId,
            k=top_k,
            filter=filters
        )

        return self._sort_by_priority(docs)

    @staticmethod
    def _sort_by_priority(docs: List[Document]) -> List[Document]:
        """
        Sort documents by priority metadata.
        """
        return sorted(
            docs,
            key=lambda d: d.metadata.get("priority", 99)
        )