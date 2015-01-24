package org.coursera.android.capstone.client.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;

import org.coursera.android.capstone.client.R;

public abstract class BaseActivity extends Activity {

    protected void showProgress(final boolean show) {

        final View formView = findViewById(R.id.form_view);
        final View progressView = findViewById(R.id.progress_view);

        final int shortAnimTime = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        formView.setVisibility(show ? View.GONE : View.VISIBLE);
        formView.animate().setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        formView.setVisibility(show ? View.GONE
                                : View.VISIBLE);
                    }
                });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        progressView.setVisibility(show ? View.VISIBLE
                                : View.GONE);
                    }
                });
    }
}
