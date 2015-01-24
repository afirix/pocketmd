package org.coursera.android.capstone.server.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeUtils {

	public static Date minusHours(final Date date, final int hours) {
		return Date.from(
				ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
					.minusHours(hours).toInstant());
	}
}
