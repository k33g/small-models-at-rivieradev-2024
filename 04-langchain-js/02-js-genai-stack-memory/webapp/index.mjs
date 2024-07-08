import Fastify from 'fastify'
import path from 'path'
import fastifyStatic from '@fastify/static'

import { ChatOllama } from "@langchain/community/chat_models/ollama"
import { StringOutputParser } from "@langchain/core/output_parsers"

import { RunnableWithMessageHistory } from "@langchain/core/runnables"

import { ConversationSummaryMemory } from "langchain/memory"

import { 
  SystemMessagePromptTemplate, 
  HumanMessagePromptTemplate, 
  MessagesPlaceholder, // 👋
  ChatPromptTemplate 
} from "@langchain/core/prompts"

//let http_port = process.env.HTTP_PORT
let ollama_base_url = process.env.OLLAMA_BASE_URL
let llm_name = process.env.LLM

const modelOptions = {
  baseUrl: ollama_base_url,
  model: llm_name, 
  temperature: 0.5,
  repeatPenalty: 1.1,
  verbose: true,
}

const model = new ChatOllama(modelOptions)

/*
repeatPenalty: (default 1.1)
Sets how strongly to penalize repetitions. 
A higher value (e.g., 1.5) will penalize repetitions more strongly, 
while a lower value (e.g., 0.9) will be more lenient.
*/

/*
stop: 🤔
Sets the stop sequences to use. 
When this pattern is encountered the LLM will stop generating text and return. 
Multiple stop patterns may be set by specifying multiple separate stop parameters in a modelfile.
*/

/*
repeat-last-n: 🤔
Sets how far back for the model to look back to prevent repetition. (Default: 64, 0 = disabled, -1 = num_ctx)
*/

/* temperature: (default 0.8)
The temperature of the model. 
Increasing the temperature will make the model answer more creatively.
*/


const memory = new ConversationSummaryMemory({
  memoryKey: "history",
  llm: model,
})

var controller = new AbortController()

const fastify = Fastify({
  logger: true
})

// Serve public/index.html
fastify.register(fastifyStatic, {
  root: path.join(import.meta.dirname, 'public'),
})

const { ADDRESS = '0.0.0.0', HTTP_PORT = '8080' } = process.env;

fastify.get('/model', async (request, reply) => {
  return modelOptions
})

fastify.delete('/clear-history', async (request, reply) => {
  console.log("👋 clear conversation summary")
  memory.clear()
  return "👋 conversation summary is empty"
})

fastify.get('/message-history', async (request, reply) => {
  return memory.chatHistory.getMessages()
})

fastify.delete('/cancel-request', async (request, reply) => {
  console.log("👋 cancel request")
  controller.abort()
  // recreate the abort controller
  var controller = new AbortController()
  return "👋 request aborted"
})

fastify.post('/prompt', async (request, reply) => {
  const system = request.body["system"]
  const question = request.body["question"]

  const prompt = ChatPromptTemplate.fromMessages([
    SystemMessagePromptTemplate.fromTemplate(`{system}`),
    new MessagesPlaceholder("history"),
    HumanMessagePromptTemplate.fromTemplate(
      `{question}`
    )
  ])

  
  const outputParser = new StringOutputParser()

  model.bind({ signal: controller.signal })

  const chain = prompt.pipe(model).pipe(outputParser)
  
  const chainWithHistory = new RunnableWithMessageHistory({
    runnable: chain,
    memory: memory,
    getMessageHistory: (_sessionId) => memory.chatHistory,
    inputMessagesKey: "question",
    historyMessagesKey: "history",
  })

  const config = { configurable: { sessionId: "1" } }

  let stream = await chainWithHistory.stream({
    system: system,
    question: question,
  }, config)
  
  reply.header('Content-Type', 'application/octet-stream')
  return reply.send(stream)
  
})

const start = async () => {
  try {
    await fastify.listen({ host: ADDRESS, port: parseInt(HTTP_PORT, 10)  })
  } catch (err) {
    fastify.log.error(err)
    process.exit(1)
  }
  console.log(`🌍 Server listening at ${ADDRESS}:${HTTP_PORT}`)

}
start()

