package edu.dosw.model;

import java.util.Map;

/**
 * Represents a student's academic record summary, including courses and credits. This class is
 * immutable and should be constructed using its Builder.
 */
public class Pemsum {
  private final String studentId;
  private final String studentName;
  private final String facultyName;
  private final String facultyPlan;
  private final int approvedCredits;
  private final int totalCredits;
  private final Map<Course, String> courses;

  /**
   * Private constructor used by the Builder pattern.
   *
   * @param builder The Builder instance containing all field values.
   */
  private Pemsum(Builder builder) {
    this.studentId = builder.studentId;
    this.studentName = builder.studentName;
    this.facultyName = builder.facultyName;
    this.facultyPlan = builder.facultyPlan;
    this.approvedCredits = builder.approvedCredits;
    this.totalCredits = builder.totalCredits;
    this.courses = builder.courses;
  }

  /**
   * @return The student's unique identifier.
   */
  public String getStudentId() {
    return studentId;
  }

  /**
   * @return The full name of the student.
   */
  public String getStudentName() {
    return studentName;
  }

  /**
   * @return The name of the faculty the student belongs to.
   */
  public String getFacultyName() {
    return facultyName;
  }

  /**
   * @return The academic plan or program the student is enrolled in.
   */
  public String getFacultyPlan() {
    return facultyPlan;
  }

  /**
   * @return The total number of credits the student has approved.
   */
  public int getApprovedCredits() {
    return approvedCredits;
  }

  /**
   * @return The total number of credits required for the program.
   */
  public int getTotalCredits() {
    return totalCredits;
  }

  /**
   * @return A map of courses and their current status for the student.
   */
  public Map<Course, String> getCourses() {
    return courses;
  }

  /**
   * Builder class for creating immutable Pemsum instances. Follows the Builder pattern for flexible
   * object creation.
   */
  public static class Builder {
    private String studentId;
    private String studentName;
    private String facultyName;
    private String facultyPlan;
    private int approvedCredits;
    private int totalCredits;
    private Map<Course, String> courses;

    /**
     * Sets the student's unique identifier.
     *
     * @param studentId The student's ID.
     * @return This builder instance for method chaining.
     */
    public Builder studentId(String studentId) {
      this.studentId = studentId;
      return this;
    }

    /**
     * Sets the student's full name.
     *
     * @param studentName The student's full name.
     * @return This builder instance for method chaining.
     */
    public Builder studentName(String studentName) {
      this.studentName = studentName;
      return this;
    }

    /**
     * Sets the name of the faculty.
     *
     * @param facultyName The faculty name.
     * @return This builder instance for method chaining.
     */
    public Builder facultyName(String facultyName) {
      this.facultyName = facultyName;
      return this;
    }

    /**
     * Sets the academic plan or program.
     *
     * @param facultyPlan The academic plan or program name.
     * @return This builder instance for method chaining.
     */
    public Builder facultyPlan(String facultyPlan) {
      this.facultyPlan = facultyPlan;
      return this;
    }

    /**
     * Sets the number of approved credits.
     *
     * @param approvedCredits The number of approved credits.
     * @return This builder instance for method chaining.
     */
    public Builder approvedCredits(int approvedCredits) {
      this.approvedCredits = approvedCredits;
      return this;
    }

    /**
     * Sets the total number of credits required.
     *
     * @param totalCredits The total credits required.
     * @return This builder instance for method chaining.
     */
    public Builder totalCredits(int totalCredits) {
      this.totalCredits = totalCredits;
      return this;
    }

    /**
     * Sets the map of courses and their statuses.
     *
     * @param courses A map of Course objects to status strings.
     * @return This builder instance for method chaining.
     */
    public Builder courses(Map<Course, String> courses) {
      this.courses = courses;
      return this;
    }

    /**
     * Builds and returns a new Pemsum instance.
     *
     * @return A new immutable Pemsum instance.
     */
    public Pemsum build() {
      return new Pemsum(this);
    }
  }
}
