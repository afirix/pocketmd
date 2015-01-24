package org.coursera.android.capstone.client.service;

import java.util.Collection;

import org.coursera.android.capstone.client.model.CheckIn;
import org.coursera.android.capstone.client.model.Doctor;
import org.coursera.android.capstone.client.model.Patient;
import org.coursera.android.capstone.client.model.Prescription;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface PocketMdServiceApi {

	String TOKEN_PATH = "/oauth/token";
	String PATIENT_PATH = "/patient";
	String PATIENT_BY_ID_PATH = PATIENT_PATH + "/{patientId}";
	String PATIENT_BY_NAME_PATH = PATIENT_PATH + "/name/{patientName}";
	String CHECKIN_PATH = PATIENT_BY_ID_PATH + "/checkin";
	String PRESCRIPTION_PATH = PATIENT_BY_ID_PATH + "/prescription";
	String DOCTOR_PATH = "/doctor";
	String DOCTOR_BY_ID_PATH = DOCTOR_PATH + "/{doctorId}";
	String DOCTOR_PATIENTS_PATH = DOCTOR_BY_ID_PATH + "/patients";
	String GCM_REGISTRATION_ID_PATH = "/gcm/{userId}/registration/{registrationId}";

	String PATIENT_ID_PARAM = "patientId";
	String PATIENT_NAME_PARAM = "patientName";
	String DOCTOR_ID_PARAM = "doctorId";
	String PRESCRIPTION_ID_PARAM = "prescriptionId";
	String USER_ID_PARAM = "userId";
	String REGISTRATION_ID_PARAM = "registrationId";
	String MEDICATION_NAME_PARAM = "medicationName";

	/**
	 * Get the current patient or null if no patient is logged in.
	 */
	@GET(PATIENT_PATH)
	Patient getCurrentPatient();

	/**
	 * Get the current doctor or null if no patient is logged in.
	 */
	@GET(DOCTOR_PATH)
	Doctor getCurrentDoctor();

	/**
	 * Record a check-in for the current patient.
	 */
	@POST(CHECKIN_PATH)
	boolean checkin(
			@Path(PATIENT_ID_PARAM) long patientId,
			@Body CheckIn checkIn);

	/**
	 * Get all prescriptions for the current patient.
	 */
	@GET(PRESCRIPTION_PATH)
	Collection<Prescription> getPrescriptions(
			@Path(PATIENT_ID_PARAM) long patientId);

	/**
	 * Get all patients assigned to the current doctor.
	 */
	@GET(DOCTOR_PATIENTS_PATH)
	Collection<Patient> getPatients(
			@Path(DOCTOR_ID_PARAM) long doctorId);

	/**
	 * Get a patient by the given id.
	 */
	@GET(PATIENT_BY_ID_PATH)
	Patient getPatient(
			@Path(PATIENT_ID_PARAM) long patientId);

	/**
	 * Search patient by its name.
	 */
	@GET(PATIENT_BY_NAME_PATH)
	Patient findPatientByName(
			@Path(PATIENT_NAME_PARAM) String patientName);

	/**
	 * Get all check-ins for the patient with a given id.
	 */
	@GET(CHECKIN_PATH)
	Collection<CheckIn> getCheckInsForPatient(
			@Path(PATIENT_ID_PARAM) long patientId);

	/**
	 * Assign a new medication for the patient with a given id.
	 */
	@POST(PRESCRIPTION_PATH)
	Prescription assignMedication(
			@Path(PATIENT_ID_PARAM) long patientId,
			@Query(MEDICATION_NAME_PARAM) String medication);

	/**
	 * Unassign medications with given prescription ids.
	 */
	@DELETE(PRESCRIPTION_PATH)
	boolean unassignMedications(
			@Path(PATIENT_ID_PARAM) long patientId,
			@Query(PRESCRIPTION_ID_PARAM) Long[] prescriptionId);

	/**
	 * Send Google Cloud Messaging registration id to the server.
	 */
	@PUT(GCM_REGISTRATION_ID_PATH)
	boolean sendGcmRegistrationId(
			@Path(USER_ID_PARAM) long userId,
			@Path(REGISTRATION_ID_PARAM) String registrationId);
}
