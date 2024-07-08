import os

from langchain_community.llms import ollama # type: ignore
from langchain.callbacks.manager import CallbackManager # type: ignore
from langchain.callbacks.streaming_stdout import StreamingStdOutCallbackHandler # type: ignore

ollama_base_url = os.getenv("OLLAMA_BASE_URL")
model_name = os.getenv("LLM")

llm = ollama.Ollama(
    base_url=ollama_base_url, 
    model=model_name,
    callback_manager= CallbackManager([StreamingStdOutCallbackHandler()])
)

llm.invoke("Who is James T Kirk?")

print('\n')

