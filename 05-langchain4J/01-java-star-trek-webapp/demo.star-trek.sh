#!/bin/bash
: <<'END_COMMENT'
This demo shows how to use the context to make the answer more accurate
END_COMMENT

#sudo chmod 666 /var/run/docker.sock
#HTTP_PORT=9999 LLM=tinyllama OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp watch
#HTTP_PORT=9999 LLM=tinyllama OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up
HTTP_PORT=9999 LLM=tinyllama OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up

