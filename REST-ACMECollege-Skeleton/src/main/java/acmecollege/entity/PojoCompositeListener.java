/********************************************************************************************************2*4*w*
 * File:  PojoCompositeListener.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
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
import static java.time.LocalDateTime.now; 
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

 
public class PojoCompositeListener {

	@PrePersist
	public void setCreatedOnDate(PojoBaseCompositeKey<?> pojoBaseComposite) {
		 
		pojoBaseComposite.setCreated(now());
		pojoBaseComposite.setUpdated(now());
	}

	@PreUpdate
	public void setUpdatedDate(PojoBaseCompositeKey<?> pojoBaseComposite) {
		pojoBaseComposite.setUpdated(now());
	}

}
