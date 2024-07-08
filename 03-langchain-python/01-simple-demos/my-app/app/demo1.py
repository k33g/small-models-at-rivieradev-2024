import os

from langchain_community.llms import ollama # type: ignore
from langchain.prompts import PromptTemplate # type: ignore

ollama_base_url = os.getenv("OLLAMA_BASE_URL")
model_name = os.getenv("LLM")

llm = ollama.Ollama(
    base_url=ollama_base_url, 
    model=model_name,
)

prompt_template = PromptTemplate.from_template(
    "Explain the programming concept of {what} in {language}."
)
prompt = prompt_template.format(what="loop", language="golang")

completion = llm.invoke(prompt)

print(completion)
