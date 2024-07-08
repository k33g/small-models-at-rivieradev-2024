#!/bin/bash

read -r -d '' DATA <<- EOM
{
  "model": "${LLM}",
  "prompt": "What is the most famous pizza in the world?",
  "stream": false
}
EOM

JSON_RESULT=$(curl --silent ${OLLAMA_BASE_URL}/api/generate \
    -H "Content-Type: application/json" \
    -d "${DATA}"
)

response=$(echo ${JSON_RESULT} | jq -r '.response')
echo "${response}"