/**
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package org.nita.notifications.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.preference.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

public class RegistrationIntentService extends IntentService {

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    private static final String TAG = RegistrationIntentService.class.getName();
    //private static final String[] TOPICS = {"test"};
    private static final String[] TOPICS = {"release"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                String token = intent.getStringExtra("TOKEN");
                Log.i(TAG, "FCM Registration Token: " + token);

                // Implement this method to send any registration to your app's servers.
                sendRegistrationToServer();

                if (intent.getBooleanExtra("unsubscribe", false)) {
                    Log.d(TAG, "Un-subscribing...");
                    unSubscribeTopics();
                    return;
                }

                // Subscribe to topic channels
                Log.d(TAG, "Subscribing...");
                subscribeTopics();

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
                // [END register_for_gcm]
                Log.d(TAG, "Completed token refresh");
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            // sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     */
    private void sendRegistrationToServer() {
        // Add custom implementation, as needed.
    }

    /**
     * Subscribe to any FCM topics of interest, as defined by the TOPICS constant.
     */
    private void subscribeTopics() {
        for (String topic : TOPICS) {
            FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
            pubSub.subscribeToTopic(topic);
        }
        Log.i(TAG, "Subscribed to topics " + Arrays.toString(TOPICS));
    }

    private void unSubscribeTopics() {
        for (String topic : TOPICS) {
            FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
            pubSub.unsubscribeFromTopic(topic);
        }
        Log.i(TAG, "Unsubscribed from topics " + Arrays.toString(TOPICS));
    }
}
