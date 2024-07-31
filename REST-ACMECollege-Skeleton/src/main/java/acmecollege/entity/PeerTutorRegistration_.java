package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2024-07-24T16:44:43.562-0400")
@StaticMetamodel(PeerTutorRegistration.class)
public class PeerTutorRegistration_ extends PojoBaseCompositeKey_ {
	public static volatile SingularAttribute<PeerTutorRegistration, PeerTutorRegistrationPK> id;
	public static volatile SingularAttribute<PeerTutorRegistration, Student> student;
	public static volatile SingularAttribute<PeerTutorRegistration, Course> course;
	public static volatile SingularAttribute<PeerTutorRegistration, PeerTutor> peerTutor;
	public static volatile SingularAttribute<PeerTutorRegistration, Integer> numericGrade;
	public static volatile SingularAttribute<PeerTutorRegistration, String> letterGrade;
}
