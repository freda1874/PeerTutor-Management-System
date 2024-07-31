/********************************************************************************************************2*4*w*
 * File:  PeerTutor.java Course materials CST 8277
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference; 


/**
 * The persistent class for the peer_tutor database table.
 */ 
@Entity 
@Table(name = "peer_tutor")  
@NamedQuery(name = PeerTutor.IS_DUPLICATE_QUERY_NAME, query = "SELECT count(pt) FROM PeerTutor pt where pt.firstName = :param1 and pt.lastName = :param2 and pt.program = :param3")
@NamedQuery(name = PeerTutor.QUERY_PEER_TUTOR_BY_NAME_PROGRAM, query = "SELECT pt FROM PeerTutor pt where pt.firstName = :param1 and pt.lastName = :param2 and pt.program = :param3") 
@NamedQuery(name = PeerTutor.QUERY_PEER_TUTOR_ALL, query = "SELECT pt FROM PeerTutor pt left join fetch pt.peerTutorRegistrations ") 
@NamedQuery(name = PeerTutor.QUERY_PEER_TUTOR_BY_ID, query = "SELECT pt FROM PeerTutor pt left join fetch pt.peerTutorRegistrations where pt.id=:param1 ") 
@AttributeOverride(name = "id", column = @Column(name = "peer_tutor_id")) 
public class PeerTutor extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

    public static final String IS_DUPLICATE_QUERY_NAME = "PeerTutor.isDuplicate";
    public static final String QUERY_PEER_TUTOR_BY_NAME_PROGRAM = "PeerTutor.findByNameProgram";
    public static final String QUERY_PEER_TUTOR_ALL = "PeerTutor.findAll";
    public static final String QUERY_PEER_TUTOR_BY_ID = "PeerTutor.findById";

	// Hint - @Basic(optional = false) is used when the object cannot be null.
	// Hint - @Basic or none can be used if the object can be null.
	// Hint - @Basic is for checking the state of object at the scope of our code.
	@Basic(optional = false)
	// Hint - @Column is used to define the details of the column which this object will map to.
	// Hint - @Column is for mapping and creation (if needed) of an object to DB.
	// Hint - @Column can also be used to define a specific name for the column if it is different than our object name.
	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@Basic(optional = false)
	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@Basic(optional = false)
	@Column(name = "program", nullable = false, length = 50)
	private String program;

	// Hint - @Transient is used to annotate a property or field of an entity class, mapped superclass, or embeddable class which is not persistent.
	@Transient
	private String hobby; // Examples:  Tennis, Cycling, etc.
	
	@Transient
	private String careerGoal; // Examples:  Become a teacher, etc.

	// Hint - @OneToMany is used to define 1:M relationship between this entity and another.
	// Hint - @OneToMany option cascade can be added to define if changes to this entity should cascade to objects.
	// Hint - @OneToMany option cascade will be ignored if not added, meaning no cascade effect.
	// Hint - @OneToMany option fetch should be lazy to prevent eagerly initializing all the data.
	@OneToMany(cascade=CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "peerTutor")
	// Hint - java.util.Set is used as a collection, however List could have been used as well.
	// Hint - java.util.Set will be unique and also possibly can provide better get performance with HashCode.
	  @JsonBackReference
	private Set<PeerTutorRegistration> peerTutorRegistrations = new HashSet<>();

	public PeerTutor() {
		super();
	}
	
	public PeerTutor(String firstName, String lastName, String program, Set<PeerTutorRegistration> peerTutorRegistrations) {
		this();
		this.firstName = firstName;
		this.lastName = lastName;
		this.program = program;
		this.peerTutorRegistrations = peerTutorRegistrations;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getCareeGoal() {
		return careerGoal;
	}

	public void setCareerGoal(String careerGoal) {
		this.careerGoal = careerGoal;
	}

	 
	public Set<PeerTutorRegistration> getPeerTutorRegistrations() {
		return peerTutorRegistrations;
	}

	public void setPeerTutorRegistrations(Set<PeerTutorRegistration> peerTutorRegistrations) {
		this.peerTutorRegistrations = peerTutorRegistrations;
	}

	public void setPeerTutor(String firstName, String lastName, String program) {
		setFirstName(firstName);
		setLastName(lastName);
		setProgram(program);
	}

	//Inherited hashCode/equals is sufficient for this entity class

}
