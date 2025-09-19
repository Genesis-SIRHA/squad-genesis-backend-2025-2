package repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import model.Request;
import java.util.Date;
import java.util.List;

@Repository
public interface RequestRepositoryInterface extends MongoRepository<Request, String> {

    List<Request> findByStudentId(String studentId);

    @Query("{ 'status': 'PENDING' }")
    List<Request> findAllAvailable();

    @Query("{ 'status':}")
    List<Request> findAllOwned(String userId);

    @Query("{ 'status': 'EXCEPTIONAL' }")
    List<Request> queryExceptionRequest();

    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Request> findRequestsBetweenDates(Date startDate, Date endDate);

    @Query("{ 'status': { $in: ?0 } }")
    List<Request> findByStatusIn(List<String> statuses);

    long countByStatus(String status);

    @Query("{ 'createdAt': { $gte: ?0 } }")
    List<Request> findRecentRequests(Date date);
}
