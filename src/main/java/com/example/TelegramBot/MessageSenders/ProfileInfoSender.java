package com.example.TelegramBot.MessageSenders;


import com.example.TelegramBot.AmazonServices.AmazonPicturesManager;
import com.example.TelegramBot.Domains.Location;
import com.example.TelegramBot.Domains.UserProfile;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.User;
import org.checkerframework.checker.units.qual.A;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileInfoSender {



    ;


    public static SendPhoto sendProfileInfo(long sendChatId, UserProfile userProfile, File file) throws IOException {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder
                .append("\n" + userProfile.getFirstName() + " " + userProfile.getSecondName())
                .append("\n" + Period.between(LocalDate.ofInstant(userProfile.getDateOfBirth().toInstant(), ZoneId.systemDefault()), LocalDate.now()).getYears() + " y.o")
                .append("\n\n" + getLocation(userProfile.getUserDefaultLocation()))
                .append("\n\n" + "Hobbies: \n" + userProfile.getHobbies())
                .append("\n\n" + "Additional Info: \n" + userProfile.getAdditionalInfo());

        SendPhoto sendPhoto = new SendPhoto();
        //Getting profile picture;
        sendPhoto.setPhoto(new InputFile(file));
        sendPhoto.setCaption(messageBuilder.toString());
        sendPhoto.setChatId(sendChatId);
        return sendPhoto;

    }

    private static String getLocation(Location location) {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getCityName() != null ? location.getCityName() + ", " : "");
        builder.append(location.getRegionName() != null ? location.getRegionName() + ", " : "");
        builder.append(location.getCountryName() != null ? location.getCountryName() : "");

        return builder.toString();
    }


}
