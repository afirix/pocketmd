package org.coursera.android.capstone.client;


import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Utilities {

    public static final DateTimeFormatter CHECKIN_TIME_FORMATTER = ISODateTimeFormat.hourMinute();

    public static List<LocalTime> convertCheckinTimesFromStringToLocalTime(final Collection<String> checkinTimes) {
        return Lists.newArrayList(
                FluentIterable.from(checkinTimes)
                        .transform(new Function<String, LocalTime>() {
                            @Override
                            public LocalTime apply(final String input) {
                                return CHECKIN_TIME_FORMATTER.parseLocalTime(input);
                            }
                        })
                        .toSortedList(Ordering.natural()));
    }

    public static Set<String> convertCheckinTimesFromLocalTimeToString(final Collection<LocalTime> checkinTimes) {
        return FluentIterable.from(checkinTimes)
                .transform(new Function<LocalTime, String>() {
                    @Override
                    public String apply(final LocalTime input) {
                        return CHECKIN_TIME_FORMATTER.print(input);
                    }
                })
                .toSortedSet(Ordering.natural());
    }
}
