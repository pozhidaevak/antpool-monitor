package notifications;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import config.Configuration;

public class TelegramNotifier implements INotifier {
    private String chatId;
    private String apiKey;
    private  TelegramBot bot;
    public void send (String message) {
        if (bot == null) {
            bot = new TelegramBot(apiKey);
        }
        SendMessage request = new SendMessage(chatId, message)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true)
                .replyToMessageId(1)
                .replyMarkup(new ForceReply());;
        SendResponse response = bot.execute(request);
        if (!response.isOk()) {
            System.out.println("Telegram Request error: " + response.message().toString());
        }
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
