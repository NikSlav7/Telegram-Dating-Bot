package com.example.TelegramBot.LocationServices;


import com.example.TelegramBot.Domains.Location;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

@Component
@EnableAsync
public class PreciseLocationManager {

    private static final String GEOCODING_API_ACCESS_KEY = "3957d1f4f27c1e3c2efbaf694202fd92";

    private final UserProfileRepository userProfileRepository;


    @Autowired
    public PreciseLocationManager(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }


    @Async
    public void setLocationCityAndRegionName(Location location, UserProfile userProfile) throws IOException, URISyntaxException {
        HttpClient client = HttpClientBuilder.create().build();
        URL url = new URL(getApiRequestLink(location.getLongitude(), location.getLatitude()));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        int i = 0;
        char[] ch = new char[1024];
        StringBuilder response = new StringBuilder();
        Scanner scanner = new Scanner(urlConnection.getURL().openStream());
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        JSONObject jsonObject = new JSONObject(response.toString());
        setCityAndRegionName(location, jsonObject);
        userProfileRepository.save(userProfile);

    }

    private void setCityAndRegionName(Location location, JSONObject apiRequestResponse){
        location.setCityName(apiRequestResponse.getJSONArray("data").getJSONObject(0).getString("county"));
        location.setRegionName(apiRequestResponse.getJSONArray("data").getJSONObject(0).getString("region"));
        location.setCountryName(apiRequestResponse.getJSONArray("data").getJSONObject(0).getString("country"));
    }

    private  String getApiRequestLink(double longitude, double latitude) {
        return "http://api.positionstack.com/v1/reverse?access_key=" + GEOCODING_API_ACCESS_KEY + "&query=" + latitude + "," + longitude;
    }

}
