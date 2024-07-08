#!/bin/bash
HTTP_PORT=8888 LLM=qwen2:0.5b OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up

: <<'COMMENT'
Update the prompt to use Qwen:

Write a Golang tutorial for beginners in the form of a list, with each item containing a concept explained in plain English and a corresponding code example demonstrating its use.
I want 3 sections:
- variables
- loops
- structures
COMMENT
