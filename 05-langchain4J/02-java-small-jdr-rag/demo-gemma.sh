#!/bin/bash
: <<'COMMENT'
This demo is about RAG
COMMENT

#sudo chmod 666 /var/run/docker.sock
#HTTP_PORT=7777 LLM=tinyllama OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up
HTTP_PORT=7777 LLM=gemma:2b OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up
