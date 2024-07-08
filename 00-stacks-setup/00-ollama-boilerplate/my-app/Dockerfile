FROM ubuntu:20.04

#ARG OLLAMA_BASE_URL=${OLLAMA_BASE_URL}
#ARG LLM=${LLM}

WORKDIR /app
COPY *.sh .

ENV OLLAMA_BASE_URL=${OLLAMA_BASE_URL}
ENV LLM=${LLM}

RUN apt-get update && apt-get install -y curl jq


