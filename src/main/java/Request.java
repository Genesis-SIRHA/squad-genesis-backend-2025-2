import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Request {
    private String id;
    private String studentId;
    private Group destinationGroup;
    private Group originGroup;
    private String description;
    private String status;
    private Date createdAt;
    public Date getCreated;
    private RequestDetails requestDetails;
    public Request() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.status = "PENDING";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public RequestDetails getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(RequestDetails requestDetails) {
        this.requestDetails = requestDetails;
    }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
}

