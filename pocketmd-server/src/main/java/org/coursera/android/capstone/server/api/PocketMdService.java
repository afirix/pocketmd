package org.coursera.android.capstone.server.api;

import java.util.Collection;

import org.coursera.android.capstone.common.model.CheckIn;
import org.coursera.android.capstone.common.model.Doctor;
import org.coursera.android.capstone.common.model.Patient;
import org.coursera.android.capstone.common.model.Prescription;

public interface PocketMdService {

	String PATIENT_PATH = "/patient";
	String PATIENT_BY_ID_PATH = PATIENT_PATH + "/{patientId}";
	String PATIENT_BY_NAME_PATH = PATIENT_PATH + "/name/{patientName}";
	String CHECKIN_PATH = PATIENT_BY_ID_PATH + "/checkin";
	String CHECKIN_BY_ID_PATH = CHECKIN_PATH + "/{checkinId}";
	String PRESCRIPTION_PATH = PATIENT_BY_ID_PATH + "/prescription";
	String DOCTOR_PATH = "/doctor";
	String DOCTOR_BY_ID_PATH = DOCTOR_PATH + "/{doctorId}";
	String DOCTOR_PATIENTS_PATH = DOCTOR_BY_ID_PATH + "/patients";
	String GCM_REGISTRATION_ID_PATH = "/gcm/{userId}/registration/{registrationId}";
	
	String PATIENT_ID_PARAM = "patientId";
	String PATIENT_NAME_PARAM = "patientName";
	String DOCTOR_ID_PARAM = "doctorId";
	String CHECKIN_ID_PARAM = "checkinId";
	String PRESCRIPTION_ID_PARAM = "prescriptionId";
	String USER_ID_PARAM = "userId";
	String REGISTRATION_ID_PARAM = "registrationId";
	String MEDICATION_NAME_PARAM = "medicationName";

	Patient getCurrentPatient();
	
	Doctor getCurrentDoctor();
	
	boolean checkin(long patientId, CheckIn checkin);
	
	Collection<Prescription> getPrescriptions(long patientId);
	
	Collection<Patient> getPatients(long doctorId);
	
	Patient getPatient(long patientId);
	
	Patient findPatientByName(String patientName);
	
	Collection<CheckIn> getCheckInsForPatient(long patientId);
	
	Prescription assignMedication(long patientId, String medication);
	
	boolean unassignMedications(long patientId, Long[] prescriptionIds);
	
	boolean sendGcmRegistrationId(long userId, String registrationId);
}
