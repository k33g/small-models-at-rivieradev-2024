import Fastify from 'fastify'
import fastifyStatic from '@fastify/static'
import path from 'path'

import { ChatOllama } from "@langchain/community/chat_models/ollama"
import { StringOutputParser } from "@langchain/core/output_parsers"

import { 
  SystemMessagePromptTemplate, 
  HumanMessagePromptTemplate, 
  ChatPromptTemplate 
} from "@langchain/core/prompts"

let ollama_base_url = process.env.OLLAMA_BASE_URL
let llm_name = process.env.LLM

const model = new ChatOllama({
  baseUrl: ollama_base_url,
  model: llm_name, 
  temperature: 0,
  repeatPenalty: 1,
  verbose: false
})

const prompt = ChatPromptTemplate.fromMessages([
  SystemMessagePromptTemplate.fromTemplate(
    `You are an expert in computer programming.
     Please make friendly answer for the noobs.
     Add source code examples.
    `
  ),
  HumanMessagePromptTemplate.fromTemplate(
    `I need a clear explanation regarding my {question}.
    `
  )
])

const fastify = Fastify({
  logger: false
})

const { ADDRESS = '0.0.0.0', HTTP_PORT = '8080' } = process.env

// Serve public/index.html
fastify.register(fastifyStatic, {
  root: path.join(import.meta.dirname, 'public'),
})

fastify.post('/prompt', async (request, reply) => {
  const question = request.body["question"]
  const outputParser = new StringOutputParser()
  const chain = prompt.pipe(model).pipe(outputParser)

  let stream = await chain.stream({
    question: question,
  })

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
