package com.udacity.sandwichclub.utils;

import android.util.Log;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json) {
        Sandwich sandwich = new Sandwich();
        ArrayList<String> knownAs = new ArrayList<String>();
        ArrayList<String> ingred = new ArrayList<String>();

        try{
            JSONObject obj = new JSONObject(json);
            sandwich.setMainName(obj.getJSONObject("name").getString("mainName"));
            JSONArray knownAsArray = obj.getJSONObject("name").getJSONArray("alsoKnownAs");
            if(knownAsArray.length() != 0) {
                for(int i=0; i<knownAsArray.length(); i++) {
                    knownAs.add(knownAsArray.getString(i));
                }
            }
            else {
                knownAs.add("No Thing To Display!");
            }
            sandwich.setAlsoKnownAs(knownAs);
            sandwich.setPlaceOfOrigin(obj.getString("placeOfOrigin"));
            sandwich.setDescription(obj.getString("description"));
            JSONArray ingredArray = obj.getJSONArray("ingredients");
            if(ingredArray.length() != 0) {
                for(int i=0; i<ingredArray.length(); i++) {
                    ingred.add(ingredArray.getString(i));
                }
            }
            else {
                ingred.add("No Thing To Display!");
            }

            sandwich.setIngredients(ingred);
            sandwich.setImage(obj.getString("image"));
        }
        catch (JSONException e) {
            return null;
        }


        return sandwich;

    }
}
