/********************************************************************************************************2*4*w*
 * File:  ACMEColegeService.java
 * Course materials CST 8277
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
 */
package acmecollege.ejb;

//import static acmecollege.entity.StudentClub.ALL_STUDENT_CLUBS_QUERY_NAME;
import static acmecollege.entity.StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_ID;
import static acmecollege.entity.StudentClub.IS_DUPLICATE_QUERY_NAME;
//import static acmecollege.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static acmecollege.entity.ClubMembership.FIND_BY_ID;
import static acmecollege.entity.MembershipCard.ID_CARD_QUERY_NAME;
import static acmecollege.entity.SecurityRole.FIND_ROLE_BY_NAME;
import static acmecollege.entity.SecurityUser.SECURITY_USER_BY_ID_QUERY;
import static acmecollege.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PREFIX;
import static acmecollege.utility.MyConstants.PARAM1;
import static acmecollege.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmecollege.utility.MyConstants.PROPERTY_SALT_SIZE;
import static acmecollege.utility.MyConstants.PU_NAME;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.DurationAndStatus;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.PeerTutor;
import acmecollege.entity.PeerTutorRegistration;
import acmecollege.entity.PeerTutorRegistrationPK;
import acmecollege.entity.SecurityRole;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService
 */
@Singleton
public class ACMECollegeService implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger();

	@PersistenceContext(name = PU_NAME)
	protected EntityManager em;

	@Inject
	protected Pbkdf2PasswordHash pbAndjPasswordHash;

	// student resources
	public List<Student> getAllStudents() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Student> cq = cb.createQuery(Student.class);
		cq.select(cq.from(Student.class));
		return em.createQuery(cq).getResultList();
	}

	public Student getStudentById(int id) {
		return em.find(Student.class, id);
	}

	@Transactional
	public Student persistStudent(Student newStudent) {
		em.persist(newStudent);
		return newStudent;
	}

	@Transactional
	public void buildUserForNewStudent(Student newStudent) {
		SecurityUser userForNewStudent = new SecurityUser();
		userForNewStudent
				.setUsername(DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName());
		Map<String, String> pbAndjProperties = new HashMap<>();
		pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
		pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
		pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
		pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
		pbAndjPasswordHash.initialize(pbAndjProperties);
		String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
		userForNewStudent.setPwHash(pwHash);
		userForNewStudent.setStudent(newStudent);
		// Use NamedQuery on SecurityRole to find USER_ROLE
		TypedQuery<SecurityRole> query = em.createNamedQuery(SecurityRole.FIND_ROLE_BY_NAME, SecurityRole.class);
		query.setParameter("param1", USER_ROLE);
		SecurityRole userRole = query.getSingleResult();
		userForNewStudent.getRoles().add(userRole);
		userRole.getUsers().add(userForNewStudent);
		em.persist(userForNewStudent);
	}

	/**
	 * To update a student
	 * 
	 * @param id                 - id of entity to update
	 * @param studentWithUpdates - entity with updated information
	 * @return Entity with updated information
	 */
	@Transactional
	public Student updateStudentById(int id, Student studentWithUpdates) {
		Student studentToBeUpdated = em.find(Student.class, id);
		if (studentToBeUpdated != null) {
			studentToBeUpdated.setFullName(studentWithUpdates.getFirstName(), studentWithUpdates.getLastName());
			em.merge(studentWithUpdates);
		} else {
			throw new EntityNotFoundException("Student with ID " + id + " not found");
		}
		return studentToBeUpdated;
	}

	/**
	 * To delete a student by id
	 * 
	 * @param id - student id to delete
	 */
	@Transactional
	public Student deleteStudentById(int id) {
		Student student = em.find(Student.class, id);
		if (student != null) {
			em.refresh(student);
			TypedQuery<SecurityUser> findUser = em.createNamedQuery(SecurityUser.SECURITY_USER_BY_Student_ID_QUERY,
					SecurityUser.class);
			findUser.setParameter("param1", id);
			SecurityUser sUser = findUser.getSingleResult();
			if (sUser != null) {
				em.remove(sUser);
			}
			em.remove(student);
			return student;
		}
		return null;
	}

	@Transactional
	public PeerTutor setPeerTutorForStudentCourse(int studentId, int courseId, PeerTutor newPeerTutor) {
		Student studentToBeUpdated = em.find(Student.class, studentId);

		if (studentToBeUpdated != null) { // Student exists
			Set<PeerTutorRegistration> peerTutorRegistrations = studentToBeUpdated.getPeerTutorRegistrations();
			peerTutorRegistrations.forEach(ptr -> {
				if (ptr.getCourse().getId() == courseId) {
					if (ptr.getPeerTutor() != null) {
						PeerTutor peer = em.find(PeerTutor.class, ptr.getPeerTutor().getId());
						peer.setPeerTutor(newPeerTutor.getFirstName(), newPeerTutor.getLastName(),
								newPeerTutor.getProgram());
						em.merge(peer);
					} else {

						ptr.setPeerTutor(newPeerTutor);
						em.merge(studentToBeUpdated);
					}
				}
			});
			return newPeerTutor;
		} else
			return null; // Student doesn't exists
	}

	@Transactional
	public PeerTutorRegistration setCourseForStudent(int studentId, int courseId) {
		Student studentToBeUpdated = em.find(Student.class, studentId);
		Course courseTobeAssociated = getById(Course.class, Course.COURSE_BY_ID_QUERY_NAME, courseId);
		if (studentToBeUpdated != null && courseTobeAssociated != null) {
			PeerTutorRegistration peerTutorRegistration = getPeerTutorRegistrationById(courseId, studentId);
			if (peerTutorRegistration == null) {
				PeerTutorRegistration ptr = new PeerTutorRegistration();
				ptr.setStudent(studentToBeUpdated);
				ptr.setCourse(courseTobeAssociated);
				em.persist(ptr);
				return ptr;
			} else {
				em.merge(peerTutorRegistration);
				em.flush();
				return peerTutorRegistration;
			}

		}

		return null;
	}

	// PeerTutor

	@Transactional
	public PeerTutor updatePeerTutor(int id, PeerTutor pt) {
		PeerTutor ptToBeUpdated = em.find(PeerTutor.class, id);
		if (ptToBeUpdated != null) {
			em.refresh(ptToBeUpdated);
			ptToBeUpdated.setFirstName(pt.getFirstName());
			ptToBeUpdated.setLastName(pt.getLastName());
			ptToBeUpdated.setProgram(pt.getProgram());
			em.merge(ptToBeUpdated);
			em.flush();
		}
		return ptToBeUpdated;
	}

	@Transactional
	public PeerTutor deletePeerTutor(int id) {
		PeerTutor peerTutor = getById(PeerTutor.class, PeerTutor.QUERY_PEER_TUTOR_BY_ID, id);
		if (peerTutor != null) {
			Set<PeerTutorRegistration> ptrs = peerTutor.getPeerTutorRegistrations();
			for (PeerTutorRegistration ptr : ptrs) {
				if (ptr.getPeerTutor().getId() == id) {

					ptr.setPeerTutor(null);
					em.merge(ptr);

				}
				peerTutor.setPeerTutorRegistrations(null);
			}
			em.remove(peerTutor);
			return peerTutor;
		}
		return null;
	}

	public boolean isPeerTutorExisted(PeerTutor peerTutor) {
		TypedQuery<Long> peerTutorsCount = em.createNamedQuery(PeerTutor.IS_DUPLICATE_QUERY_NAME, Long.class);
		peerTutorsCount.setParameter("param1", peerTutor.getFirstName());
		peerTutorsCount.setParameter("param2", peerTutor.getLastName());
		peerTutorsCount.setParameter("param3", peerTutor.getProgram());

		return (peerTutorsCount.getSingleResult() >= 1);
	}

	public PeerTutor getPeerTutorByName(PeerTutor peerTutor) {
		TypedQuery<PeerTutor> query = em.createNamedQuery(PeerTutor.QUERY_PEER_TUTOR_BY_NAME_PROGRAM, PeerTutor.class);
		query.setParameter("param1", peerTutor.getFirstName());
		query.setParameter("param2", peerTutor.getLastName());
		query.setParameter("param3", peerTutor.getProgram());

		return (query.getSingleResult());
	}

	// PeerTutorRegistration resources

	public boolean isPeerTutorRegistrationDuplicated(int courseId, int studentId) {
		TypedQuery<Long> allPTRQuery = em.createNamedQuery(PeerTutorRegistration.IS_DUPLICATE_QUERY, Long.class);
		allPTRQuery.setParameter("param1", studentId);
		allPTRQuery.setParameter("param2", courseId);

		return (allPTRQuery.getSingleResult() >= 1);
	}

	@Transactional
	public PeerTutorRegistration associateCourseAndPeerTutorToStudent(int studentId, int courseId, int peerTutorId,PeerTutorRegistration ptr) {
		LOG.info("Associating PeerTutor and Course to Student: studentId={}, courseId={}, peerTutorId={}", studentId,
				courseId, peerTutorId);

		Student student = em.find(Student.class, studentId);
		if (student == null) {
			throw new EntityNotFoundException("Student with ID " + studentId + " not found");
		}

		Course course = em.find(Course.class, courseId);
		if (course == null) {
			throw new EntityNotFoundException("Course with ID " + courseId + " not found");
		}

		PeerTutor peerTutor = em.find(PeerTutor.class, peerTutorId);
		if (peerTutor == null) {
			throw new EntityNotFoundException("Peer Tutor with ID " + peerTutorId + " not found");
		}

		PeerTutorRegistration registrationToAdd = new PeerTutorRegistration();
		if (isPeerTutorRegistrationDuplicated(courseId, studentId)) {
			throw new EntityExistsException("PeerTutor already registered");
		} else {
			registrationToAdd.setCourse(course);
			registrationToAdd.setStudent(student);
			registrationToAdd.setPeerTutor(peerTutor);
			registrationToAdd.setNumericGrade(ptr.getNumericGrade());
			registrationToAdd.setLetterGrade(ptr.getLetterGrade());
			
			em.persist(registrationToAdd);

		}
		return registrationToAdd;
		
	}

	@Transactional
	public PeerTutorRegistration deletePeerTutorRegistration(int studentId, int courseId) {

		LOG.info("Associating PeerTutor and Course to Student: studentId={}, courseId={}, peerTutorId={}", studentId,
				courseId);

		PeerTutorRegistrationPK pk = new PeerTutorRegistrationPK();

		Student student = em.find(Student.class, studentId);
		if (student == null) {
			throw new EntityNotFoundException("Student with ID " + studentId + " not found");
		} else {
			pk.setStudentId(studentId);
		}

		Course course = em.find(Course.class, courseId);
		if (course == null) {
			throw new EntityNotFoundException("course with ID " + courseId + " not found");
		} else {
			pk.setCourseId(courseId);
		}

		PeerTutorRegistration registration = em.find(PeerTutorRegistration.class, pk);
		if (registration != null) {
			em.remove(registration);
		}
		return registration;
	}

	public PeerTutorRegistration getPeerTutorRegistrationById(int courseId, int studentId) {
		TypedQuery<PeerTutorRegistration> query = em
				.createNamedQuery(PeerTutorRegistration.REGISTRATION_BY_ID_QUERY_NAME, PeerTutorRegistration.class);
		query.setParameter("param1", studentId);
		query.setParameter("param2", courseId);
		System.out.println("Course ID: " + courseId + ", Student ID: " + studentId);
		return query.getSingleResult();
	}

	@Transactional
	public PeerTutorRegistration persistPeerTutorRegistration(PeerTutorRegistration peerTutorRegistration) {
		Course course = getById(Course.class, Course.COURSE_BY_ID_QUERY_NAME,
				peerTutorRegistration.getCourse().getId());
		Student student = getStudentById(peerTutorRegistration.getStudent().getId());
		if (course != null) {
			peerTutorRegistration.setCourse(course);
		}
		if (student != null) {
			peerTutorRegistration.setStudent(student);
		}

		em.persist(peerTutorRegistration);
		return peerTutorRegistration;
	}

	@Transactional
	public PeerTutorRegistration updateRegistrationInfo(int studentId, int courseId, PeerTutor peerTutor) {

		PeerTutorRegistrationPK pk = new PeerTutorRegistrationPK();
		pk.setCourseId(courseId);
		pk.setStudentId(studentId);
		PeerTutorRegistration peerTutorRegistration = em.find(PeerTutorRegistration.class, pk);

		if (peerTutorRegistration != null) {
			if (isPeerTutorExisted(peerTutor)) {
				PeerTutor peerTutorExisted = getPeerTutorByName(peerTutor);
				em.persist(peerTutorExisted);
				peerTutorRegistration.setPeerTutor(peerTutorExisted);
				em.merge(peerTutorRegistration);
			} else {
				PeerTutor peerTutorToBeUpdated = new PeerTutor();
				peerTutorToBeUpdated.setPeerTutor(peerTutor.getFirstName(), peerTutor.getLastName(),
						peerTutor.getProgram());
				em.persist(peerTutorToBeUpdated);
				peerTutorRegistration.setPeerTutor(peerTutorToBeUpdated);
				em.merge(peerTutorRegistration);
			}
		}
		return peerTutorRegistration;
	}

	// course

	@Transactional
	public Course deleteCourse(int id) {
		Course course = getById(Course.class, Course.COURSE_BY_ID_QUERY_NAME, id);
		if (course != null) {
			em.remove(course);
			return course;
		}
		return null;
	}

	@Transactional
	public Course updateCourse(int id, Course courseWithUpdates) {
		Course courseToBeUpdated = em.find(Course.class, id);
		if (courseToBeUpdated != null) {
			em.refresh(courseToBeUpdated);
			courseToBeUpdated.setCourseCode(courseWithUpdates.getCourseCode());
			courseToBeUpdated.setCourseTitle(courseWithUpdates.getCourseTitle());
			courseToBeUpdated.setYear(courseWithUpdates.getYear());
			courseToBeUpdated.setSemester(courseWithUpdates.getSemester());

			em.merge(courseToBeUpdated);
			em.flush();
		}
		return courseToBeUpdated;
	}

	// student club
	public List<StudentClub> getAllStudentClubs() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StudentClub> cq = cb.createQuery(StudentClub.class);
		cq.select(cq.from(StudentClub.class));
		return em.createQuery(cq).getResultList();
	}

	// Why not use the build-in em.find? The named query
	// SPECIFIC_STUDENT_CLUB_QUERY_NAME
	// includes JOIN FETCH that we cannot add to the above API
	public StudentClub getStudentClubById(int id) {
		TypedQuery<StudentClub> specificStudentClubQuery = em.createNamedQuery(SPECIFIC_STUDENT_CLUB_QUERY_ID,
				StudentClub.class);
		specificStudentClubQuery.setParameter(PARAM1, id);
		return specificStudentClubQuery.getSingleResult();
	}

	@Transactional
	public StudentClub deleteStudentClub(int id) {
		StudentClub sc = getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_ID, id);
		if (sc != null) {
			Set<ClubMembership> memberships = sc.getClubMemberships();
			List<ClubMembership> list = new LinkedList<>();
			memberships.forEach(list::add);
			list.forEach(m -> {
				if (m.getCard() != null) {
					MembershipCard mc = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME,
							m.getCard().getId());
					mc.setClubMembership(null);
				}
				m.setCard(null);
				em.merge(m);
			});
			em.remove(sc);
			return sc;
		}
		return null;
	}

	public boolean isDuplicated(StudentClub newStudentClub) {
		TypedQuery<Long> allStudentClubsQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
		allStudentClubsQuery.setParameter(PARAM1, newStudentClub.getName());
		return (allStudentClubsQuery.getSingleResult() >= 1);
	}

	@Transactional
	public StudentClub persistStudentClub(StudentClub newStudentClub) {
		em.persist(newStudentClub);
		return newStudentClub;
	}

	@Transactional
	public StudentClub updateStudentClub(int id, StudentClub updatingStudentClub) {
		StudentClub studentClubToBeUpdated = em.find(StudentClub.class, id);
		if (studentClubToBeUpdated != null) {
			em.refresh(studentClubToBeUpdated);
			studentClubToBeUpdated.setName(updatingStudentClub.getName());
			em.merge(studentClubToBeUpdated);
			em.flush();
		}
		return studentClubToBeUpdated;
	}

	// club membership
	@Transactional
	public ClubMembership persistClubMembership(ClubMembership newClubMembership) {
		StudentClub sc = getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_ID,
				newClubMembership.getStudentClub().getId());
		if (sc != null) {
			newClubMembership.setStudentClub(sc);
		}

		em.persist(newClubMembership);
		return newClubMembership;
	}

	public ClubMembership getClubMembershipById(int cmId) {
		TypedQuery<ClubMembership> allClubMembershipQuery = em.createNamedQuery(ClubMembership.FIND_BY_ID,
				ClubMembership.class);
		allClubMembershipQuery.setParameter(PARAM1, cmId);
		return allClubMembershipQuery.getSingleResult();
	}

	@Transactional
	public ClubMembership updateClubMembershipDuration(int id, DurationAndStatus durationAndStatusWithUpdates) {
		ClubMembership clubMembershipToBeUpdated = em.find(ClubMembership.class, id);
		if (clubMembershipToBeUpdated != null) {
			DurationAndStatus durationAndStatus = clubMembershipToBeUpdated.getDurationAndStatus();
			durationAndStatus.setStartDate(durationAndStatusWithUpdates.getStartDate());
			durationAndStatus.setEndDate(durationAndStatusWithUpdates.getEndDate());
			durationAndStatus.setActive(durationAndStatusWithUpdates.getActive());
			clubMembershipToBeUpdated.setDurationAndStatus(durationAndStatus);

			em.merge(clubMembershipToBeUpdated);
			em.flush();
		}
		return clubMembershipToBeUpdated;
	}

	@Transactional
	public ClubMembership deleteClubMembership(int id) {
		ClubMembership cm = em.find(ClubMembership.class, id);
		if (cm != null) {
			if (cm.getCard() != null) {
				MembershipCard membershipCard = cm.getCard();

				em.remove(membershipCard);

			}

			em.remove(cm);

		}
		return cm;
	}

	// membership card

	@Transactional
	public MembershipCard persistMembershipCard(MembershipCard newCard) {

		LOG.debug(newCard.toString());
		Student student = em.find(Student.class, newCard.getOwner().getId());

		ClubMembership clubMembership = em.find(ClubMembership.class, newCard.getClubMembership().getId());

		MembershipCard membershipCardToBeUpdated = new MembershipCard();
		if (student != null && clubMembership != null) {
			membershipCardToBeUpdated.setOwner(student);
			membershipCardToBeUpdated.setClubMembership(clubMembership);
			membershipCardToBeUpdated.setSigned(false);
			em.persist(membershipCardToBeUpdated);
		}

		return membershipCardToBeUpdated;
	}

	public MembershipCard getMembershipCardById(int id) {
		TypedQuery<MembershipCard> specificQuery = em.createNamedQuery(MembershipCard.ID_CARD_QUERY_NAME,
				MembershipCard.class);
		specificQuery.setParameter(PARAM1, id);
		return specificQuery.getSingleResult();
	}

	@Transactional
	public MembershipCard deleteMembershipCard(int id) {
		MembershipCard mc = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, id);
		if (mc != null) {

			em.remove(mc);
			return mc;
		}
		return null;
	}

	@Transactional
	public MembershipCard updateMembershipCard(int mcId, MembershipCard newCard) {
		MembershipCard cardToBeUpdated = em.find(MembershipCard.class, mcId);
		byte signed = newCard.getSigned();

		if (cardToBeUpdated != null) {
			Boolean signBoolean = true;
			if (signed == 0) {
				signBoolean = false;
			}
			cardToBeUpdated.setSigned(signBoolean);
			em.merge(cardToBeUpdated);
			em.flush();

		}
		return cardToBeUpdated;
	}

	// These methods are more generic.

	@Transactional
	public <T> T addEntity(T entity) {
		em.persist(entity);
		return entity;
	}

	public <T> List<T> getAll(Class<T> entity, String namedQuery) {
		TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
		return allQuery.getResultList();
	}

	public <T> T getById(Class<T> entity, String namedQuery, int id) {
		TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
		allQuery.setParameter(PARAM1, id);
		return allQuery.getSingleResult();
	}

}