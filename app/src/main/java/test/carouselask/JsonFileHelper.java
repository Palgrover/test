package test.carouselask;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by samvedana on 7/4/15.
 */
public class JsonFileHelper {
    //reads a given json file from assets as a Json Object
    public static JsonObject loadFromFileAsJsonObject(Test app, String fileName) {
        InputStream fis = null;
        try {
            fis = app.getAssets().open(fileName);
            Reader reader = new InputStreamReader(fis);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            return gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonArray loadFromFileAsJsonArray(Test app, String fileName) {
        InputStream fis = null;
        try {
            fis = app.getAssets().open(fileName);
            Reader reader = new InputStreamReader(fis);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            return gson.fromJson(reader, JsonArray.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
