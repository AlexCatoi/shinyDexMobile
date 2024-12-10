package com.example.proiect;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JsonFileHelper {
    public static List<String> readJsonFile(Context context, String fileName, String key) {
        List<String> result = new ArrayList<>();
        try {
            // Open and read the file
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            Log.d("Pokes","not yet");
            // Parse the JSON
            JSONArray jsonArray = new JSONArray(jsonString.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has(key)) {
                    result.add(jsonObject.getString(key));
                }
            }

            br.close();
            isr.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    public static boolean deleteJsonFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists()) {
            return file.delete(); // Returns true if the file was successfully deleted
        }
        return false; // File does not exist
    }
}
