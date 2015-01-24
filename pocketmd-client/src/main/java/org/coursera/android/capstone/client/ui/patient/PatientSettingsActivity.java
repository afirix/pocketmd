package org.coursera.android.capstone.client.ui.patient;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.common.base.Function;

import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.Utilities;
import org.coursera.android.capstone.client.alarm.AlarmScheduler;
import org.coursera.android.capstone.client.infrastructure.PreferencesKeys;
import org.coursera.android.capstone.client.infrastructure.PropertiesManager;
import org.coursera.android.capstone.client.ui.UserActivity;
import org.joda.time.LocalTime;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTouch;

public class PatientSettingsActivity extends UserActivity {

    @InjectView(R.id.checkin_times_list)
    ListView checkinTimesList;

    private GestureDetectorCompat gestureDetector;

    private CheckinTimesAdapter checkinTimesAdapter;

    private int minCheckins;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_settings);
        ButterKnife.inject(this);

        final Set<String> checkinTimes = userPreferences.getStringSet(PreferencesKeys.SETTINGS_CHECKIN_TIMES, Collections.<String>emptySet());
        final List<LocalTime> checkinTimesSorted = Utilities.convertCheckinTimesFromStringToLocalTime(checkinTimes);

        if (checkinTimesSorted.isEmpty()) {
            checkinTimesSorted.add(new LocalTime(12, 00));
            checkinTimesSorted.add(new LocalTime(13, 00));
            checkinTimesSorted.add(new LocalTime(14, 00));
            checkinTimesSorted.add(new LocalTime(15, 00));
        }

        checkinTimesAdapter = new CheckinTimesAdapter(this, checkinTimesSorted);
        checkinTimesList.setAdapter(checkinTimesAdapter);

        minCheckins = Integer.parseInt(PropertiesManager.getProperty(this, PropertiesManager.MIN_CHECKINS));

        gestureDetector = new GestureDetectorCompat(this, new CheckinListTouchListener());
    }

    @OnItemClick(R.id.checkin_times_list)
    public void onCheckinTimeClick(final int position) {
        editCheckinTime(checkinTimesAdapter.getItem(position), position);
    }

    @OnTouch(R.id.checkin_times_list)
    public boolean onCheckinListTouch(final View v, final MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @OnClick(R.id.save_settings_button)
    public void saveSettings() {
        final List<LocalTime> checkinTimes = checkinTimesAdapter.getValues();
        if (checkinTimes.size() < minCheckins) {
            showInformationDialog(String.format(getString(R.string.error_too_few_checkins), minCheckins));
        } else {
            storeCheckinTimes(checkinTimes);
            AlarmScheduler.getInstance().scheduleCheckinAlarms(getApplicationContext(), currentUser);
            finish();
        }
    }

    private void storeCheckinTimes(final List<LocalTime> checkinTimes) {
        final Set<String> checkinTimesStrings = Utilities.convertCheckinTimesFromLocalTimeToString(checkinTimes);
        userPreferences.edit()
                .putStringSet(PreferencesKeys.SETTINGS_CHECKIN_TIMES, checkinTimesStrings)
                .apply();
    }

    @OnClick(R.id.add_new_checkin_time_button)
    public void addCheckinTime() {
        showTimePickerDialog(LocalTime.now(), new Function<LocalTime, Void>() {
            @Override
            public Void apply(final LocalTime input) {
                checkinTimesAdapter.add(input);
                return null;
            }
        });
    }

    private void editCheckinTime(final LocalTime currentCheckinTime, final int position) {
        showTimePickerDialog(currentCheckinTime, new Function<LocalTime, Void>() {
            @Override
            public Void apply(final LocalTime input) {
                checkinTimesAdapter.getValues().set(position, input);
                return null;
            }
        });
    }

    private void showTimePickerDialog(final LocalTime checkinTime, final Function<LocalTime, Void> callback) {
        new TimePickerDialog(
                this,
                new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(
                            final TimePicker view,
                            final int hourOfDay,
                            final int minute) {
                        if (!view.isShown()) {
                            return;
                        }

                        callback.apply(new LocalTime(hourOfDay, minute));
                        Collections.sort(checkinTimesAdapter.getValues());
                        checkinTimesAdapter.notifyDataSetChanged();
                    }
                },
                checkinTime.getHourOfDay(),
                checkinTime.getMinuteOfHour(), true)
        .show();
    }

    class CheckinTimesAdapter extends ArrayAdapter<LocalTime> {

        private List<LocalTime> values;

        public CheckinTimesAdapter(final Context context, final List<LocalTime> values) {
            super(context, R.layout.list_patient_settings_checkin_time_row, values);
            this.values = values;
        }

        public List<LocalTime> getValues() {
            return values;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final LocalTime checkinTime = getItem(position);

            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_patient_settings_checkin_time_row, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.checkinTimeLabel = (TextView) view.findViewById(R.id.checkin_time_row_label);
                viewHolder.checkinTimeLabel.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                    }
                });
                view.setTag(viewHolder);
            }

            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.checkinTimeLabel.setText(Utilities.CHECKIN_TIME_FORMATTER.print(checkinTime));

            return view;
        }

        class ViewHolder {
            protected TextView checkinTimeLabel;
        }
    }

    class CheckinListTouchListener extends GestureDetector.SimpleOnGestureListener {

        private static final int FLING_MIN_DISTANCE = 50;
        private static final int FLING_MAX_DEVIATION = 100;
        private static final int FLING_MIN_VELOCITY = 20;

        private MotionEvent lastOnDownEvent = null;

        @Override
        public boolean onDown(final MotionEvent event) {
            lastOnDownEvent = event;
            return super.onDown(event);
        }

        @Override
        public boolean onFling(
                final MotionEvent event1,
                final MotionEvent event2,
                final float velocityX,
                final float velocityY) {
            final MotionEvent flingStartEvent = (event1 == null) ? lastOnDownEvent : event1;

            if (flingStartEvent == null || event2 == null) {
                return false;
            }

            final float deltaX = Math.abs(event2.getX() - flingStartEvent.getX());
            final float deltaY = Math.abs(event2.getY() - flingStartEvent.getY());

            if (deltaX >= FLING_MIN_DISTANCE &&
                deltaY < FLING_MAX_DEVIATION &&
                Math.abs(velocityX) >= FLING_MIN_VELOCITY) {

                final int position = checkinTimesList.pointToPosition(
                        (int) flingStartEvent.getX(),
                        (int) flingStartEvent.getY());

                final LocalTime checkinTimeToRemove = checkinTimesAdapter.getItem(position);
                checkinTimesAdapter.remove(checkinTimeToRemove);
                checkinTimesAdapter.notifyDataSetChanged();

                final String toastText = String.format(getString(R.string.checkin_time_deleted), Utilities.CHECKIN_TIME_FORMATTER.print(checkinTimeToRemove));
                Toast.makeText(PatientSettingsActivity.this, toastText, Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        }
    }
}
