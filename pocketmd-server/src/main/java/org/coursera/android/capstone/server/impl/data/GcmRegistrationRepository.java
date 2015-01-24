package org.coursera.android.capstone.server.impl.data;

import java.util.List;

import org.coursera.android.capstone.server.model.GcmRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GcmRegistrationRepository extends
		JpaRepository<GcmRegistration, Long> {

	List<GcmRegistration> findByUserId(long userId);
	
	List<GcmRegistration> findByRegistrationIdHash(byte[] registrationIdHash);
}
