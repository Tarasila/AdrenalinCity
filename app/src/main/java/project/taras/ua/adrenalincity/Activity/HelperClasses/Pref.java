package project.taras.ua.adrenalincity.Activity.HelperClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import project.taras.ua.adrenalincity.Activity.Login.User;

/**
 * Created by Taras on 03.04.2017.
 */

public class Pref {

    private boolean userLoggedIn = false;

    private static Pref instance;
    private static final String PREF = "pref";
    private static final String CURRENT_USER = "currentuser";
    private static final String USER_NAME = "username";
    private static final String USER_FB_ID = "userfbid";
    private static final String USER_VK_ID = "uservkid";
    private static final String USER_EMAIL = "useremail";
    private static final String USER_ID_ADRENALIN = "useridadrenalin";
    private static final String USER_PHOTO = "userphoto";
    private static final String BASE_64_USER_PHOTO = "base64photo";
    private static final String USER_ORDER = "userOrder";
    private static final String IS_USER_LOGGED_IN = "userloggedin";
    private static final String HOW_USER_LOGGED_IN = "howuserloggedin";

    private static SharedPreferences preferences;

    private Pref() {
    }

    public static Pref getInstance(Context context) {

        if (instance == null) {
            instance = new Pref();
            preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public void saveCurrentUser(User user) {

        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put(USER_NAME, user.getName());
            jsonUser.put(USER_EMAIL, user.getEmail());
            jsonUser.put(USER_ID_ADRENALIN, user.getDbId());
            jsonUser.put(USER_FB_ID, user.getFbId());
            jsonUser.put(USER_VK_ID, user.getVkId());
            jsonUser.put(USER_PHOTO, user.getProfilePhoto());

            Log.v("save_user", user.getName());

            preferences
                    .edit()
                    .putString(CURRENT_USER, jsonUser.toString())
                    .apply();

            preferences
                    .edit()
                    .putBoolean(IS_USER_LOGGED_IN, true)
                    .apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteCurrentUser() {
        preferences
                .edit()
                .clear()
                .apply();

        preferences
                .edit()
                .putBoolean(IS_USER_LOGGED_IN, false)
                .apply();

        clearHowUserLoggedIn();
    }

    public boolean isUserLoggedIn() {
        return preferences
                .getBoolean(IS_USER_LOGGED_IN, false);
    }

    public void setHowUserLoggedIn(int constVal) {
        preferences
                .edit()
                .putInt(HOW_USER_LOGGED_IN, constVal)
                .apply();
    }

    public int howUserLoggedIn() {
        return preferences
                .getInt(HOW_USER_LOGGED_IN, 0);
    }

    private void clearHowUserLoggedIn() {
        preferences
                .edit()
                .putInt(HOW_USER_LOGGED_IN, 0)
                .apply();
    }

    @Nullable
    public JSONObject getCurrentUser() {
        JSONObject jsonUser = null;
        try {
            jsonUser = new JSONObject(preferences.getString(CURRENT_USER, null));
        } catch (JSONException e) {
            Log.e("jsonerror", e.toString());
        }
        return jsonUser;
    }

    //probably would be better to delete both of these methods
    //I expressly saved user's photo here to make it permanent throughout app's lifecycle
    public void saveUserPhoto(String b64UserPhoto) {
        preferences
                .edit()
                .putString(BASE_64_USER_PHOTO, b64UserPhoto)
                .apply();
    }

    public Bitmap getBitmapUserPhoto() {
        String encoded = preferences.getString(BASE_64_USER_PHOTO, null);
        byte[] byteArray = encoded.getBytes();
        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bm;
    }

    /*public void saveOrder(Map<String, Object> order) {
        Gson g = new Gson();

        preferences
                .edit()
                .putString(USER_ORDER, g.toJson(order))
                .apply();
    }

    public Map<String, Object>  getOrder(){
        Gson g = new Gson();
        String gsonOrder = preferences.getString(USER_ORDER, null);

        return g.fromJson(gsonOrder, new TypeToken<Map<String, Object>>(){}.getType());

    }*/
}
