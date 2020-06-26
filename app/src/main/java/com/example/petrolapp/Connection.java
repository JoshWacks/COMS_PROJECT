package com.example.petrolapp;

import android.app.Activity;
import android.content.ContentValues;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Connection {
    private final OkHttpClient client = new OkHttpClient();
    private String url;
    private String file;

    public Connection(String prefix) {
        url = prefix;

    }

    public void fetchInfo(Activity a, String f, ContentValues params, RequestHandler requestHandler) {
        file = f + ".php";


        FormBody.Builder builder = new FormBody.Builder();

        for (String key : params.keySet()) {
            builder.add(key, params.getAsString(key));
        }
        Request request = new Request.Builder().url(url + file).post(builder.build()).build();

        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                e.printStackTrace(); }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                final String responseDate = response.body().string();
                                                a.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        requestHandler.processResponse(responseDate);
                                                    }
                                                });
                                            }
                                        }
        );
    }

}
