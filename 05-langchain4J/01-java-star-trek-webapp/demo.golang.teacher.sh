#!/bin/bash
: <<'COMMENT'
This demo allows to create a conversation with the LLM
COMMENT

#sudo chmod 666 /var/run/docker.sock
#HTTP_PORT=8888 LLM=phi3 OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up --build
#HTTP_PORT=8888 LLM=deepseek-coder OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp watch
HTTP_PORT=8888 LLM=deepseek-coder OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up
