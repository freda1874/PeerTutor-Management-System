/********************************************************************************************************2*4*w*
 * File:  StudentClubResource.java Course materials CST 8277
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
package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT; 
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD; 
import static acmecollege.utility.MyConstants.PEER_TUTOR_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME; 
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.notNullValue; 
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
 
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException; 
import acmecollege.entity.PeerTutor;
import acmecollege.entity.PeerTutorRegistration;
import acmecollege.entity.Student; 

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDPeerTutorRegistration {
	private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
	private static final Logger logger = LogManager.getLogger(_thisClaz);

	static final String HTTP_SCHEMA = "http";
	static final String HOST = "localhost";
	static final int PORT = 8080;

	// Test fixture(s)
	static URI uri;
	static HttpAuthenticationFeature adminAuth;
	static HttpAuthenticationFeature userAuth;

	@BeforeAll
	public static void oneTimeSetUp() throws Exception {
		logger.debug("oneTimeSetUp");
		uri = UriBuilder.fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION).scheme(HTTP_SCHEMA).host(HOST)
				.port(PORT).build();
		adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
		userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
	}

	protected WebTarget webTarget;

	@BeforeEach
	public void setUp() {
		Client client = ClientBuilder
				.newClient(new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
		webTarget = client.target(uri);

	}

	// DELETE PeerTutorRegistration By ID (admin)
		@Test
		public void test01_delete_peerTutorRegistration_with_adminrole()
				throws JsonMappingException, JsonProcessingException {

			Response responseBefore = webTarget
					// .register(userAuth)
					.register(adminAuth).path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME).request().get();
			assertThat(responseBefore.getStatus(), is(200));

			List<PeerTutorRegistration> before = responseBefore.readEntity(new GenericType<List<PeerTutorRegistration>>() {
			});
			assertThat(before, is(not(empty())));
			int rowCountBefore = before.size();

			PeerTutorRegistration deletePeerTutorRegistration = new PeerTutorRegistration();
			deletePeerTutorRegistration = before.get(before.size() - 1);
			int studentId = deletePeerTutorRegistration.getId().getStudentId();
			int courseId = deletePeerTutorRegistration.getId().getCourseId();

			Response responseDelete = webTarget.register(adminAuth)
					.path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME + "/" + studentId + "/course/" + courseId).request()
					.delete();

			assertThat(responseDelete.getStatus(), is(200));// successful

			Response responseAfter = webTarget.register(adminAuth).path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME).request()
					.get();

			assertThat(responseDelete.getStatus(), is(200));// successful

			List<PeerTutorRegistration> after = responseAfter.readEntity(new GenericType<List<PeerTutorRegistration>>() {
			});
			// assertThat(after, is(not(empty())));

			assertEquals(before.size() - after.size(), 1);

			// cleanup the new student in test 5 to keep the DB as Initial status
			// If the Lisa Frost in student, delete it
			// get the students in DB
			Response response_studentAll = webTarget.register(adminAuth).path(STUDENT_RESOURCE_NAME).request().get();
			assertThat(response_studentAll.getStatus(), is(200));
			List<Student> studentsAll = response_studentAll.readEntity(new GenericType<List<Student>>() {
			});
			// assertThat(studentsAll, is(notNullValue()));
			if (studentsAll != null) {
				Student lisaFrost = new Student();
				for (Student student : studentsAll) {
					if (student.getFirstName() == "Lisa" && student.getLastName() == "Frost") {
						lisaFrost = student;
					}
				}

				if (lisaFrost != null) {
					Response responseCleanup = webTarget.register(adminAuth)
							.path(STUDENT_RESOURCE_NAME + "/" + lisaFrost.getId()).request().delete();
					// assertThat(responseCleanup.getStatus(), is(200));
				}
			}
		}

		// DELETE PeerTutorRegistration By ID (user)
		@Test
		public void test02_delete_peerTutorRegistration_with_userrole()
				throws JsonMappingException, JsonProcessingException {
			Response response = webTarget.register(userAuth)
					// .register(adminAuth)
					.path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME + "/" + 1 + "/course/" + 1).request().delete();

			assertThat(response.getStatus(), is(403));

		}

		// POST New PeerTutorRegistration (admin)
		@Test
		public void test03_create_peerTutorRegistration_with_adminrole()
				throws JsonMappingException, JsonProcessingException {

			int studentId = 1;
			int courseId = 2; 

			int numericGrade = 90;
			String letterGrade = "A";

			PeerTutorRegistration peerTutorRegistration = new PeerTutorRegistration();
			peerTutorRegistration.setLetterGrade(letterGrade);
			peerTutorRegistration.setNumericGrade(numericGrade);

			Response response = webTarget.register(adminAuth)
					.path("peertutorregistration/student/" + studentId + "/course/" + courseId + "/peertutor/1").request()
					.post(Entity.entity(peerTutorRegistration, MediaType.APPLICATION_JSON));

			assertThat(response.getStatus(), is(200)); // created successfully

			PeerTutorRegistration peerTutorRegistrationCreated = response.readEntity(PeerTutorRegistration.class);
			assertThat(peerTutorRegistrationCreated, is(notNullValue()));

			assertThat(peerTutorRegistrationCreated.getId().getStudentId(), is(studentId));
			assertThat(peerTutorRegistrationCreated.getId().getCourseId(), is(courseId));

		}

		// POST New PeerTutorRegistration (user)
		@Test
		public void test04_create_peerTutorRegistration_with_Userrole()
				throws JsonMappingException, JsonProcessingException {

			int studentId = 1;
			int courseId = 2; 

			int numericGrade = 90;
			String letterGrade = "A";

			PeerTutorRegistration peerTutorRegistration = new PeerTutorRegistration();
			peerTutorRegistration.setLetterGrade(letterGrade);
			peerTutorRegistration.setNumericGrade(numericGrade);

			Response response = webTarget.register(userAuth)
					.path("peertutorregistration/student/" + studentId + "/course/" + courseId + "/peertutor/1").request()
					.post(Entity.entity(peerTutorRegistration, MediaType.APPLICATION_JSON));

			assertThat(response.getStatus(), is(403)); // forbidden

		}
	@Test
	public void test05_all_peerTutorRegistrations_with_adminrole()
			throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200));
		List<PeerTutorRegistration> peerTutorRegistrations = response
				.readEntity(new GenericType<List<PeerTutorRegistration>>() {
				});
		assertThat(peerTutorRegistrations, is(not(empty())));
	}

	@Test
	public void test06_all_peerTutorRegistrations_with_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth)
				// .register(adminAuth)
				.path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(403));

	}

	// GET PeerTutorRegistration By ID (admin)
	@Test
	public void test07_get_peerTutorRegistration_byID_with_adminrole()
			throws JsonMappingException, JsonProcessingException {
		int studentId = 1;
		int courseId = 1;
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth)
				.path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME + "/" + studentId + "/course/" + courseId).request().get();
		assertThat(response.getStatus(), is(200));

		// Deserialize the response into a PeerTutorRegistration object
		PeerTutorRegistration peerTutorRegistration = response.readEntity(PeerTutorRegistration.class);

		// Assert that the PeerTutorRegistration is not null
		assertThat(peerTutorRegistration, is(notNullValue()));

		// Assert the PeerTutorRegistration's code and title
		assertThat(peerTutorRegistration.getId().getStudentId(), is(studentId));
		assertThat(peerTutorRegistration.getId().getCourseId(), is(courseId));

	}

	// GET PeerTutorRegistration By ID (user)
	// user has the permission to read.
	@Test
	public void test08_get_peerTutorRegistration_byID_with_userrole()
			throws JsonMappingException, JsonProcessingException {
		int studentId = 1;
		int courseId = 1;
		Response response = webTarget.register(userAuth)
				// .register(adminAuth)
				.path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME + "/" + studentId + "/course/" + courseId).request().get();
		assertThat(response.getStatus(), is(200));

		PeerTutorRegistration peerTutorRegistration = response.readEntity(PeerTutorRegistration.class);

		// Assert that the PeerTutorRegistration is not null
		assertThat(peerTutorRegistration, is(notNullValue()));

		// Assert the PeerTutorRegistration's code and title
		assertThat(peerTutorRegistration.getId().getStudentId(), is(1));
		assertThat(peerTutorRegistration.getId().getCourseId(), is(1));

	}
 
	

	@Test
	public void test09_update_peerTutorRegistration_with_adminrole()
			throws JsonMappingException, JsonProcessingException {
		String firstName = "Jason";
		String lastName = "Xavier";
		String program = "Physics";
		PeerTutor peerTutor = new PeerTutor();
		peerTutor.setPeerTutor(firstName, lastName, program);

		Response response = webTarget.register(adminAuth)
				.path("peertutorregistration/student/" + 1 + "/course/" + 1 + "/peertutor").request()
				.put(Entity.entity(peerTutor, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(200));

		PeerTutorRegistration ptr = response.readEntity(PeerTutorRegistration.class);

		assertThat(ptr.getPeerTutor(), is(notNullValue()));

		assertThat(ptr.getPeerTutor().getFirstName(), is(firstName));
		assertThat(ptr.getPeerTutor().getLastName(), is(lastName));
		assertThat(ptr.getPeerTutor().getProgram(), is(program));

	}
}
