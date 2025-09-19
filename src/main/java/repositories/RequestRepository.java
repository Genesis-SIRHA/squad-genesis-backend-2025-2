package repositories;

import model.Request;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RequestRepository extends MongoRepository<Request, String> {
    @Query("{ 'status': 'PENDING' }")
    List<Request> findAllAvailable();

    @Query("{ 'status': 'EXCEPTIONAL' }")
    List<Request> queryExceptionRequest();

    long countByStatus(String status);


}
