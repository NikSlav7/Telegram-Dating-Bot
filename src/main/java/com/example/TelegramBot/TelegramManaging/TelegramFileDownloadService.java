package com.example.TelegramBot.TelegramManaging;


import com.amazonaws.services.dynamodbv2.xspec.S;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class TelegramFileDownloadService {


    private final TelegramBot telegramBot;

    public TelegramFileDownloadService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    //Returns file path
    public File downloadFile(String fileId, long ownerUserId) throws TelegramApiException, IOException, InterruptedException {

        InputStream inputStream = new URL("https://api.telegram.org/file/bot" + telegramBot.getBotToken() + "/" + getFilePath(fileId)).openStream();

    File file = new File(generateFileName(ownerUserId));
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    byte[] buffer = new byte[1024];
    int c = 0;
    while ((c = inputStream.read(buffer)) != -1) {
        fileOutputStream.write(buffer, 0, c);
    }
    fileOutputStream.close();
    inputStream.close();
    return file;
    }


    private String getFilePath(String fileId) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(getFilePathRequestURL(fileId))).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonObject = new JSONObject(response.body());

        return jsonObject.getJSONObject("result").getString("file_path");
    }

    private String getFilePathRequestURL(String fileId) {
        return "https://api.telegram.org/bot" + telegramBot.getBotToken() + "/getFile?file_id=" + fileId;
    }

    private String generateFileName(long userId) {
        return userId + "profile pic.jpeg";
    }
}
