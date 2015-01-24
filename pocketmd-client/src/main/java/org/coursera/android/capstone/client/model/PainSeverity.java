package org.coursera.android.capstone.client.model;

import android.content.Context;
import android.content.res.Resources;

public enum PainSeverity {
	WELL_CONTROLLED,
	MODERATE,
	SEVERE;

    public String getLabel(final Context context) {
        final Resources res = context.getResources();
        final int resId = res.getIdentifier(this.name(), "string", context.getPackageName());
        return (resId != 0) ? res.getString(resId) : name();
    }
}
