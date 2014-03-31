package com.ram.mygov;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONParser {

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String s = "";
        String line = null;

        try {

            do {

                line = reader.readLine();
                if (line != null)
                    s+=line;

            } while (line != null);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;

    }

    public static String convertStreamToString(InputStream is, String name) {

        String s = convertStreamToString(is);

        return getVariableFromString(s,name);

    }

    public static String getVariableFromString(String s, String name) {

        JSONObject obj = (JSONObject) JSONValue.parse(s);

        if (obj == null)
            return "";

        if (obj.get(name) == null)
            return s;

        return obj.get(name).toString();

    }

    public static String[] convertStreamToArray(InputStream is) {

        String s = convertStreamToString(is);

        JSONArray mJsonArray = null;

        String[] data = null;

        try {

            mJsonArray = new JSONArray(s);

            if (mJsonArray == null)
                return null;

            data = new String[mJsonArray.length()];

            for (int i = 0; i < mJsonArray.length(); i++) {
                data[i] = mJsonArray.getJSONObject(i).toString();
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }

        return data;

    }

    public static String[] convertStringToArray(String s) {

        JSONArray mJsonArray = null;

        String[] data = null;

        try {

            mJsonArray = new JSONArray(s);

            if (mJsonArray == null)
                return null;

            data = new String[mJsonArray.length()];

            for (int i = 0; i < mJsonArray.length(); i++) {
                data[i] = mJsonArray.getJSONObject(i).toString();
            }

        } catch(JSONException e) {
            e.printStackTrace();
        }

        return data;

    }

}
