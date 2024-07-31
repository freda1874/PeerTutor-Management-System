/********************************************************************************************************2*4*w*
 * File:  MembershipCard.java Course materials CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 *  Updated by:  Group 16
 *   041068533, Lei, Luo 
 *   041062508, Yang, Mi 
 *   041066092, Yueying, Li 
 *   041079885, Miao, Yang  
 *   Date modified: 2024-07-24
 *   
 */
package acmecollege.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
 
 

/**
 * The persistent class for the membership_card database table.
 */
@Entity
@Table(name = "membership_card")
@AttributeOverride(name = "id", column = @Column(name = "card_id"))
@NamedQuery(name = MembershipCard.ID_CARD_QUERY_NAME, query =
"SELECT  mc FROM MembershipCard mc left join fetch mc.owner left join fetch mc.clubMembership WHERE mc.id=:param1")
@NamedQuery(name = MembershipCard.CARD_QUERY_NAME, query =
"SELECT  mc FROM MembershipCard mc left join fetch mc.owner left join fetch mc.clubMembership ")
@NamedQuery(name = MembershipCard.CARD_QUERY_By_Student_ClubMembership, query =
"SELECT  mc FROM MembershipCard mc left join fetch mc.owner left join fetch mc.clubMembership where mc.owner=:param1 and mc.clubMembership=:param2")
public class MembershipCard extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String ID_CARD_QUERY_NAME = "MembershipCard.findById";
	public static final String CARD_QUERY_NAME = "MembershipCard.findAll";
	public static final String CARD_QUERY_By_Student_ClubMembership ="MembershipCard.findbystudentandclubmembership";
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinColumn(name="membership_id", referencedColumnName = "membership_id")
	@JsonManagedReference(value = "club-membershipCard")
	private ClubMembership clubMembership;

	 
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	 @JsonIgnoreProperties({"membershipCards"})
	private Student owner;

	 
	@Basic(optional = false)
	@Column(name="signed", columnDefinition = "BIT(1)", nullable = false)
	private byte signed;

	public MembershipCard() {
		super();
	}
	
	public MembershipCard(ClubMembership clubMembership, Student owner, byte signed) {
		this();
		this.clubMembership = clubMembership;
		this.owner = owner;
		this.signed = signed;
	}

    
	public ClubMembership getClubMembership() {
		return clubMembership;
	}

	public void setClubMembership(ClubMembership clubMembership) {
		this.clubMembership = clubMembership;
	}
	
	
	public Student getOwner() {
		return owner;
	}

	public void setOwner(Student owner) {
		this.owner = owner;
	}

	public byte getSigned() {
		return signed;
	}

	public boolean isSigned() {
        return this.signed == (byte) 0b0001;
    }
	
	public void setSigned(boolean signed) {
		this.signed = (byte) (signed ? 0b0001 : 0b0000);
	}
	
	@Override
	public String toString() {
		return "MembershipCard{" +
				"id=" + getId() +
				", clubMembership=" + (clubMembership != null ? clubMembership.getId() : "null") +
				", owner=" + (owner != null ? owner.getId() : "null") +
				", signed=" + isSigned() +
				'}';
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}