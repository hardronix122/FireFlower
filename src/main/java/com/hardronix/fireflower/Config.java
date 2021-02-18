package com.hardronix.fireflower;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {

    private JSONObject config;

    public Config()
    {
        if(Files.notExists(Paths.get("config.json"))) {
            createDefaultConfig();
        }
        loadConfig();
    }

    private void createDefaultConfig() {
        try {
            FileWriter configWriter = new FileWriter("config.json");
            JSONObject defaultConfig = new JSONObject();

            defaultConfig.put("text_shadow", false);
            defaultConfig.put("nasa_api_key", "DEMO_KEY");
            defaultConfig.put("enable_mpd", true);
            defaultConfig.put("mpd_address", "127.0.0.1:6600");
            defaultConfig.put("mpd_password", "");

            configWriter.write(defaultConfig.toString());
            configWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig()
    {
        try {
            config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }
}
