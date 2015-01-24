package org.coursera.android.capstone.client.alarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.model.Person;
import org.coursera.android.capstone.client.ui.UserActivity;
import org.coursera.android.capstone.client.ui.patient.PatientCheckInActivity;

public class PatientCheckInNotificationService extends IntentService {

    public static final String PATIENT_CHECK_IN_NOTIFICATION_SERVICE = "PatientCheckInNotificationService";

    public static final String CHECKIN_TIME_INTENT_TOKEN = "CHECKIN_TIME";

    private NotificationManager notificationManager;

    public PatientCheckInNotificationService() {
        super(PATIENT_CHECK_IN_NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        final Person currentUser = intent.getParcelableExtra(UserActivity.USER_INTENT_TOKEN);
        final String checkinTime = intent.getStringExtra(CHECKIN_TIME_INTENT_TOKEN);
        final String notificationText = String.format(getString(R.string.checkin_notification_text), checkinTime);

        final Intent patientCheckinIntent = new Intent(this, PatientCheckInActivity.class);
        patientCheckinIntent.putExtra(UserActivity.USER_INTENT_TOKEN, currentUser);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                patientCheckinIntent, PendingIntent.FLAG_ONE_SHOT);

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.checkin_notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notificationText))
                        .setContentText(notificationText)
                        .setAutoCancel(true);

        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());

        AlarmScheduler.getInstance().scheduleCheckinAlarms(getApplicationContext(), currentUser);

        PatientCheckInNotificationBroadcastReceiver.completeWakefulIntent(intent);
    }
}
