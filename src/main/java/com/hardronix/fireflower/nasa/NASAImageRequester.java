package com.hardronix.fireflower.nasa;

import org.apache.commons.collections4.iterators.LoopingListIterator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NASAImageRequester {

    private final String apiKey;
    private static final String EPIC_BASE_URL = "https://api.nasa.gov/EPIC/api/natural/images?api_key=";

    public NASAImageRequester(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public LoopingListIterator<EPICEntry> getEPICImages()
    {
        List<EPICEntry> epicEntries = new ArrayList<>();
        try {
            URL url = new URL(EPIC_BASE_URL + apiKey);
            Scanner scanner = new Scanner(url.openStream());
            String response = scanner.useDelimiter("\\Z").next();
            scanner.close();

            JSONArray entries = new JSONArray(response);

            for(Object object : entries)
            {
                String caption = ((JSONObject) object).getString("caption");
                String image = ((JSONObject) object).getString("image");
                String date = ((JSONObject) object).getString("date");

                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime localDate = LocalDateTime.parse(date, format);

                epicEntries.add(new EPICEntry(image, localDate, caption));
            }

            return new LoopingListIterator<>(epicEntries);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
