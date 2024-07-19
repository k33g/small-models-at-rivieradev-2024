#!/bin/bash

. "../lib/parakeet.sh"

OLLAMA_BASE_URL=${OLLAMA_BASE_URL:-http://localhost:11434}

read -r -d '' SYSTEM_CONTENT <<- EOM
You are a useful AI agent. 
Your job is to analyse meeting transcripts and then give information about these transcripts. 
EOM

read -r -d '' CONTEXT_CONTENT <<- EOM
### Meeting Transcript: WASI Preview 1

**Date:** July 3, 2024  
**Time:** 10:00 AM  
**Participants:** John, Kate, Bob

---

**John:** Good morning, everyone. Let's get started with today's meeting about WASI Preview 1. Thanks for joining, Kate and Bob. How are you both doing?

**Kate:** Good morning, John. I'm doing well, thanks. Excited to discuss the new features and improvements in WASI Preview 1.

**Bob:** Hi, everyone. I'm doing great, too. Looking forward to our discussion.

**John:** Excellent. Let's dive right in. So, WASI Preview 1 is the latest step in evolving the WebAssembly System Interface. It brings some key enhancements and changes that we need to go over. Kate, could you start by summarizing the major updates?

**Kate:** Sure, John. WASI Preview 1 includes several significant updates. First, it introduces new capabilities-based security features, which allow more fine-grained control over resource access. This helps in creating more secure and isolated environments for WebAssembly modules.

**Bob:** That sounds promising. How does it handle backward compatibility with the previous versions of WASI?

**Kate:** Good question, Bob. WASI Preview 1 aims to maintain backward compatibility where possible, but there are some breaking changes. The transition should be smooth for most users, though, thanks to the detailed migration guides provided.

**John:** Right. And alongside security, performance improvements were a major focus in this release, correct?

**Kate:** Exactly, John. There have been optimizations in the I/O system, which should result in better performance for file and network operations. These changes are designed to reduce latency and improve overall efficiency.

**Bob:** That's really encouraging. Are there any new modules or APIs introduced in this preview?

**Kate:** Yes, Bob. A few new modules have been added, such as the wasi-filesystem and wasi-socket modules, which offer more comprehensive and flexible ways to handle file and network operations, respectively.

**John:** Thanks for the summary, Kate. Bob, do you have any specific concerns or areas you'd like to focus on regarding these updates?

**Bob:** One thing I'm particularly interested in is how the new security features will impact our current deployments. We rely heavily on WASI for our microservices, and security is a top priority.

**Kate:** That's a valid concern. The capabilities-based security model should provide you with more control over what each module can access. It might require some initial adjustments to your configurations, but the end result will be a more secure setup.

**John:** Indeed. It's crucial that we carefully review our current permissions and adjust them according to the new capabilities system. Kate, do we have any resources or documentation that can help with this transition?

**Kate:** Yes, John. The WASI team has put together comprehensive documentation and examples. There are also community forums and support channels where we can ask for help and share experiences.

**Bob:** That's great to hear. Having robust support and documentation will definitely ease the transition.

**John:** Absolutely. Moving forward, let's allocate some time this week to go through our deployments and identify any potential changes we need to make. Kate, could you take the lead on this and schedule a follow-up meeting?

**Kate:** Sure thing, John. I'll prepare an agenda and set up a meeting for later this week. 

**John:** Perfect. Anything else we need to discuss today regarding WASI Preview 1?

**Bob:** I think we've covered the major points. I'm looking forward to testing out the new features.

**Kate:** Same here. It will be interesting to see the performance improvements in action.

**John:** Alright, let's wrap this up. Thanks for the productive discussion, Kate and Bob. Looking forward to our follow-up meeting. Have a great day, everyone.

**Kate:** Thanks, John. You too.

**Bob:** Thanks, everyone. Talk to you soon.

---

**Meeting adjourned at 10:30 AM.**
EOM

# Make a summary of the transcript above
# [Summary] Explain the transcript above
read -r -d '' USER_CONTENT <<- EOM
Explain the above transcript
EOM
#[Summary] Explain the above transcript


SYSTEM_CONTENT=$(Sanitize "${SYSTEM_CONTENT}")
CONTEXT_CONTENT=$(Sanitize "${CONTEXT_CONTENT}")
USER_CONTENT=$(Sanitize "${USER_CONTENT}")

#MODEL="tinyllama"
MODEL="tinydolphin"

read -r -d '' DATA <<- EOM
{
  "model":"${MODEL}",
  "options": {
    "temperature": 0.3,
    "repeat_last_n": 2,
    "top_p": 0.5,
    "top_k": 0.5
  },
  "messages": [
    {"role":"system", "content": "${SYSTEM_CONTENT}"},
    {"role":"system", "content": "${CONTEXT_CONTENT}"},
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
