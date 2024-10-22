# riviera LLM

## Initialize

```bash
ollama show qwen:0.5b --modelfile > riviera.modelfile
```

## Add some data to riviera.modelfile

> copy the content of `data.txt`

You can change the parameters: ``
> https://github.com/ollama/ollama/blob/main/docs/modelfile.md#parameter

```bash
PARAMETER temperature 0.0
PARAMETER top_k 10
PARAMETER top_p 0.5
PARAMETER repeat_last_n 2
PARAMETER repeat_penalty 2
```

> - `top_k`: Reduces the probability of generating nonsense. A higher value (e.g. 100) will give more diverse answers, while a lower value (e.g. 10) will be more conservative. (Default: 40)
> - `top_p`: Works together with top-k. A higher value (e.g., 0.95) will lead to more diverse text, while a lower value (e.g., 0.5) will generate more focused and conservative text. (Default: 0.9)
> - `repeat_last_n`: Sets how far back for the model to look back to prevent repetition. (Default: 64, 0 = disabled, -1 = num_ctx)
> - `repeat_penalty`: Sets how strongly to penalize repetitions. A higher value (e.g., 1.5) will penalize repetitions more strongly, while a lower value (e.g., 0.9) will be more lenient. (Default: 1.1)	

## Create a new model

```bash
ollama create riviera --file riviera.modelfile
ollama list
```