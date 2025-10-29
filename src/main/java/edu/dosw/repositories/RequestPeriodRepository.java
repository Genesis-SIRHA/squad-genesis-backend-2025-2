package edu.dosw.repositories;

import edu.dosw.dto.RequestPeriodDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RequestPeriodRepository extends MongoRepository<RequestPeriodDTO, String> {
  @Query("{ 'isActive': true }")
  RequestPeriodDTO activePeriod();

  RequestPeriodDTO getById(String id);
}
