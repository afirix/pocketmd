package org.coursera.android.capstone.server.impl.data;

import java.util.List;

import org.coursera.android.capstone.common.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRepository extends
		JpaRepository<Prescription, Long> {

	List<Prescription> findByPatientId(long patientId);
}
