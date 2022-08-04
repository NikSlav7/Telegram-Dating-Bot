package com.example.TelegramBot.AmazonServices;


import com.amazonaws.services.s3.AmazonS3;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.File;

@Component
public class AmazonPicturesManager {


    //The amazon bucket name. Should not be changed
    public static final String AMAZON_PICTURES_BUCKET_NAME = "telegram-profile-pictures";

    private final AmazonS3 amazonS3;


    @Autowired
    public AmazonPicturesManager(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    //Saving the profile picture into the amazon cloud
    public void savePictureIntoCloud(java.io.File file, String fileName) {
        amazonS3.putObject(AMAZON_PICTURES_BUCKET_NAME, fileName, file);
    }
}
