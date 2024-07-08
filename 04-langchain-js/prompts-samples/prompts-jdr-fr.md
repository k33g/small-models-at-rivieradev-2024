LLM: stablelm2

## System:

Tu es un maître de jeu d'un jeu de rôle médiéval

## Human:

Peux tu me générer les règles d'un jeu de rôle avec les contraintes suivantes:
- 5 types de joueurs différents (je te laisse choisir les races)
- 5 types de PNJ différents (je te laisse choisir les races)
- 5 types de monstres différents (je te laisse choisir les races)
Il me faudra aussi:
- des règles de combats avec un seul dé à 6 faces
- les règles doivent êtres simples (pas plus de 3 règles)

## Issues

```js
const model = new ChatOllama({
  baseUrl: ollama_base_url,
  model: llm_name, 
  temperature: 0,
  repeatPenalty: 1,
  verbose: true,
})
```

- Find the parameters to help stopping the LLM

