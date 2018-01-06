package github.antmonitor.notifications;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TelegramNotifier implements IMonospaceNotifier {

  private static final Logger log = LogManager.getLogger(TelegramNotifier.class);
  private final String chatId;
  private final String apiKey;
  private final TelegramBot bot;
  private final int timeout;

  @Autowired
  public TelegramNotifier(@Value("${telegram.chatId}") String chatId,
      @Value("${telegram.apiKey}") String apiKey,
      @Value("${telegram.timeout}") int timeout) {
    this.chatId = chatId;
    this.apiKey = apiKey;
    this.timeout = timeout;

    //Set custom timeouts and initialize bot
    OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
        .writeTimeout(timeout, TimeUnit.SECONDS)
        .build();
    bot = new TelegramBot.Builder(apiKey).okHttpClient(client).build();
  }

  public void send(String message) {
    SendMessage request = new SendMessage(chatId, message)
        .parseMode(ParseMode.Markdown)
        .disableWebPagePreview(true);
    SendResponse response = bot.execute(request);
    if (!response.isOk()) {
      log.error("Telegram Request error: " + response.description());
    }
  }

  @Override
  public String monospace(String message) {
    return "```\n" + message + "\n```";
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getChatId() {
    return chatId;
  }

  public int getTimeout() {
    return timeout;
  }

}
