# JS Docker GenAI Stack 🟡🐳🤖🦜🔗🦙 with 🧠 "memory"

```bash
HTTP_PORT=8888 LLM=tinydolphin OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up

HTTP_PORT=8888 LLM=phi3:mini OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up

HTTP_PORT=8888 LLM=qwen2:0.5b OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up
```


```
rouge/replete-coder-qwen2-1.5b:Q8       00f379efa80d    1.9 GB  2 days ago  
stablelm2:latest                        714a6116cffa    982 MB  7 days ago  
stable-code:latest                      37681d29a55a    1.6 GB  7 days ago  
starcoder:3b                            847e5a7aa26f    1.8 GB  8 days ago  
starcoder:1b                            77e6c46054d9    726 MB  8 days ago  
codegemma:7b                            0c96700aaada    5.0 GB  8 days ago  
codegemma:2b                            757806401a36    1.6 GB  8 days ago  
deepseek-coder:1.3b                     3ddd2d3fc8d2    776 MB  8 days ago  
tinyllama:latest                        2644915ede35    637 MB  11 days ago 
gemma:2b                                b50d6c999e59    1.7 GB  2 weeks ago 
deepseek-coder:latest                   3ddd2d3fc8d2    776 MB  2 weeks ago 
deepseek-coder:instruct                 3ddd2d3fc8d2    776 MB  2 weeks ago 
mistral:latest                          2ae6f6dd7a3d    4.1 GB  3 weeks ago 
qwen2:0.5b                              6f48b936a09f    352 MB  3 weeks ago 
granite-code:3b                         9e9883ba2cd4    2.0 GB  4 weeks ago 
phi3:mini                               64c1188f2485    2.4 GB  4 weeks ago 
qwen:0.5b                               b5dc5e784f2a    394 MB  6 weeks ago 
tinydolphin:latest                      0f9dd11f824c    636 MB  7 weeks ago 
all-minilm:latest                       1b226e2802db    45 MB   2 months ago
```



> 👋 you can use this project with [Visual Studio Code Dev Containers](https://code.visualstudio.com/docs/devcontainers/containers). Take a look at the `.devcontainer.json` file. The Docker image is defined int this repository [https://github.com/genai-for-all/javascript-workspace](https://github.com/genai-for-all/javascript-workspace).

> ✋ in case of problem like this one: "`permission denied while trying to connect to the Docker daemon socket at unix:///var/run/docker.sock`" when you start Docke compose from the container, run this command: `sudo chmod 666 /var/run/docker.sock`

## Run all in containers

```bash
HTTP_PORT=8888 LLM=deepseek-coder OLLAMA_BASE_URL=http://ollama:11434 docker compose --profile container up
```
> The first time only, you must wait for the complete downloading of the model.

## Use the native Ollama install (like on macOS)

> To do for the first time only:
```bash
LLM=deepseek-coder
ollama pull ${LLM}

LLM=deepseek-coder:6.7b
ollama pull ${LLM}
```

```bash
HTTP_PORT=8888 LLM=deepseek-coder OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up

HTTP_PORT=8888 LLM=deepseek-coder:6.7b OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up
```

## 🖐️ Ask for writting a Golang Tutorial with simple source code examples

**System**:
Your task is to help people to learn programming languages

### For a concise and focused tutorial:

**User**:
Write a Golang tutorial for beginners in the form of a list, with each item containing a concept explained in plain English and a corresponding code example demonstrating its use.


```bash
HTTP_PORT=8888 LLM=phi3:mini OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up
```

## Use the GPU from the Ollama container on Linux or Windows

> 🚧 This is a work in progress

## Query Ollama

```bash
curl -H "Content-Type: application/json" http://localhost:8080/prompt \
-d '{
  "question": "what are structs in Golang?"
}'
```

## Rebuild the WebApp image

> All in containers
```bash
HTTP_PORT=8888 LLM=deepseek-coder OLLAMA_BASE_URL=http://ollama:11434 docker compose --profile container up --build
```

> Use the Ollama local install (like on macOS)
```bash
HTTP_PORT=8888 LLM=deepseek-coder OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp up --build
```

## Development mode

For developping the application, use the `watch` command of Docker Compose

> All in containers
```bash
HTTP_PORT=8888 LLM=deepseek-coder OLLAMA_BASE_URL=http://ollama:11434 docker compose --profile container watch
```
> Use the Ollama local install (like on macOS)
```bash
HTTP_PORT=8888 LLM=deepseek-coder OLLAMA_BASE_URL=http://host.docker.internal:11434 docker compose --profile webapp watch
```

## FAQ

> **good to know:** ✋ How to fix `permission denied while trying to connect to the Docker daemon socket at unix:///var/run/docker.sock`:
> ```bash
> sudo chmod 666 /var/run/docker.sock
> ```