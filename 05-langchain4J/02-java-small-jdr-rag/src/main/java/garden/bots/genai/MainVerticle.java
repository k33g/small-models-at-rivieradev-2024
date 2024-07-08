package garden.bots.genai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.File;
import java.util.*;


import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.*;

import static dev.langchain4j.data.message.SystemMessage.systemMessage;

public class MainVerticle extends AbstractVerticle {

  private boolean cancelRequest ;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    // ----------------------------------------
    // 📝 Load the document to use it for RAG
    // ----------------------------------------
    System.out.println("🤖 Execution directory:" + System.getProperty("user.dir"));
    File directoryPath = new File(System.getProperty("user.dir")+"/documents");
    TextDocumentParser documentParser = new TextDocumentParser();
    Document document = loadDocument(directoryPath.toPath()+"/rules.md", documentParser);
    System.out.println("🤖 Document is loaded: " + document);

    /*
      We need to split this document into smaller segments, also known as "chunks."
      This approach allows us to send only relevant segments to the LLM in response to a user query,
      rather than the entire document. 
    */
    // ----------------------------------------
    // 📝📝📝 Create chunks from the document
    // ----------------------------------------
    DocumentSplitter splitter = DocumentSplitters.recursive(1536, 128);
    List<TextSegment> segments = splitter.split(document);

    System.out.println("🧩 chunks/segments: " + segments);

    /*
      Now, we need to embed (also known as "vectorize") these segments.
      Embedding is needed for performing similarity searches.
      LLM embeddings are numerical representations of words or text captured by the LLM, 
      encoding their meaning and relationships in a high-dimensional space.
    */
    // ----------------------------------------
    // 📙 Create embeddings
    // ----------------------------------------
    EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

    /*
     * BgeSmallEnV15QuantizedEmbeddingModel is an in-process embedding model
     * LangChain4j offers a set of in-process embedding models 
     * that you can leverage for various tasks without relying on external APIs. 
     * These models are stored in the Open Neural Network Exchange (ONNX) format,
     *  enabling them to run directly within your Java application.
     */
 
    List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

    System.out.println("📙 embeddings:" + embeddings);

    /*
      Next, we will store these embeddings in an embedding store (also known as a "vector database").
      This store will be used to search for relevant segments during each interaction with the LLM.
      For simplicity, this example uses an in-memory embedding store.
    */
    // ----------------------------------------
    // 📦 Store embeddings in a vector db
    // ----------------------------------------
    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    embeddingStore.addAll(embeddings, segments);

    System.out.println("📦 embedding store: " + embeddingStore);

    // 🔎 The content retriever is responsible for retrieving relevant content based on a user query.
    ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(10) // on each interaction we will retrieve the 10 most relevant segments
            .minScore(0.8) // we want to retrieve segments at least somewhat similar to user query
            .build();


    var llmBaseUrl = Optional.ofNullable(System.getenv("OLLAMA_BASE_URL")).orElse("http://localhost:11434");
    var modelName = Optional.ofNullable(System.getenv("LLM")).orElse("deepseek-coder");

    var staticPath = Optional.ofNullable(System.getenv("STATIC_PATH")).orElse("/*");
    var httpPort = Optional.ofNullable(System.getenv("HTTP_PORT")).orElse("8888");

    // Using the Ollama Streaming Chat Model
    StreamingChatLanguageModel streamingModel = OllamaStreamingChatModel.builder()
      .baseUrl(llmBaseUrl)
      .modelName(modelName).temperature(0.8).repeatPenalty(1.0)
      .build();

    var memory = MessageWindowChatMemory.withMaxMessages(5);


    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    // Serving static resources
    var staticHandler = StaticHandler.create();
    staticHandler.setCachingEnabled(false);
    router.route(staticPath).handler(staticHandler);

    router.delete("/clear-history").handler(ctx -> {
      memory.clear();
      ctx.response()
        .putHeader("content-type", "text/plain;charset=utf-8")
        .end("👋 conversation memory is empty");
    });

    router.get("/message-history").handler(ctx -> {
      var strList = memory.messages().stream().map(msg -> msg.toString());
      JsonArray messages = new JsonArray(strList.toList());
      ctx.response()
        .putHeader("Content-Type", "application/json;charset=utf-8")
        .end(messages.encodePrettily());
    });

    router.delete("/cancel-request").handler(ctx -> {
      cancelRequest = true;
      ctx.response()
        .putHeader("content-type", "text/plain;charset=utf-8")
        .end("👋 request aborted");
    });

    router.get("/model").handler(ctx -> {
      ctx.response()
        .putHeader("Content-Type", "application/json;charset=utf-8")
        .end("{\"model\":\""+modelName+"\",\"url\":\""+llmBaseUrl+"\"}");
    });


    router.post("/prompt").handler(ctx -> {

      var question = ctx.body().asJsonObject().getString("question");
      var systemContent = ctx.body().asJsonObject().getString("system");

      // ----------------------------------------
      // 📝 Prepare message for the prompt
      // ----------------------------------------
      SystemMessage systemInstructions = systemMessage(systemContent);
      UserMessage humanMessage = UserMessage.userMessage(question);

      // ----------------------------------------
      // 🔎 search similarities in the vector db
      // ----------------------------------------
      var similarities = contentRetriever.retrieve(new Query(question));
      System.out.println("🔎 similarities: " + similarities);

      // ----------------------------------------
      // 📝📝📝 Create context with similarities
      // ----------------------------------------
      StringBuilder content = new StringBuilder();
      content.append("<content>");
      similarities.forEach(similarity -> {
        content.append("<doc>"+similarity.toString()+"</doc>");
      });
      content.append("</content>");

      SystemMessage contextMessage = systemMessage(content.toString());

      // ----------------------------------------
      // 📝 Create the prompt
      // ----------------------------------------
      List<ChatMessage> messages = new ArrayList<>();
      messages.add(systemInstructions);
      messages.addAll(memory.messages());
      messages.add(contextMessage);
      messages.add(humanMessage);

      // ----------------------------------------
      // 🧠 Update the memory of the conversation
      // ----------------------------------------
      memory.add(humanMessage);

      HttpServerResponse response = ctx.response();

      response
        .putHeader("Content-Type", "application/octet-stream")
        .setChunked(true);

      // ----------------------------------------
      // 🤖 Generate the completion (stream)
      // ----------------------------------------
      streamingModel.generate(messages, new StreamingResponseHandler<AiMessage>() {
        @Override
        public void onNext(String token) {
          if (cancelRequest) {
            cancelRequest = false;
            throw new RuntimeException("🤬 Shut up!");
          }

          System.out.println("New token: '" + token + "'");
          response.write(token);
        }

        @Override
        public void onComplete(Response<AiMessage> modelResponse) {
          memory.add(modelResponse.content());
          System.out.println("Streaming completed: " + modelResponse);
          response.end();

        }

        @Override
        public void onError(Throwable throwable) {
          throwable.printStackTrace();
        }
      });

    });

    // Create an HTTP server
    var server = vertx.createHttpServer();

    //! Start the HTTP server
    server.requestHandler(router).listen(Integer.parseInt(httpPort), http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("GenAI Vert-x server started on port " + httpPort);
      } else {
        startPromise.fail(http.cause());
      }
    });

  }
}