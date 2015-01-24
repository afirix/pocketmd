package org.coursera.android.capstone.server.impl.alert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.coursera.android.capstone.common.model.Patient;
import org.coursera.android.capstone.server.impl.data.GcmRegistrationRepository;
import org.coursera.android.capstone.server.model.GcmRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Component
public class DoctorAlertSender {
	
	private static final Logger LOG = LoggerFactory.getLogger(DoctorAlertSender.class);

	private static final String PROPERTY_APP_NAME = "app.name";
	private static final String PROPERTY_GOOGLE_SERVER_API_KEY = "google.server.api.key";
	private static final String PROPERTY_GOOGLE_MESSAGE_TIME_TO_LIVE_SECONDS = "google.message.time.to.live.seconds";
	
	private static final String MESSAGE_PARAMETER_DATA = "data";
	private static final String MESSAGE_PARAMETER_TIME_TO_LIVE = "time_to_live";
	
	private static final String GCM_PLATFORM_ID = "GCM";
	private static final String PLATFORM_CREDENTIAL = "PlatformCredential";
	
	@Resource
	private Environment env;

	@Resource
	private AmazonSNS sns;
	
	@Resource
	private GcmRegistrationRepository gcmRegistrationRepository;
	
	private String platformApplicationArn;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@PostConstruct
	void init() {
		LOG.info("Creating SNS platform application");
		final CreatePlatformApplicationResult platformApplicationResult = createPlatformApplication(
				env.getRequiredProperty(PROPERTY_APP_NAME),
				env.getRequiredProperty(PROPERTY_GOOGLE_SERVER_API_KEY));
		LOG.info("Platform application created: {}", platformApplicationResult.toString());
		platformApplicationArn = platformApplicationResult.getPlatformApplicationArn();
	}

	public void send(final Patient patient, final Collection<DoctorAlert> alerts) {
		final List<String> alertMessages = alerts
			.stream()
			.map(DoctorAlert::getMessage)
			.collect(Collectors.toList());
		final String compoundMessage = Joiner.on("\n").join(alertMessages);
		
		final String payload = toJson(new AlertMessage(compoundMessage, patient.getId()));
		
		final List<String> registrationIds = gcmRegistrationRepository.findByUserId(patient.getDoctorId())
			.stream()
			.map(GcmRegistration::getRegistrationIdAsString)
			.collect(Collectors.toList());
		
		registrationIds.forEach(registrationId -> send(registrationId, payload));
	}

	private void send(
			final String registrationId,
			final String payload) {
		LOG.info("Creating SNS platform endpoint");
		final CreatePlatformEndpointResult platformEndpointResult = createPlatformEndpoint(
				registrationId,
				platformApplicationArn);
		LOG.info("Platform endpoint created: {}", platformEndpointResult.toString());

		LOG.info("Publishing message, payload={}", payload);
		final PublishResult publishResult = publish(
				payload,
				platformEndpointResult.getEndpointArn());
		LOG.info("Message published: {}", publishResult.toString());
	}

	private CreatePlatformApplicationResult createPlatformApplication(
			final String applicationName,
			final String credential) {
		final CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest()
				.withName(applicationName)
				.withPlatform(GCM_PLATFORM_ID)
				.withAttributes(ImmutableMap.of(PLATFORM_CREDENTIAL, credential));
		return sns.createPlatformApplication(platformApplicationRequest);
	}

	private CreatePlatformEndpointResult createPlatformEndpoint(
			final String registrationId,
			final String applicationArn) {
		final CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest()
				.withPlatformApplicationArn(applicationArn)
				.withToken(registrationId);
		return sns.createPlatformEndpoint(platformEndpointRequest);
	}
	
	private PublishResult publish(
			final String payload,
			final String endpointArn) {
		final String message = getMessage(payload);
		
		final PublishRequest publishRequest = new PublishRequest()
				.withTargetArn(endpointArn)
				.withMessage(message);
		return sns.publish(publishRequest);
	}
	
	private String getMessage(final String payload) {
		final Map<String, Object> messageMap = Maps.newHashMap();
		messageMap.put(MESSAGE_PARAMETER_DATA, payload);
		messageMap
				.put(MESSAGE_PARAMETER_TIME_TO_LIVE,
						env.getRequiredProperty(PROPERTY_GOOGLE_MESSAGE_TIME_TO_LIVE_SECONDS));
		return toJson(messageMap);
	}
	
	private String toJson(final Object message) {
		try {
			return objectMapper.writeValueAsString(message);
		} catch (final JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static class AlertMessage {
		@JsonProperty
		private final String message;
		@JsonProperty
		private final long patientId;
		
		public AlertMessage(final String message, final long patientId) {
			this.message = message;
			this.patientId = patientId;
		}
	}
}
