package xyz.gnarbot.gnar.utils;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import java.io.IOException;

/**
 * Created by Gatt on 20/06/2017.
 */
public class MyAnimeListAPI {

    private String username, password;
    private boolean loggedIn = false;
    private String apiStart = "https://myanimelist.net/api/";

    public MyAnimeListAPI(String username, String password) {
        this.username = username;
        this.password = password;
        loggedIn = attemptLogIn();
    }

    public boolean attemptLogIn(){
        Request request = new Request.Builder().url(apiStart + "account/verify_credentials.xml")
                .header("User-Agent", "Gnar")
                .header("Content-Type", "text/plain")
                .addHeader("-u", username + ":" + password)
                .build();
        try (Response response = HttpUtils.CLIENT.newCall(request).execute()) {
            JSONObject jso = convertXML(response.body().string());

            response.close();

            return jso.has("user") && jso.getJSONObject("user").has("id");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JSONObject convertXML(String xml){
        return XML.toJSONObject(xml);
    }


}
