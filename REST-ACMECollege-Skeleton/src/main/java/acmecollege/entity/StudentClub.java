/********************************************************************************************************2*4*w*
 * File:  StudentClub.java Course materials CST 8277
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
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The persistent class for the student_club database table.
 */
@Entity
@Table(name = "student_club")
@AttributeOverride(name = "id", column = @Column(name = "club_id"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "academic", columnDefinition = "BIT(1)", discriminatorType = DiscriminatorType.INTEGER)
@NamedQuery(name = StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_ID, query = "SELECT distinct sc FROM StudentClub sc left join fetch sc.clubMemberships WHERE sc.id=:param1")
@NamedQuery(name = StudentClub.STUDENT_CLUB_QUERY, query = "SELECT distinct sc FROM StudentClub sc left join fetch sc.clubMemberships ")
@NamedQuery(name = StudentClub.IS_DUPLICATE_QUERY_NAME, query = "SELECT COUNT(sc.name) FROM StudentClub sc WHERE sc.name = :param1")
//Add in JSON annotations to indicate different sub-classes of StudentClub
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "entity-type")
@JsonSubTypes({ @Type(value = AcademicStudentClub.class, name = "academic_student_club"),
		@Type(value = NonAcademicStudentClub.class, name = "non_academic_student_club") })
public abstract class StudentClub extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String SPECIFIC_STUDENT_CLUB_QUERY_ID = "StudentClub.findById";
	public static final String IS_DUPLICATE_QUERY_NAME = "StudentClub.duplicateName";
	public static final String STUDENT_CLUB_QUERY = "StudentClub.findAll";

	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "club", orphanRemoval = true)
	@JsonIgnore
	private Set<ClubMembership> clubMemberships = new HashSet<>();

	@Transient
	private boolean isAcademic;

	public StudentClub() {
		super();
	}

	public StudentClub(boolean isAcademic) {
		this();
		this.isAcademic = isAcademic;
	}

	public Set<ClubMembership> getClubMemberships() {
		return clubMemberships;
	}

	public void setClubMembership(Set<ClubMembership> clubMemberships) {
		this.clubMemberships = clubMemberships;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// Inherited hashCode/equals is NOT sufficient for this entity class

	/**
	 * Very important: Use getter's for member variables because JPA sometimes needs
	 * to intercept those calls<br/>
	 * and go to the database to retrieve the value
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		// Only include member variables that really contribute to an object's identity
		// i.e. if variables like version/updated/name/etc. change throughout an
		// object's lifecycle,
		// they shouldn't be part of the hashCode calculation

		// The database schema for the STUDENT_CLUB table has a UNIQUE constraint for
		// the NAME column,
		// so we should include that in the hash/equals calculations

		return prime * result + Objects.hash(getId(), getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (obj instanceof StudentClub otherStudentClub) {
			// See comment (above) in hashCode(): Compare using only member variables that
			// are
			// truly part of an object's identity
			return Objects.equals(this.getId(), otherStudentClub.getId())
					&& Objects.equals(this.getName(), otherStudentClub.getName());
		}
		return false;
	}
}
