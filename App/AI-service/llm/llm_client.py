from openai import OpenAI
import os
from dotenv import load_dotenv

load_dotenv()

class Client:
    def __init__(self):
        # GROQ
        self.client = OpenAI(
            api_key=os.getenv("GROQ_API_KEY"),
                    base_url="https://api.groq.com/openai/v1",
        )

    def chat(self, model: str, messages: list, options: dict = None):
        try:
            response = self.client.chat.completions.create(
                model=model,
                messages=messages,
                temperature=0.1,
                max_tokens=2000,
                timeout=options.get("timeout", 60) if options else 60,
            )

            return {
                "message": {
                    "content": response.choices[0].message.content
                }
            }

        except Exception as e:

            raise RuntimeError(f"LLM call failed: {str(e)}")