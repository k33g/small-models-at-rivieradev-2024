# Simple demos

```bash
OLLAMA_BASE_URL=http://ollama-service:11434 LLM=tinyllama \
docker compose --profile ollma-in-docker up
```

> use the local Ollama instance
```bash
OLLAMA_BASE_URL=http://host.docker.internal:11434 LLM=tinyllama \
docker compose --profile application up
```





