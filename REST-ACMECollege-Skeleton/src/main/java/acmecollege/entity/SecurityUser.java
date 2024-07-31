/********************************************************************************************************2*4*w*
 * File:  SecurityUser.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 *
 * Updated by:  Group 16 
 * 041068533, Lei, Luo 
 * 041062508, Yang, Mi 
 * 041066092, Yueying, Li 
 * 041079885, Miao, Yang  
 * Date modified: 2024-07-24
 * 
 */
package acmecollege.entity;

import static acmecollege.entity.SecurityUser.SECURITY_USER_BY_ID_QUERY;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import acmecollege.rest.serializer.SecurityRoleSerializer; 

/**
 * User class used for (JSR-375) Java EE Security authorization/authentication
 */

@Entity
@Table(name = "security_user")
@NamedQuery(name = SecurityUser.SECURITY_USER_BY_NAME_QUERY,
query = "SELECT u FROM SecurityUser u left join fetch u.student left join fetch u.roles WHERE u.username = :param1")
@NamedQuery(name = SECURITY_USER_BY_ID_QUERY, query = "SELECT u FROM SecurityUser u left join fetch u.student  left join fetch u.roles  WHERE u.id = :param1")
@NamedQuery(name = SecurityUser.SECURITY_USER_BY_Student_ID_QUERY, query = "SELECT u FROM SecurityUser u left join fetch u.student  left join fetch u.roles  WHERE u.student.id = :param1")
public class SecurityUser implements Serializable, Principal {
    /** Explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    public static final String SECURITY_USER_BY_ID_QUERY = "SecurityUser.findById";
    public static final String SECURITY_USER_BY_NAME_QUERY = "SecurityUser.findByName";
    public static final String SECURITY_USER_BY_Student_ID_QUERY = "SecurityUser.findByStudentId";  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    protected int id;
    
    @Column(name = "username",columnDefinition = "VARCHAR(100)")
    protected String username;
    
    @Column(name = "password_hash",columnDefinition = "VARCHAR(256)")
    protected String pwHash;
    
    @OneToOne(optional = true,cascade = CascadeType.REMOVE)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    protected Student student;
    

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "user_have_role",
        joinColumns = @JoinColumn(referencedColumnName = "user_id", name = "user_id"), 
        inverseJoinColumns = @JoinColumn(referencedColumnName = "role_id", name = "role_id")) 
    protected Set<SecurityRole> roles = new HashSet<SecurityRole>();

    public SecurityUser() {
        super();
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwHash() {
        return pwHash;
    }
    
    public void setPwHash(String pwHash) {
        this.pwHash = pwHash;
    }

    //  Setup custom JSON serializer
    @JsonSerialize(using = SecurityRoleSerializer.class)
    public Set<SecurityRole> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<SecurityRole> roles) {
        this.roles = roles;
    }

    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }

    // Principal
    @Override
    public String getName() {
        return getUsername();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        // Only include member variables that really contribute to an object's identity
        // i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
        // they shouldn't be part of the hashCode calculation
        return prime * result + Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof SecurityUser otherSecurityUser) {
            // See comment (above) in hashCode():  Compare using only member variables that are
            // truly part of an object's identity
            return Objects.equals(this.getId(), otherSecurityUser.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityUser [id = ").append(id).append(", username = ").append(username).append("]");
        return builder.toString();
    }
    
}
