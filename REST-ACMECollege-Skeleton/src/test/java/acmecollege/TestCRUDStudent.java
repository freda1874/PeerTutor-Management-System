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
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME; 
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull; 
import static org.hamcrest.Matchers.notNullValue;

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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException; 
import acmecollege.entity.Student; 
 

//@TestMethodOrder(MethodOrderer.MethodName.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCRUDStudent {
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
	@Order(1)
	public void test01_POST_new_student_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		Student newStudent = new Student();
		newStudent.setFirstName("Abdullah");
		newStudent.setLastName("Sabbagh");

		Response response = webTarget.register(adminAuth).path(STUDENT_RESOURCE_NAME).request()
				.post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(200));// successful
		Student student = response.readEntity(Student.class);
		assertThat(student.getFirstName(), is("Abdullah"));
		assertThat(student.getLastName(), is("Sabbagh"));
	}

	@Test
	@Order(2)
	public void test02_POST_new_student_with_USERrole() throws JsonMappingException, JsonProcessingException {
		Student newStudent = new Student();
		newStudent.setFirstName("Abdullah");
		newStudent.setLastName("Sabbagh");

		Response response = webTarget.register(userAuth).path(STUDENT_RESOURCE_NAME).request()
				.post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(403));
	}

	@Test
	@Order(3)
	public void test03_GET_all_students_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(STUDENT_RESOURCE_NAME).request().get();

		assertThat(response.getStatus(), is(200));
		List<Student> students = response.readEntity(new GenericType<List<Student>>() {
		});
		assertThat(students, is(not(empty())));
	}

	@Test
	@Order(4)
	public void test04_GET_all_students_with_USERrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path(STUDENT_RESOURCE_NAME).request().get();

		assertThat(response.getStatus(), is(403));
	}

	@Test
	@Order(5)
	public void test05_GET_student_by_ID_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		int student_Id = 1;
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(STUDENT_RESOURCE_NAME + "/" + student_Id).request().get();
		assertThat(response.getStatus(), is(200));

		Student student = response.readEntity(Student.class);

		assertThat(student, is(notNullValue()));

		assertThat(student.getId(), is(student_Id));
	}

	@Test
	 @Order(6)
	public void test06_GET_student_by_ID_with_USERrole() throws JsonMappingException, JsonProcessingException {
		int student_Id = 1;
		Response response = webTarget.register(userAuth)
//            .register(adminAuth)
				.path(STUDENT_RESOURCE_NAME + "/" + student_Id).request().get();
		assertThat(response.getStatus(), is(200));

		// Deserialize the response into 1 Student object
		Student student = response.readEntity(Student.class);

		// Assert that the student is not null
		assertThat(student, is(notNullValue()));

		// Assert the student's first name and last name
		assertThat(student.getId(), is(student_Id));
	}

	@Test
	 @Order(7)
	public void test07_Extra_GET_unauthorized_user_student_info_with_USERrole()
			throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path(STUDENT_RESOURCE_NAME + "/2").request().get();
		assertThat(response.getStatus(), is(403)); // unauthorized
		Student student = response.readEntity(Student.class);
		assertNotNull(student);
	}

	@Test
	 @Order(8)
	public void test08_DELETE_student_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		Response responseAll = webTarget.register(adminAuth).path(STUDENT_RESOURCE_NAME).request().get();
		assertThat(responseAll.getStatus(), is(200));
		List<Student> students = responseAll.readEntity(new GenericType<List<Student>>() {
		});
		assertThat(students, is(not(empty())));
		int originalSize = students.size();

		int lastId = students.get(students.size() - 1).getId();

		Response responseDelete = webTarget.register(adminAuth).path(STUDENT_RESOURCE_NAME + "/" + lastId).request()
				.delete();

		Response responseAll2 = webTarget.register(adminAuth).path(STUDENT_RESOURCE_NAME).request().get();

		assertThat(responseAll2.getStatus(), is(200));// successful

		List<Student> adStudents = responseAll2.readEntity(new GenericType<List<Student>>() {
		});
		assertThat(students, is(not(empty())));

		assertEquals(1, originalSize - adStudents.size());
	}

	@Test
	 @Order(9)
	public void test09_Update_student_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		int student_Id = 1;
		String firstName = "Jason";
		String lastName = "Dany";
		Student studentToBeUpdated = new Student();
		studentToBeUpdated.setFirstName(firstName);
		studentToBeUpdated.setLastName(lastName);
		Response response = webTarget.register(adminAuth).path(STUDENT_RESOURCE_NAME + "/" + student_Id).request()
				.put(Entity.entity(studentToBeUpdated, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(200));

		// Deserialize the response into 1 Student object
		Student studentWithUpdates = response.readEntity(Student.class);
		assertEquals(firstName, studentWithUpdates.getFirstName());
		assertEquals(lastName, studentWithUpdates.getLastName());

	}

	@Test
	 @Order(10)
	public void test10_DELETE_student_with_USERrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth).path(STUDENT_RESOURCE_NAME + "/" + 1).request().delete();
		assertThat(response.getStatus(), is(403));
	}

}