package com.benjaminshamoilia.trackerapp.doorbell;

import android.graphics.Bitmap;
import android.util.Base64;
import com.benjaminshamoilia.trackerapp.MainActivity;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class DoorbellApi extends RestApi {

    private static final String DOORBELL_IO_URL = "https://doorbell.io/api/";
    private static final String DOORBELL_IO_HOST = "doorbell.io";
    private static final String DOORBELL_USER_AGENT = "Doorbell Android SDK";

    private String mApiKey;
    private long mAppId;
    private String language;

    public DoorbellApi(MainActivity activity) {
        super(activity);

        this.BASE_URL = DOORBELL_IO_URL;
        this.rest.setHost(DOORBELL_IO_HOST);
        this.setUserAgent(DOORBELL_USER_AGENT);

        this.language = activity.getResources().getConfiguration().locale.getLanguage();

        this.reset();
    }

    public void setAppId(long id) {
        this.mAppId = id;
    }

    public void setApiKey(String key) {
        this.mApiKey = key;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void reset() {
        super.reset();

        this.addParameter("sdk", "android");
        this.addParameter("version", "0.2.6");

        this.cachePolicy = RestCache.CachePolicy.NETWORK_ONLY;
    }

    public void impression() {
        this.setLoadingMessage(null);
        this.post("applications/" + this.mAppId + "/impression?key=" + this.mApiKey);
    }

    public void open() {
        this.setLoadingMessage(null);
        this.post("applications/" + this.mAppId + "/open?key=" + this.mApiKey);
    }

    public void sendFeedbackWithScreenshot(String message, String email, JSONObject properties, String name, Bitmap screenshot) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        screenshot.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String encoded = Base64.encodeToString(byteArrayOutputStream .toByteArray(), Base64.DEFAULT);

        this.addParameter("android_screenshot", encoded);

        this.sendFeedback(message, email, properties, name);
    }

    public void sendFeedback(String message, String email, JSONObject properties, String name) {
        this.addParameter("message", message);
        this.addParameter("email", email);

        this.addParameter("properties", properties.toString());

        this.addParameter("name", name);

        this.addParameter("language", this.language);

        this.post("applications/" + this.mAppId + "/submit?key=" + this.mApiKey);
    }


    public void sendFeedback(String message, String email) {
        this.addParameter("message", message);
        this.addParameter("email", email);


        this.addParameter("language", this.language);

        this.post("applications/" + this.mAppId + "/submit?key=" + this.mApiKey);
    }


}