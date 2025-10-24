package edu.dosw.repositories;

import edu.dosw.model.RequestHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RequestHistoryRepository extends MongoRepository<RequestHistory, String> {
}
