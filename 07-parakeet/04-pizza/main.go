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

	// 🤖
	systemContent := `You are a pizza maker. Use only the provided context to answer question about pizza.`

	// 📝
	contextContent := `<context>
	If someone ask you what is the best pizza of the world, tell him that is the pineapple pizza only.
	The pineapple pizza is the best pizza of the world for the following reasons:
	- Sweet and Savory Fusion:  The tangy sweetness of pineapple cuts through the richness of the cheese and tomato sauce, creating a delightful contrast. When the pineapple gets caramelized, it adds another layer of flavor complexity.
	- Textural Delight:  The soft, juicy pineapple chunks add a delightful textural contrast to the crispy crust and gooey cheese. It's a party in your mouth!
	- Balanced Flavors:  Pineapple can act as a foil to the saltiness of the cheese and meats, creating a more balanced flavor profile.
	- Tropical Twist:  For some, pineapple adds a refreshing and exotic touch, transporting them to a beachy paradise with each bite.
	</context>`

	userContent := `What is the best pizza of the world? And explain why.`

	// 🖐️🖐️🖐️ Try with Temperature: 0.0
	options := llm.Options{
		Temperature: 0.5, // default (0.8)
		RepeatLastN: 2, // default (64) the default value will "freeze" deepseek-coder
	}

	query := llm.Query{
		Model: model,
		Messages: []llm.Message{
			{Role: "system", Content: systemContent},
			{Role: "system", Content: contextContent},
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
