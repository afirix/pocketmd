package org.coursera.android.capstone.server.impl.alert;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.coursera.android.capstone.common.model.CheckIn;
import org.coursera.android.capstone.common.model.EatingProblems;
import org.coursera.android.capstone.common.model.PainSeverity;
import org.coursera.android.capstone.common.model.Patient;
import org.coursera.android.capstone.server.impl.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Component
public class DoctorAlertChecker {
	
	private static final Logger LOG = LoggerFactory.getLogger(DoctorAlertChecker.class);

	private static class AlertTest {

		private Predicate<CheckIn> predicate;
		private int hoursThreshold;
		private String alertTemplate;
		private DoctorAlert.Type alertType;

		public Predicate<CheckIn> getPredicate() {
			return predicate;
		}

		public int getHoursThreshold() {
			return hoursThreshold;
		}

		public String getAlertTemplate() {
			return alertTemplate;
		}

		public DoctorAlert.Type getAlertType() {
			return alertType;
		}

		private static class Builder {
			private Predicate<CheckIn> predicate;
			private int hoursThreshold;
			private String alertTemplate;
			private DoctorAlert.Type alertType;

			public Builder withPredicate(final Predicate<CheckIn> predicate) {
				this.predicate = predicate;
				return this;
			}

			public Builder withHoursThreshold(final int hoursThreshold) {
				this.hoursThreshold = hoursThreshold;
				return this;
			}

			public Builder withAlertTemplate(final String alertTemplate) {
				this.alertTemplate = alertTemplate;
				return this;
			}

			public Builder withAlertType(final DoctorAlert.Type alertType) {
				this.alertType = alertType;
				return this;
			}

			public AlertTest build() {
				final AlertTest test = new AlertTest();
				test.predicate = predicate;
				test.hoursThreshold = hoursThreshold;
				test.alertTemplate = alertTemplate;
				test.alertType = alertType;
				return test;
			}
		}
	}

	private static final String PROPERTY_NAME_PROLONGED_SEVERE_PAIN_HOURS = "alert.prolonged.severe.pain.hours";
	private static final String PROPERTY_NAME_PROLONGED_PAIN_HOURS = "alert.prolonged.pain.hours";
	private static final String PROPERTY_NAME_PROLONGED_EATING_PROBLEMS_HOURS = "alert.prolonged.eating.problems.hours";

	private static final String PROPERTY_NAME_PROLONGED_SEVERE_PAIN_MESSAGE = "alert.prolonged.severe.pain.message";
	private static final String PROPERTY_NAME_PROLONGED_PAIN_MESSAGE = "alert.prolonged.pain.message";
	private static final String PROPERTY_NAME_PROLONGED_EATING_PROBLEMS_MESSAGE = "alert.prolonged.eating.problems.message";

	@Resource
	private Environment env;
	
	private List<AlertTest> alertTests = Lists.newLinkedList();
	
	@PostConstruct
	void initialize() {
		alertTests.add(new AlertTest.Builder()
				.withPredicate(
						checkIn -> checkIn.getPainSeverity() == PainSeverity.SEVERE)
				.withHoursThreshold(
						Integer.parseInt(env
								.getRequiredProperty(PROPERTY_NAME_PROLONGED_SEVERE_PAIN_HOURS)))
				.withAlertTemplate(
						env.getRequiredProperty(PROPERTY_NAME_PROLONGED_SEVERE_PAIN_MESSAGE))
				.withAlertType(DoctorAlert.Type.PROLONGED_SEVERE_PAIN).build());

		alertTests.add(new AlertTest.Builder()
				.withPredicate(
						checkIn -> checkIn.getPainSeverity() == PainSeverity.SEVERE
								|| checkIn.getPainSeverity() == PainSeverity.MODERATE)
				.withHoursThreshold(
						Integer.parseInt(env
								.getRequiredProperty(PROPERTY_NAME_PROLONGED_PAIN_HOURS)))
				.withAlertTemplate(
						env.getRequiredProperty(PROPERTY_NAME_PROLONGED_PAIN_MESSAGE))
				.withAlertType(DoctorAlert.Type.PROLONGED_PAIN).build());

		alertTests.add(new AlertTest.Builder()
				.withPredicate(
						checkIn -> checkIn.getEatingProblems() == EatingProblems.CANT_EAT)
				.withHoursThreshold(
						Integer.parseInt(env
								.getRequiredProperty(PROPERTY_NAME_PROLONGED_EATING_PROBLEMS_HOURS)))
				.withAlertTemplate(
						env.getRequiredProperty(PROPERTY_NAME_PROLONGED_EATING_PROBLEMS_MESSAGE))
				.withAlertType(DoctorAlert.Type.PROLONGED_EATING_PROBLEMS)
				.build());
	}

	public int getMaxHoursThreshold() {
		return alertTests.stream().mapToInt(test -> test.hoursThreshold).max().getAsInt();
	}

	public Set<DoctorAlert> check(final List<CheckIn> checkIns, final Date now,
			final Patient patient) {
		Set<DoctorAlert> alerts = Sets.newHashSet();
		for (final AlertTest alertTest : alertTests) {
			LOG.info("Testing for {} for last {} hours", alertTest.alertType,
					alertTest.hoursThreshold);
			final List<CheckIn> subset = getCheckInHistorySubsetToAnalyze(checkIns, now, alertTest.getHoursThreshold());
			LOG.info("Testing {} last check-ins", subset.size());
			if (!subset.isEmpty() && subset.stream().allMatch(alertTest.getPredicate())) {
				final DoctorAlert alert = new DoctorAlert.Builder()
						.withType(alertTest.getAlertType())
						.withTime(now)
						.withMessage(
								String.format(alertTest.getAlertTemplate(),
										patient.getFullName(), alertTest.getHoursThreshold()))
						.build();
				LOG.info("Alert for {} is detected", alertTest.alertType);
				alerts.add(alert);
			}
		}
		return alerts;
	}

	private List<CheckIn> getCheckInHistorySubsetToAnalyze(
			final List<CheckIn> checkInHistory,
			final Date alertTestTime,
			final int hoursThreshold) {
		final Date alertTestTimeWindowStart = TimeUtils.minusHours(
				alertTestTime, hoursThreshold);
		final Optional<CheckIn> firstCheckInWithinTimeWindow = checkInHistory
				.stream()
				.filter(checkIn -> !checkIn.getDateTime().after(alertTestTimeWindowStart))
				.max((checkIn1, checkIn2) -> checkIn1.getDateTime().compareTo(checkIn2.getDateTime()));
		if (!firstCheckInWithinTimeWindow.isPresent()) {
			return ImmutableList.of();
		} else {
			return checkInHistory
					.stream()
					.filter(checkIn -> !checkIn.getDateTime().before(
							firstCheckInWithinTimeWindow.get().getDateTime()))
					.collect(Collectors.toList());
		}
	}
}
