# Simple demos

```bash
OLLAMA_BASE_URL=http://ollama-service:11434 LLM=deepseek-coder:instruct \
docker compose --profile ollma-in-docker up
```

> use the local Ollama instance
```bash
OLLAMA_BASE_URL=http://host.docker.internal:11434 LLM=deepseek-coder:instruct \
docker compose --profile application up

OLLAMA_BASE_URL=http://host.docker.internal:11434 LLM=deepseek-coder:1.3b \
docker compose --profile application up
```





