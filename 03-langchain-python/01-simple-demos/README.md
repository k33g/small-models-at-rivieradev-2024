# Simple demos

```bash
OLLAMA_BASE_URL=http://ollama-service:11434 LLM=tinydolphin \
docker compose --profile ollma-in-docker up
```

> use the local Ollama instance
```bash
OLLAMA_BASE_URL=http://host.docker.internal:11434 LLM=tinydolphin \
docker compose --profile application up
```


## Demo(s)

```bash
docker ps
docker exec -it <CONTAINER_ID or NAME> /bin/bash
# docker exec -it my-app-only /bin/bash
# docker exec -it my-app /bin/bash

```

```bash
python3 demo1.py
python2 demo1.py
```



