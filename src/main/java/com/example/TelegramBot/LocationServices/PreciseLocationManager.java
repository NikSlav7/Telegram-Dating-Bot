package com.example.TelegramBot.LocationServices;


import com.example.TelegramBot.Domains.Location;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Component
@EnableAsync
public class PreciseLocationManager {

    private static final String GEOCODING_API_ACCESS_KEY = "3957d1f4f27c1e3c2efbaf694202fd92";





    @Async
    public void setLocationCityAndRegionName(Location location) throws IOException, URISyntaxException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(new URI(getApiRequestLink(location.getLongitude(), location.getLatitude())));
        HttpResponse response = client.execute(getRequest);

        JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()), "UTF-8");

        setCityAndRegionName(location, jsonObject);


    }

    private void setCityAndRegionName(Location location, JSONObject apiRequestResponse){
        location.setCityName((String) apiRequestResponse.getJSONArray("data").getJSONObject(0).get("county"));
        location.setRegionName((String) apiRequestResponse.getJSONArray("data").getJSONObject(0).get("region"));
        location.setCountryName((String) apiRequestResponse.getJSONArray("data").getJSONObject(0).get("country"));
    }

    private  String getApiRequestLink(double longitude, double latitude) {
        return "http://api.positionstack.com/v1/reverse?access_key=" + GEOCODING_API_ACCESS_KEY + "&query=" + latitude + "," + longitude;
    }
}
