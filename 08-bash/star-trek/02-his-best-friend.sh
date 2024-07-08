#!/bin/bash

. "../lib/parakeet.sh"

OLLAMA_BASE_URL=${OLLAMA_BASE_URL:-http://localhost:11434}

MODEL="tinydolphin" 

read -r -d '' SYSTEM_CONTENT <<- EOM
You are an expert of the Star Trek universe. 
EOM

read -r -d '' USER_CONTENT <<- EOM
[brief] Who is his best friend?
EOM

SYSTEM_CONTENT=$(Sanitize "${SYSTEM_CONTENT}")
USER_CONTENT=$(Sanitize "${USER_CONTENT}")


read -r -d '' DATA <<- EOM
{
  "model":"${MODEL}",
  "options": {
    "temperature": 0.5,
    "repeat_last_n": 2
  },
  "messages": [
    {"role":"system", "content": "${SYSTEM_CONTENT}"},
    {"role":"user", "content": "${USER_CONTENT}"}
  ],
  "stream": false,
  "raw": false
}
EOM

jsonResult=$(Chat "${OLLAMA_BASE_URL}" "${DATA}")

messageContent=$(echo "${jsonResult}" | jq '.message.content')

#echo "${messageContent}" 
echo $(Sanitize "${messageContent}")