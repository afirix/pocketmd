package org.coursera.android.capstone.client;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.commons.lang3.StringUtils;
import org.coursera.android.capstone.client.infrastructure.PropertiesManager;
import org.coursera.android.capstone.client.model.Doctor;
import org.coursera.android.capstone.client.model.Patient;
import org.coursera.android.capstone.client.model.Person;
import org.coursera.android.capstone.client.service.PocketMdService;
import org.coursera.android.capstone.client.service.PocketMdServiceApi;

import java.io.IOException;

public class LoginTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = LoginTask.class.getSimpleName();

    protected final Context context;
    protected final String server;
    protected final String username;
    protected final String password;
    protected final String projectNumber;
    protected String gcmRegistrationId;

    protected Person currentUser;

    protected PocketMdServiceApi service;

    public LoginTask(
            final Context context,
            final String server,
            final String username,
            final String password,
            final String projectNumber,
            final String gcmRegistrationId) {
        this.context = context;
        this.server = server;
        this.username = username;
        this.password = password;
        this.projectNumber = projectNumber;
        this.gcmRegistrationId = gcmRegistrationId;
    }

    @Override
    protected Boolean doInBackground(final Void... params) {

        service = PocketMdService.init(server, username, password);

        try {
            currentUser = getCurrentUser();
            if (currentUser == null) {
                return false;
            }

            if (StringUtils.isBlank(gcmRegistrationId)) {
                try {
                    gcmRegistrationId = registerInGcm();
                    service.sendGcmRegistrationId(currentUser.getId(), gcmRegistrationId);
                } catch (final Exception e) {
                    Log.e(TAG, "Cannot register in Google Cloud Messaging");
                }
            }

            return true;
        } catch (final Exception e) {
            Log.e(TAG, "Cannot establish connection to the server", e);
        }

        return false;
    }

    protected Person getCurrentUser() {
        final Patient patient = service.getCurrentPatient();
        if (patient != null) {
            return patient;
        } else {
            final Doctor doctor = service.getCurrentDoctor();
            if (doctor != null) {
                return doctor;
            } else {
                Log.e(TAG, "Unknown user");
                return null;
            }
        }
    }

    protected String registerInGcm() throws IOException {
        return GoogleCloudMessaging.getInstance(context).register(projectNumber);
    }
}
