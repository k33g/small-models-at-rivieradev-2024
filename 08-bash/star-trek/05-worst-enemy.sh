#!/bin/bash

. "../lib/parakeet.sh"

OLLAMA_BASE_URL=${OLLAMA_BASE_URL:-http://localhost:11434}

MODEL="tinydolphin" 

read -r -d '' SYSTEM_CONTENT <<- EOM
You are an expert of the Star Trek universe.
Use the provided context to answer question about Star Trek.
EOM

read -r -d '' PREVIOUS_USER_CONTENT <<- EOM
[brief] Who is Jean-Luc Picard?
EOM

read -r -d '' CONTEXT_CONTENT <<- EOM
<context>
The worst enemy of Jean-Luc Picard are the Borg.
The catch phrase of the Borg is 'Resistance is futile'.
</context>
EOM

read -r -d '' USER_CONTENT <<- EOM
[brief] Who is his worst enemy? and what is his catch phrase?
EOM

SYSTEM_CONTENT=$(Sanitize "${SYSTEM_CONTENT}")
CONTEXT_CONTENT=$(Sanitize "${CONTEXT_CONTENT}")
PREVIOUS_USER_CONTENT=$(Sanitize "${PREVIOUS_USER_CONTENT}")
USER_CONTENT=$(Sanitize "${USER_CONTENT}")


read -r -d '' DATA <<- EOM
{
  "model":"${MODEL}",
  "options": {
    "temperature": 0.2,
    "repeat_last_n": 2
  },
  "messages": [
    {"role":"system", "content": "${SYSTEM_CONTENT}"},
    {"role":"system", "content": "${CONTEXT_CONTENT}"},
    {"role":"user", "content": "${PREVIOUS_USER_CONTENT}"},
    {"role":"user", "content": "${USER_CONTENT}"}
  ],
  "stream": false,
  "raw": false
}
EOM

#echo $DATA

jsonResult=$(Chat "${OLLAMA_BASE_URL}" "${DATA}")

messageContent=$(echo "${jsonResult}" | jq '.message.content')

#echo "${messageContent}" 
echo $(Sanitize "${messageContent}")
