/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imaguru.community;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String serverKey = "key=AIzaSyDZrLsKJ6TOxVyM4QCjL-pSTov7GbWLvqk";

                HashMap<String, Object> params = new HashMap<>();
                String channel="dLzFWPWGf90:APA91bGk3RO-qz3w9h3hwWLGl9f49nAKSQVfoz2QmFxU2ieKX49Skvk_FfzaJC7L0KyLJxTFKdAR_0oxsX0O-pZbOWDO73qGNcEQCefUoChKPj_XlX5mUWIck3uWKEoZJxLNvt-9w690";
                //vika
                //String channel="fQqxItG5jrM:APA91bFaA2YSHhcx3x-2aWtaj4y35Lpq2u6a3G5HcB-7zqOZWY9azqaA-zx8A1m_7nT_KpaAoMczWHgPUTBpzBBisBrw5j9D2Y5CMplZ6LogqKDfWSxEzOh_8yvOcX4WLL8EnrgYCN-y";
                final Map<String, String> data = new HashMap<>();
                data.put("body" , "606060405260126006556000600790600019169055341561001c57fe5b604051602080611c5d833981016040528080519060200190919050505b5b60005b80600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550806000819055505b5033600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055503373ffffffffffffffffffffffffffffffffffffffff167fce241d7ca1f669fee44b6fc00b8eba2df3bb514eed0f6f668f8f89096e81ed9460405180905060405180910390a25b80600581600019169055505b505b611b2e8061012f6000396000f3006060604052361561011b576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306fdde031461011d57806307da68f51461014b578063095ea7b31461015d57806313af4035146101b457806318160ddd146101ea57806323b872dd14610210578063313ce567146102865780633452f51d146102ac5780635ac801fe1461031557806369d3e20e1461033957806370a082311461036b57806375f12b");
                data.put("title" , "ChatSDK424");
                params.put("to", channel);
                params.put("notification", data);

                bodyString = new JSONObject(params).toString();

                new PostToFCM().execute();

                // Log and toast
                String msg = getString(R.string.msg_subscribed);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        Button subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("news");
                // [END subscribe_topics]

                // Log and toast
                String msg = getString(R.string.msg_subscribed);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        Button logTokenButton = findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                String token = FirebaseInstanceId.getInstance().getToken();

                // Log and toast
                String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static int responseCode = 0;
    public static String responseString = "";
    public static String bodyString = "";

    class PostToFCM extends AsyncTask<String, Void, Void> {

        private OkHttpClient client = new OkHttpClient();

        protected Void doInBackground(String... urls) {
            try {
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, bodyString);
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(body)
                        .addHeader("Authorization", "key=AIzaSyB6ZHxvczLRpUloRSc0h5he_LPyZxL5zvk")
                        .build();

                Response response = client.newCall(request).execute();

                if ((responseCode = response.code()) == 200) {
                    // Get response
                    String jsonData = response.body().string();

                    // Transform reponse to JSon Object
                    JSONObject json = new JSONObject(jsonData);

                    // Use the JSon Object
                    String _token = json.getString("token");
                }

            } catch (IOException e) {
                responseString = e.toString();
            } catch (JSONException e) {
                responseString = e.toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }


}
