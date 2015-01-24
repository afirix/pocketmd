package org.coursera.android.capstone.client.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import org.coursera.android.capstone.client.Utilities;
import org.coursera.android.capstone.client.infrastructure.PreferencesKeys;
import org.coursera.android.capstone.client.model.Person;
import org.coursera.android.capstone.client.ui.UserActivity;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.Collections;
import java.util.Date;
import java.util.NavigableSet;
import java.util.Set;

public class AlarmScheduler {

    private static AlarmScheduler instance;

    public static AlarmScheduler getInstance() {
        if (instance == null) {
            instance = new AlarmScheduler();
        }
        return instance;
    }

    private AlarmManager alarmManager;

    private AlarmScheduler() {
    }

    public void cancelCheckinAlarm(final Context context) {
        final Intent checkinIntent = new Intent(context, PatientCheckInNotificationBroadcastReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, checkinIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_UPDATE_CURRENT);

        getAlarmManager(context).cancel(pendingIntent);
    }

    public void scheduleCheckinAlarms(final Context context, final Person currentUser) {
        final SharedPreferences userPreferences = context.getSharedPreferences(
                PreferencesKeys.APP_KEY + currentUser.getUsername(),
                Context.MODE_PRIVATE);
        final Set<String> checkinTimes = userPreferences.getStringSet(
                PreferencesKeys.SETTINGS_CHECKIN_TIMES,
                Collections.<String>emptySet());
        final NavigableSet<LocalTime> checkinTimesSorted = Sets.newTreeSet(
                Ordering.natural().sortedCopy(
                        Utilities.convertCheckinTimesFromStringToLocalTime(checkinTimes)));

        final LocalTime now = LocalTime.fromDateFields(new Date());

        LocalTime nextAlarmTime = checkinTimesSorted.higher(now);
        if (nextAlarmTime == null) {
            nextAlarmTime = checkinTimesSorted.iterator().next();
        }

        DateTime nextAlarmDateTime = nextAlarmTime.toDateTimeToday();
        if (nextAlarmTime.isBefore(now)) {
            nextAlarmDateTime = nextAlarmDateTime.plusDays(1);
        }

        final Intent checkinIntent = new Intent(
                context,
                PatientCheckInNotificationBroadcastReceiver.class);
        checkinIntent.putExtra(
                PatientCheckInNotificationService.CHECKIN_TIME_INTENT_TOKEN,
                Utilities.CHECKIN_TIME_FORMATTER.print(nextAlarmTime));
        checkinIntent.putExtra(UserActivity.USER_INTENT_TOKEN, currentUser);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, checkinIntent, PendingIntent.FLAG_ONE_SHOT);

        cancelCheckinAlarm(context);

        getAlarmManager(context).setExact(
                AlarmManager.RTC_WAKEUP,
                nextAlarmDateTime.getMillis(),
                pendingIntent);
    }

    private AlarmManager getAlarmManager(final Context context) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return alarmManager;
    }
}
