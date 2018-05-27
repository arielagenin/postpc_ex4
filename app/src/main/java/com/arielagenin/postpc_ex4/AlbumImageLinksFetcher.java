package com.arielagenin.postpc_ex4;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AlbumImageLinksFetcher implements Callback {
    private static final String IMGUR_CLIENT_ID = "adf32ecbe5454eb";
    private static final String ALBUM_ID = "hiH6I";

    private Handler handler;
    private OnLinksFetchedListener listener;

    AlbumImageLinksFetcher(OnLinksFetchedListener listener) {
        handler = new Handler(Looper.getMainLooper());
        this.listener = listener;
    }

    /**
     * According to the GET Album Images request description at
     * https://apidocs.imgur.com Album -> Album Images.
     */
    public void fetchLinks() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url("https://api.imgur.com/3/album/" + ALBUM_ID + "/images")
                .build();
        client.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        e.printStackTrace();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException(response.toString());
        }

        final String responseData = response.body().string();

        // https://api.imgur.com/models/image
        try {
            final JSONArray jsonArrayData = new JSONObject(responseData).getJSONArray("data");
            final int imageCount = jsonArrayData.length();
            final String[] links = new String[imageCount];

            for (int i = 0; i < imageCount; ++i) {
                final JSONObject imageData = jsonArrayData.getJSONObject(i);
                final String gifvLink = imageData.optString("gifv", null);
                // If this is not a gif then we get the regular link
                links[i] = gifvLink != null ? toGifLink(gifvLink) : imageData.getString("link");
            }

            // Invoke listener method on the main thread
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onLinksFetched(links);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Imgur's gifv links aren't proper gif links.
     * Apparently, changing ".gifv" to ".gif" at the end of the link will give us the desired link.
     *
     * @param gifvLink Link to Imgur's gifv.
     * @return Proper gif link.
     */
    private static String toGifLink(String gifvLink) {
        return gifvLink.substring(0, gifvLink.length() - 1);
    }

    public interface OnLinksFetchedListener {
        void onLinksFetched(String[] links);
    }
}
