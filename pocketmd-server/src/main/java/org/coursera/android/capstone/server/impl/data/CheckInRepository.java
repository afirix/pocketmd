package org.coursera.android.capstone.server.impl.data;

import java.util.Date;
import java.util.List;

import org.coursera.android.capstone.common.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
	
	@Query("select c1 from CheckIn c1 where c1.patientId = :patientId and c1.dateTime >= "
			+ "(select max(c2.dateTime) from CheckIn c2 where c2.patientId = :patientId and c2.dateTime <= :dateTime) "
			+ "order by c1.dateTime")
	List<CheckIn> findRecentCheckInHistory(@Param("dateTime") Date dateTime, @Param("patientId") long patientId);
	
	List<CheckIn> findByPatientId(long patientId);
}
