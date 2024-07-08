package garden.bots.genai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.*;
import java.util.stream.Stream;

import static dev.langchain4j.data.message.SystemMessage.systemMessage;

public class MainVerticle extends AbstractVerticle {

  private boolean cancelRequest ;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {


    var llmBaseUrl = Optional.ofNullable(System.getenv("OLLAMA_BASE_URL")).orElse("http://localhost:11434");
    var modelName = Optional.ofNullable(System.getenv("LLM")).orElse("deepseek-coder");

    var staticPath = Optional.ofNullable(System.getenv("STATIC_PATH")).orElse("/*");
    var httpPort = Optional.ofNullable(System.getenv("HTTP_PORT")).orElse("8888");

    //https://docs.langchain4j.dev/apidocs/dev/langchain4j/model/ollama/OllamaStreamingChatModel.html
    StreamingChatLanguageModel streamingModel = OllamaStreamingChatModel.builder()
      .baseUrl(llmBaseUrl)
      .modelName(modelName).temperature(0.0).repeatPenalty(1.0)
      .build();

    // ----------------------------------------
    // 🧠 Keep the memory of the conversation
    // ----------------------------------------
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
      Stream<String> strList = memory.messages().stream().map(msg -> msg.toString());
      JsonArray messages = new JsonArray(strList.toList());
      ctx.response()
        .putHeader("Content-Type", "application/json;charset=utf-8")
        .end(messages.encodePrettily());
    });

    router.get("/model").handler(ctx -> {
      ctx.response()
        .putHeader("Content-Type", "application/json;charset=utf-8")
        .end("{\"model\":\""+modelName+"\",\"url\":\""+llmBaseUrl+"\"}");
    });


    router.delete("/cancel-request").handler(ctx -> {
      cancelRequest = true;
      ctx.response()
        .putHeader("content-type", "text/plain;charset=utf-8")
        .end("👋 request aborted");
    });

    router.post("/prompt").handler(ctx -> {

      var question = ctx.body().asJsonObject().getString("question");
      var systemContent = ctx.body().asJsonObject().getString("system");
      var contextContent = ctx.body().asJsonObject().getString("context");

      // ----------------------------------------
      // 📝 Build the prompt
      // ----------------------------------------
      SystemMessage systemInstructions = systemMessage(systemContent);
      SystemMessage contextMessage = systemMessage(contextContent);
      UserMessage humanMessage = UserMessage.userMessage(question);

      List<ChatMessage> messages = new ArrayList<>();
      messages.add(systemInstructions);
      messages.addAll(memory.messages());
      messages.add(contextMessage);
      messages.add(humanMessage);

      System.out.println(messages);

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