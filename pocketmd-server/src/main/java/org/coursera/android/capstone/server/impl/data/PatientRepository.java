package org.coursera.android.capstone.server.impl.data;

import java.util.List;

import org.coursera.android.capstone.common.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

	Patient findByUsername(String username);

	//List<Patient> findByLastName(String lastName);

	@Query("select p from Patient p where lower(concat(p.firstName, ' ', p.lastName)) = lower(:fullName) and p.doctorId = :doctorId")
	Patient findByFullName(@Param("fullName") String fullName, @Param("doctorId") long doctorId);

	List<Patient> findByDoctorId(long doctorId);
}
