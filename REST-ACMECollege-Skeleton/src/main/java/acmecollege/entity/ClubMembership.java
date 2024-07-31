/********************************************************************************************************2*4*w*
 * File:  ClubMembership.java Course materials CST 8277
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
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table; 

import com.fasterxml.jackson.annotation.JsonBackReference; 

/**
 * The persistent class for the club_membership database table.
 */
@Entity
@Table(name = "club_membership")
@AttributeOverride(name = "id", column = @Column(name = "membership_id"))
@NamedQuery(name = ClubMembership.FIND_BY_ID, query = "SELECT cm FROM ClubMembership cm left join fetch cm.club WHERE cm.id = :param1")
@NamedQuery(name = ClubMembership.FIND_ALL, query = "SELECT  cm FROM ClubMembership cm left join fetch cm.club  ")
public class ClubMembership extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String FIND_BY_ID = "ClubMembership.findById";
	public static final String FIND_ALL = "ClubMembership.findAll";

	// CM03 - Add annotations for M:1. Changes to this class should cascade to
	// StudentClub.
	@ManyToOne(cascade ={CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JoinColumn(name = "club_id", referencedColumnName = "club_id")
	@JsonBackReference 
	private StudentClub club;

	@OneToOne(mappedBy = "clubMembership")
	@JsonBackReference(value = "club-membershipCard")
	private MembershipCard card;

	@Embedded
	private DurationAndStatus durationAndStatus;

	public ClubMembership() {
		durationAndStatus = new DurationAndStatus();
	}

	
	public StudentClub getStudentClub() {
		return club;
	}

	public void setStudentClub(StudentClub club) {
		this.club = club;
	}

	public MembershipCard getCard() {
		return card;
	}

	public void setCard(MembershipCard card) {
		this.card = card;
	}

	public DurationAndStatus getDurationAndStatus() {
		return durationAndStatus;
	}

	public void setDurationAndStatus(DurationAndStatus durationAndStatus) {
		this.durationAndStatus = durationAndStatus;
	}

	// Inherited hashCode/equals NOT sufficient for this Entity class
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

		// include DurationAndStatus in identity
		return prime * result + Objects.hash(getId(), getDurationAndStatus());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof ClubMembership otherClubMembership) {
			// See comment (above) in hashCode(): Compare using only member variables that
			// are
			// truly part of an object's identity
			return Objects.equals(this.getId(), otherClubMembership.getId())
					&& Objects.equals(this.getDurationAndStatus(), otherClubMembership.getDurationAndStatus());
		}
		return false;
	}
}
