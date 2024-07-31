/********************************************************************************************************2*4*w*
 * File:  TestACMECollegeSystem.java
 * Course materials CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 *
 * Updated by:  Group 16
 *   041068533, Lei, Luo 
 *   041062508, Yang, Mi 
 *   041066092, Yueying, Li 
 *   041079885, Miao, Yang  
 *   Date modified: 2024-07-24
 */
package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD; 
import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME; 
import static org.hamcrest.CoreMatchers.is; 
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull; 

import java.lang.invoke.MethodHandles;
import java.net.URI; 
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget; 
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
 
import acmecollege.entity.StudentClub;
import acmecollege.entity.AcademicStudentClub; 

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDStudentClub {
	private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
	private static final Logger logger = LogManager.getLogger(_thisClaz);

	static final String HTTP_SCHEMA = "http";
	static final String HOST = "localhost";
	static final int PORT = 8080;


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

	@Test
	public void test01_GET_all_student_clubs_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(adminAuth).path(STUDENT_CLUB_RESOURCE_NAME).request().get();

		assertThat(response.getStatus(), is(200));
	}


	@Test
	public void test02_GET_a_student_club_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(adminAuth).path(STUDENT_CLUB_RESOURCE_NAME + "/2").request().get();
		assertThat(response.getStatus(), is(200)); // successful
		StudentClub club = response.readEntity(StudentClub.class);
		assertNotNull(club);
	}

	@Test
	public void test03_GET_a_student_club_with_USERrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path(STUDENT_CLUB_RESOURCE_NAME + "/1").request().get();
		assertThat(response.getStatus(), is(200)); // successful
		StudentClub club = response.readEntity(StudentClub.class);
		assertNotNull(club);
	}
 

	@Test
	public void test05_POST_new_studentclub_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		AcademicStudentClub newRecord = new AcademicStudentClub();
		newRecord.setName("New AcaClub");

		Response response = webTarget.register(adminAuth).path(STUDENT_CLUB_RESOURCE_NAME).request()
				.post(Entity.entity(newRecord, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(200));// successful
		AcademicStudentClub postReturned = response.readEntity(AcademicStudentClub.class);
		assertThat(postReturned.getName(), is("New AcaClub"));
	}

	@Test
	public void test06_POST_new_studentclub_with_USERrole() throws JsonMappingException, JsonProcessingException {
		AcademicStudentClub newRecord = new AcademicStudentClub();
		newRecord.setName("New AcaClub");

		Response response = webTarget.register(userAuth).path(STUDENT_CLUB_RESOURCE_NAME).request()
				.post(Entity.entity(newRecord, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(403)); // forbidden
	}

	@Test
	public void test07_Update_studentClub_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		int id = 2;
		String clubName = "Computer Science Club";
		AcademicStudentClub studentClubToBeUpdated = new AcademicStudentClub();

		studentClubToBeUpdated.setName(clubName);

		Response response = webTarget.register(adminAuth).path(STUDENT_CLUB_RESOURCE_NAME + "/" + id).request()
				.put(Entity.entity(studentClubToBeUpdated, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(200));

		StudentClub studentClubWithUpdates = response.readEntity(StudentClub.class);

		assertEquals(clubName, studentClubWithUpdates.getName());

	}

	@Test
	public void test08_DELETE_studentclub_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		int lastId = 2;

		Response responseDelete = webTarget.register(adminAuth).path("studentclub" + "/" + lastId).request().delete();

		assertThat(responseDelete.getStatus(), is(200));
	}

	@Test
	public void test09_DELETE_studentclub_with_USERrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path("studentclub" + "/" + 3).request().delete();

		assertThat(response.getStatus(), is(403));
	}

}