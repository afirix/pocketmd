package org.coursera.android.capstone.server.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.coursera.android.capstone.common.model.CheckIn;
import org.coursera.android.capstone.common.model.Doctor;
import org.coursera.android.capstone.common.model.Patient;
import org.coursera.android.capstone.common.model.Person;
import org.coursera.android.capstone.common.model.Prescription;
import org.coursera.android.capstone.server.api.PocketMdService;
import org.coursera.android.capstone.server.exception.NotFoundException;
import org.coursera.android.capstone.server.exception.UnauthorizedAccessAttemptException;
import org.coursera.android.capstone.server.impl.alert.DoctorAlert;
import org.coursera.android.capstone.server.impl.alert.DoctorAlertChecker;
import org.coursera.android.capstone.server.impl.alert.DoctorAlertSender;
import org.coursera.android.capstone.server.impl.data.CheckInRepository;
import org.coursera.android.capstone.server.impl.data.DoctorRepository;
import org.coursera.android.capstone.server.impl.data.GcmRegistrationRepository;
import org.coursera.android.capstone.server.impl.data.PatientRepository;
import org.coursera.android.capstone.server.impl.data.PrescriptionRepository;
import org.coursera.android.capstone.server.model.GcmRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

@Controller
public class PocketMdServiceImpl implements PocketMdService {

	private static final Logger LOG = LoggerFactory.getLogger(PocketMdServiceImpl.class);
	
	@Resource
	private PatientRepository patientRepository;

	@Resource
	private DoctorRepository doctorRepository;

	@Resource
	private CheckInRepository checkInRepository;

	@Resource
	private PrescriptionRepository prescriptionRepository;

	@Resource
	private GcmRegistrationRepository gcmRegistrationRepository;

	@Resource
	private DoctorAlertChecker doctorAlertChecker;

	@Resource
	private DoctorAlertSender doctorAlertSender;

	@Override
	@RequestMapping(value = PATIENT_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Patient getCurrentPatient() {
		final UserDetails user = getCurrentUser();
		final Patient patient = patientRepository.findByUsername(user
				.getUsername());
		LOG.info("Request: getCurrentPatient() | coming from username {}",
				user.getUsername());
		return patient;
	}

	@Override
	@RequestMapping(value = DOCTOR_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Doctor getCurrentDoctor() {
		final UserDetails user = getCurrentUser();
		final Doctor doctor = doctorRepository.findByUsername(user
				.getUsername());
		LOG.info("Request: getCurrentDoctor() | coming from username {}",
				user.getUsername());
		return doctor;
	}

	@Override
	@RequestMapping(value = CHECKIN_PATH, method = RequestMethod.POST)
	@ResponseBody
	public boolean checkin(
			@PathVariable(PATIENT_ID_PARAM) final long patientId,
			@RequestBody final CheckIn checkin) {
		final Patient patient = getCurrentPatient();
		LOG.info(
				"Request: checkin(), patientId = {}, checkin = {} | coming from user {}",
				patientId, checkin.toString(), patient.getId());
		
		validateUserId(patient, patientId);
		
		patient.getCheckIns().add(checkin);
		checkInRepository.save(checkin);
		patientRepository.save(patient);
		
		LOG.info("New check-in has been saved for patient {}", patientId);

		final Date alertTestTimeWindowEnd = checkin.getDateTime();
		final Date alertTestTimeWindowStart = TimeUtils.minusHours(
				alertTestTimeWindowEnd,
				doctorAlertChecker.getMaxHoursThreshold());
		List<CheckIn> checkInHistory = checkInRepository
				.findRecentCheckInHistory(alertTestTimeWindowStart, patientId);
		if (checkInHistory.isEmpty()) {
			checkInHistory = checkInRepository.findByPatientId(patientId);
		}
		final Set<DoctorAlert> alerts = doctorAlertChecker.check(
				checkInHistory, alertTestTimeWindowEnd, patient);
		if (!alerts.isEmpty()) {
			LOG.info("{} alerts have been detected for patient {}",
					alerts.size(), patientId);
			doctorAlertSender.send(patient, alerts);
		}

		return true;
	}

	@Override
	@RequestMapping(value = PRESCRIPTION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Collection<Prescription> getPrescriptions(
			@PathVariable(PATIENT_ID_PARAM) final long patientId) {
		final Patient patient = getCurrentPatient();
		LOG.info(
				"Request: getPrescriptions(), patientId = {} | coming from user {}",
				patientId, patient.getId());
		
		validateUserId(patient, patientId);

		final List<Prescription> prescriptions = prescriptionRepository
				.findByPatientId(patient.getId());
		LOG.info("{} prescriptions have been found for patient {}",
				prescriptions.size(), patient.getId());
		return prescriptions;
	}

	@Override
	@RequestMapping(value = DOCTOR_PATIENTS_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Collection<Patient> getPatients(
			@PathVariable(DOCTOR_ID_PARAM) final long doctorId) {
		final Doctor doctor = getCurrentDoctor();
		LOG.info(
				"Request: getPatients(), doctorId = {} | coming from user {}",
				doctorId, doctor.getId());
		
		validateUserId(doctor, doctorId);

		final List<Patient> patients = patientRepository.findByDoctorId(doctor
				.getId());
		LOG.info("{} patients have been found for doctor {}",
				patients.size(), doctorId);
		return patients;
	}
	
	@Override
	@RequestMapping(value = PATIENT_BY_ID_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Patient getPatient(
			@PathVariable(PATIENT_ID_PARAM) final long patientId) {
		final Doctor doctor = getCurrentDoctor();
		LOG.info(
				"Request: getPatient(), patientId = {} | coming from user {}",
				patientId, doctor.getId());
		
		final Patient patient = patientRepository.findOne(patientId);
		validatePatient(patient, doctor.getId());
		
		LOG.info("Patient {} has been found by ID", patientId);
		return patient;
	}

	@Override
	@RequestMapping(value = PATIENT_BY_NAME_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Patient findPatientByName(
			@PathVariable(PATIENT_NAME_PARAM) final String patientName) {
		final Doctor doctor = getCurrentDoctor();
		LOG.info(
				"Request: findPatientByName(), patientName = {} | coming from user {}",
				patientName, doctor.getId());
		
		final Patient patient = patientRepository.findByFullName(patientName,
				doctor.getId());
		validatePatient(patient, doctor.getId());
		
		LOG.info("Patient {} has been found by name", patient.getId());
		return patient;
	}

	@Override
	@RequestMapping(value = CHECKIN_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Collection<CheckIn> getCheckInsForPatient(
			@PathVariable(PATIENT_ID_PARAM) final long patientId) {
		final Doctor doctor = getCurrentDoctor();
		LOG.info(
				"Request: getCheckInsForPatient(), patientId = {} | coming from user {}",
				patientId, doctor.getId());
		
		final Collection<CheckIn> checkins = getPatientByPatientId(patientId).getCheckIns();
		LOG.info("{} check-ins have been found for patient {}",
				checkins.size(), patientId);
		return checkins;
	}

	@Override
	@RequestMapping(value = PRESCRIPTION_PATH, method = RequestMethod.POST)
	@ResponseBody
	public Prescription assignMedication(
			@PathVariable(PATIENT_ID_PARAM) final long patientId,
			@RequestParam(MEDICATION_NAME_PARAM) final String medication) {
		final Doctor doctor = getCurrentDoctor();
		final Patient patient = getPatientByPatientId(patientId);
		LOG.info(
				"Request: assignMedication(), patientId = {}, medication = {} | coming from user {}",
				patientId, medication, doctor.getId());
		
		validateUserId(patient, patientId);

		final Prescription prescription = new Prescription();
		prescription.setMedicationName(medication);
		patient.getPrescriptions().add(prescription);
		prescriptionRepository.save(prescription);
		patientRepository.save(patient);
		
		LOG.info("Medication {} has been assigned for patient {}", medication,
				patientId);

		return prescription;
	}

	@Override
	@RequestMapping(value = PRESCRIPTION_PATH, method = RequestMethod.DELETE)
	@ResponseBody
	public boolean unassignMedications(
			@PathVariable(PATIENT_ID_PARAM) final long patientId,
			@RequestParam(PRESCRIPTION_ID_PARAM) final Long[] prescriptionIds) {
		final Doctor doctor = getCurrentDoctor();
		LOG.info(
				"Request: unassignMedications(), patientId = {}, prescriptionIds = {} | coming from user {}",
				patientId, Arrays.toString(prescriptionIds), doctor.getId());
		
		final List<Prescription> prescriptions = prescriptionRepository
				.findAll(Arrays.asList(prescriptionIds));

		final Patient patient = patientRepository.findOne(patientId);
		validatePatient(patient, doctor.getId());

		patient.getPrescriptions().removeAll(prescriptions);
		prescriptionRepository.deleteInBatch(prescriptions);
		patientRepository.save(patient);
		
		LOG.info("{} medications have been removed from patient {}",
				prescriptions.size(), patientId);

		return true;
	}

	@Override
	@RequestMapping(
			value = GCM_REGISTRATION_ID_PATH,
			method = RequestMethod.PUT)
	@ResponseBody
	public boolean sendGcmRegistrationId(
			@PathVariable(USER_ID_PARAM) final long userId,
			@PathVariable(REGISTRATION_ID_PARAM) final String registrationId) {
		LOG.info(
				"Request: sendGcmRegistrationId(), userId = {}, registrationId = {} | coming from user {}",
				userId, registrationId, userId);
		
		final byte[] registrationIdBytes = registrationId
				.getBytes(Charsets.UTF_8);
		final HashCode registrationIdHash = Hashing.sha256()
				.hashString(registrationId, Charsets.UTF_8);
		final byte[] registrationIdHashBytes = registrationIdHash.asBytes();

		final Collection<GcmRegistration> existingRegistrations = gcmRegistrationRepository
				.findByRegistrationIdHash(registrationIdHashBytes);
		LOG.info("{} have been found for registration ID hash {}",
				existingRegistrations.size(), registrationIdHash.toString());
		
		if (existingRegistrations.isEmpty()) {
			// new device signs in
			final GcmRegistration newDeviceRegistration = new GcmRegistration(
					userId, registrationIdBytes, registrationIdHashBytes);
			gcmRegistrationRepository.save(newDeviceRegistration);
			LOG.info("New registration ID has been added for user {}", userId);
		} else {
			boolean matchingRegistrationIdFound = false;
			// try to find a record with the same registration id
			for (final GcmRegistration existingRegistration : existingRegistrations) {
				if (Arrays.equals(registrationIdBytes,
						existingRegistration.getRegistrationId())) {
					matchingRegistrationIdFound = true;
					if (userId != existingRegistration.getUserId()) {
						// new user signs in from the same device
						existingRegistration.setUserId(userId);
						gcmRegistrationRepository.save(existingRegistration);
						LOG.info("Registration ID has been reused for user {}",
								userId);
					}
					break;
				}
			}

			if (!matchingRegistrationIdFound) {
				// hash collision happened (different registration ids led to
				// the same hash)
				final GcmRegistration newDeviceRegistration = new GcmRegistration(
						userId, registrationIdBytes, registrationIdHashBytes);
				gcmRegistrationRepository.save(newDeviceRegistration);
				LOG.info(
						"Registration ID hash collision has been detected for hash {}",
						registrationIdHash.toString());
				LOG.info("New registration ID has been added for user {}",
						userId);
			}
		}

		return true;
	}

	private UserDetails getCurrentUser() {
		return (UserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
	}

	private Patient getPatientByPatientId(final long patientId) {
		final Doctor doctor = getCurrentDoctor();
		final Patient patient = patientRepository.findOne(patientId);
		validatePatient(patient, doctor.getId());
		return patient;
	}

	private void validatePatient(final Patient patient, final long doctorId) {
		if (patient == null) {
			LOG.error("Patient has not been found for doctor {}", doctorId);
			throw new NotFoundException("Patient not found");
		}
		if (patient.getDoctorId() != doctorId) {
			LOG.error("Patient {} is not assigned to doctor {}",
					patient.getId(), doctorId);
			throw new UnauthorizedAccessAttemptException(String.format(
					"Patient with the ID={} is assigned to other doctor",
					patient.getId()));
		}
	}

	private void validateUserId(final Person user, final long id) {
		if (user.getId() != id) {
			LOG.error(
					"Given ID {} does not equal to the ID of the current user",
					id);
			throw new UnauthorizedAccessAttemptException(String.format(
					"Given ID={} does not equal to the ID of the current user",
					id));
		}
	}
}
