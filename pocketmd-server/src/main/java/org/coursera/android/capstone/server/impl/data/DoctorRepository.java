package org.coursera.android.capstone.server.impl.data;

import org.coursera.android.capstone.common.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

	Doctor findByUsername(String username);
}
