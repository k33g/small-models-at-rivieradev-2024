#!/bin/bash

OLLAMA_URL="http://localhost:11434"
MODEL="qwen:0.5b"

read -r -d '' USER_CONTENT <<- EOM
Who is the best friend of Philippe Charrière?
EOM

USER_CONTENT=$(echo ${USER_CONTENT} | tr -d '\n')

read -r -d '' DATA <<- EOM
{
  "model":"${MODEL}",
  "options": {
    "temperature": 0.5,
    "repeat_penalty": 2
  },
  "prompt": "${USER_CONTENT}",
  "stream": false
}
EOM

curl ${OLLAMA_URL}/api/generate \
   -H "Content-Type: application/json" \
   -d "${DATA}" | jq -c '{ response }'



