/********************************************************************************************************2*4*w*
 * File:  PeerTutorRegistration.java Course materials CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 * Updated by:  Group 16
 *   041068533, Lei, Luo 
 *   041062508, Yang, Mi 
 *   041066092, Yueying, Li 
 *   041079885, Miao, Yang  
 *   Date modified: 2024-07-24
 *   
 */
package acmecollege.entity;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The persistent class for the peer_tutor_registration database table.
 */
@Entity
@Table(name = "peer_tutor_registration")
@Access(AccessType.FIELD)
@NamedQuery(name = PeerTutorRegistration.ALL_REGISTRATIONS_QUERY_NAME, query = "SELECT distinct ptr FROM PeerTutorRegistration ptr LEFT JOIN FETCH ptr.peerTutor LEFT JOIN FETCH ptr.course")
@NamedQuery(
	    name = PeerTutorRegistration.REGISTRATION_BY_ID_QUERY_NAME,
	    query = "SELECT DISTINCT ptr FROM PeerTutorRegistration ptr LEFT JOIN FETCH ptr.peerTutor LEFT JOIN FETCH ptr.course WHERE ptr.id.studentId = :param1 AND ptr.id.courseId = :param2"
	)
@NamedQuery(name = PeerTutorRegistration.IS_DUPLICATE_QUERY, query = "SELECT COUNT(ptr) FROM PeerTutorRegistration ptr WHERE ptr.id.studentId = :param1 AND ptr.id.courseId = :param2")
public class PeerTutorRegistration extends PojoBaseCompositeKey<PeerTutorRegistrationPK> implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ALL_REGISTRATIONS_QUERY_NAME = "PeerTutorRegistration.findAll";
	public static final String REGISTRATION_BY_ID_QUERY_NAME = "PeerTutorRegistration.findById";
	public static final String IS_DUPLICATE_QUERY = "PeerTutorRegistration.findDuplication";
	// Hint - What annotation is used for a composite primary key type?
	@EmbeddedId
	private PeerTutorRegistrationPK id;

	// @MapsId is used to map a part of composite key to an entity.
	@MapsId("studentId")
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	 @JsonBackReference(value="student-peer")
	private Student student;

	@MapsId("courseId")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
    @JsonBackReference(value="peerTutorRegistration-course")
	private Course course;

	@ManyToOne
	@JoinColumn(name = "peer_tutor_id")
	 @JsonIgnoreProperties({"peertutors"})
	private PeerTutor peerTutor;

	@Column(name = "numeric_grade")
	private int numericGrade;

	@Column(length = 3, name = "letter_grade")
	private String letterGrade;

	public PeerTutorRegistration() {
		id = new PeerTutorRegistrationPK();
	}

	@Override
	public PeerTutorRegistrationPK getId() {
		return id;
	}

	@Override
	public void setId(PeerTutorRegistrationPK id) {
		this.id = id;
	}

	 
	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		id.setStudentId(student.id);
		this.student = student;
	}

	 
	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		id.setCourseId(course.id);
		this.course = course;
	}


	public PeerTutor getPeerTutor() {
		return peerTutor;
	}

	public void setPeerTutor(PeerTutor peerTutor) {
		this.peerTutor = peerTutor;
	}

	public int getNumericGrade() {
		return numericGrade;
	}

	public void setNumericGrade(int numericGrade) {
		this.numericGrade = numericGrade;
	}

	public String getLetterGrade() {
		return letterGrade;
	}

	public void setLetterGrade(String letterGrade) {
		this.letterGrade = letterGrade;
	}

	// Inherited hashCode/equals is sufficient for this entity class

}