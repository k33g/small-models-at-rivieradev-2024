package main

import (
	"fmt"
	"log"

	"github.com/parakeet-nest/parakeet/completion"
	"github.com/parakeet-nest/parakeet/llm"
)

func main() {
	ollamaUrl := "http://localhost:11434"
	//ollamaUrl := "http://host.docker.internal:11434"
	model := "qwen2:0.5b"

	systemContent := `You are a pizza maker.`

	userContent := `What is the best pizza of the world?`

	options := llm.Options{
		Temperature: 0.5, // default (0.8)
		RepeatLastN: 2, // default (64) the default value will "freeze" deepseek-coder
	}

	query := llm.Query{
		Model: model,
		Messages: []llm.Message{
			{Role: "system", Content: systemContent},
			{Role: "user", Content: userContent},
		},
		Options: options,
		Stream:  false,
	}

	_, err := completion.ChatStream(ollamaUrl, query,
		func(answer llm.Answer) error {
			fmt.Print(answer.Message.Content)
			return nil
		})

	if err != nil {
		log.Fatal("😡:", err)
	}
	fmt.Println()
}
