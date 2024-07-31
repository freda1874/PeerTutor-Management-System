/********************************************************************************************************2*4*w*
 * File:  Course.java Course materials CST 8277
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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference; 
/**
 * The persistent class for the course database table.
 */
@Entity
@Table(name = "Course")
@AttributeOverride(name = "id", column = @Column(name = "course_id"))
@NamedQuery(name = Course.ALL_COURSES_QUERY_NAME, query = "SELECT distinct c FROM Course c LEFT JOIN FETCH c.peerTutorRegistrations")
@NamedQuery(name = Course.COURSE_BY_ID_QUERY_NAME, query = "SELECT c FROM Course c LEFT JOIN FETCH c.peerTutorRegistrations WHERE c.id = :param1")
public class Course extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ALL_COURSES_QUERY_NAME = "Course.findAll";
	public static final String COURSE_BY_ID_QUERY_NAME = "Course.findById";
	
	@Basic(optional = false)
	@Column(name = "course_code", nullable = false, length = 7)
	private String courseCode;

	@Basic(optional = false)
    @Column(name = "course_title", nullable = false, length = 100)
	private String courseTitle;

	@Basic(optional = false)
	@Column(name = "year",  nullable = false)
	private int year;

	@Basic(optional = false)
	@Column(name = "semester",  nullable = false, length = 6)
	private String semester;

	@Basic(optional = false)
	@Column(name = "credit_units",  nullable = false)
	private int creditUnits;

	@Basic(optional = false)
	@Column(name = "online",  nullable = false)
	private byte online;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "course")
	@JsonManagedReference("peerTutorRegistration-course")
	private Set<PeerTutorRegistration> peerTutorRegistrations = new HashSet<>();

	public Course() {
		super();
	}

	public Course(String courseCode, String courseTitle, int year, String semester, int creditUnits, byte online) {
		this();
		this.courseCode = courseCode;
		this.courseTitle = courseTitle;
		this.year = year;
		this.semester = semester;
		this.creditUnits = creditUnits;
		this.online = online;
	}

	public Course setCourse(String courseCode, String courseTitle, int year, String semester, int creditUnits,
			byte online) {
		setCourseCode(courseCode);
		setCourseTitle(courseTitle);
		setYear(year);
		setSemester(semester);
		setCreditUnits(creditUnits);
		setOnline(online);
		return this;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public int getCreditUnits() {
		return creditUnits;
	}

	public void setCreditUnits(int creditUnits) {
		this.creditUnits = creditUnits;
	}

	public byte getOnline() {
		return online;
	}

	public void setOnline(byte online) {
		this.online = online;
	}

	 
	public Set<PeerTutorRegistration> getPeerTutorRegistrations() {
		return peerTutorRegistrations;
	}

	public void setPeerTutorRegistrations(Set<PeerTutorRegistration> peerTutorRegistrations) {
		this.peerTutorRegistrations = peerTutorRegistrations;
	}

	// Inherited hashCode/equals is sufficient for this Entity class

}
