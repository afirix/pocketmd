package org.coursera.android.capstone.client.model;

import android.os.Parcelable;

public interface Person extends Parcelable {

	long getId();

	String getUsername();

	String getFirstName();

	String getLastName();

    String getFullName();
}
