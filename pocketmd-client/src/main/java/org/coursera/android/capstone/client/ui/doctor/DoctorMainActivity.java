package org.coursera.android.capstone.client.ui.doctor;

import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.ui.UserActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class DoctorMainActivity extends UserActivity {

	@InjectView(R.id.doctor_main_menu)
	ListView mainMenu;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_main);
		ButterKnife.inject(this);
	}

	@OnItemClick(R.id.doctor_main_menu)
	public void onItemClick(final int position) {
		final Class<? extends Activity> activityToStart;
		switch (position) {
			case 0:
				activityToStart = DoctorSearchActivity.class;
				break;
			case 1:
				activityToStart = DoctorListActivity.class;
				break;
			default:
				throw new IllegalStateException("No item in the list found");
		}
        final Intent intent = new Intent(this, activityToStart);
        intent.putExtra(USER_INTENT_TOKEN, currentUser);
        startActivity(intent);
	}

	@Override
	public void onBackPressed() {
        promptExit(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
	}
}
