package org.coursera.android.capstone.client.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import org.coursera.android.capstone.client.PocketMdClientApplication;
import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.model.Patient;
import org.coursera.android.capstone.client.service.PocketMdServiceApi;
import org.coursera.android.capstone.client.ui.doctor.DoctorPatientDetailsActivity;

import java.util.Map;

public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;
    public static final String GCM_INTENT_SERVICE_NAME = "GcmIntentService";

    private final PocketMdServiceApi service;
    private final Gson gson;
    private GoogleCloudMessaging gcm;
    private NotificationManager notificationManager;

    public GcmIntentService() {
        super(GCM_INTENT_SERVICE_NAME);

        service = ((PocketMdClientApplication) getApplication()).getPocketMdService();
        gson = new Gson();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(this);
        }
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        final Bundle extras = intent.getExtras();

        if (!extras.isEmpty() && GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(gcm.getMessageType(intent))) {

            final String message = extras.getString("default");
            final Map<String, String> messageMap = gson.fromJson(message, Map.class);
            final String payload = messageMap.get("data");

            final AlertMessage alertMessage = gson.fromJson(payload, AlertMessage.class);

            // Post notification of received message.
            sendNotification(alertMessage);
            Log.i(TAG, "Received: " + extras.toString());
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(final AlertMessage alertMessage) {
        final long patientId = alertMessage.getPatientId();
        final Patient patient = service.getPatient(patientId);
        if (patient == null) {
            return;
        }

        final Intent patientDetailsIntent = new Intent(this, DoctorPatientDetailsActivity.class);
        patientDetailsIntent.putExtra(DoctorPatientDetailsActivity.PATIENT_INTENT_TOKEN, patient);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                patientDetailsIntent, 0);

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.alert_notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(alertMessage.getMessage()))
                        .setContentText(alertMessage.getMessage());

        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private static class AlertMessage {

        private String message;
        private long patientId;

        public String getMessage() {
            return message;
        }

        public long getPatientId() {
            return patientId;
        }
    }
}