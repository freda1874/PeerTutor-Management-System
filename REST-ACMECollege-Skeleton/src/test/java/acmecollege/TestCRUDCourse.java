/********************************************************************************************************2*4*w*
 * File:  Student.java Course materials CST 8277
 * 
 * Updated by:  Group 16
 *   041068533, Lei, Luo 
 *   041062508, Yang, Mi 
 *   041066092, Yueying, Li 
 *   041079885, Miao, Yang  
 *   Date modified: 2024-07-24
 * 
 *   
 */


package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME; 
 
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

 
import acmecollege.entity.Course; 

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDCourse {
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
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
        
    }
    
    // The next are C-R-D tests for Course

    @Test
    public void test01_all_courses_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
        assertThat(courses, hasSize(2));
    }
    
    @Test
    public void test02_all_courses_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            //.register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));

    }
    
    //GET Course By ID (admin)
    @Test
    public void test03_get_course_byID_with_adminrole() throws JsonMappingException, JsonProcessingException {
        int course_Id = 1;
    	Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME + "/" + course_Id)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        
        // Deserialize the response into a Course object
        Course course = response.readEntity(Course.class);

        // Assert that the Course is not null
        assertThat(course, is(notNullValue()));

        // Assert the Course's code and title
        assertThat(course.getCourseCode(), is("CST8277")); 
        assertThat(course.getCourseTitle(), is("Enterprise Application Programming"));  

    }
    
    //GET Course By ID (user)
    @Test
    public void test04_get_course_byID_with_userrole() throws JsonMappingException, JsonProcessingException {
        int course_Id = 1;
    	Response response = webTarget
            .register(userAuth)
            //.register(adminAuth)
            .path(COURSE_RESOURCE_NAME + "/" + course_Id)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        
    }    
    

    // POST New Course (admin)
    @Test
    public void test05_create_course_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Course newCourse = new Course();
    	newCourse.setCourse("CST8102", "Computer Essentials", 2022, "WINTER", 3,  (byte) 0);
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));
        
        Course postReturned = response.readEntity(Course.class);
        assertThat(postReturned.getCourseCode(), is("CST8102"));
        assertThat(postReturned.getCourseTitle(), is("Computer Essentials"));
        assertThat(postReturned.getYear(), is(2022));
        assertThat(postReturned.getSemester(), is("WINTER"));
        assertThat(postReturned.getCreditUnits(), is(3));
        assertThat(postReturned.getOnline(), is((byte) 0));

    }
    
    // POST New Course (user)
    @Test
    public void test06_create_course_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Course newCourse = new Course();
    	newCourse.setCourse("CST8101", "Computer Essentials", 2022, "WINTER", 3,  (byte) 0);
        Response response = webTarget
            .register(userAuth)
            //.register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(403));
       
    }   

    // DELETE Course By ID (admin)
    @Test
    public void test07_delete_course_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response responseBefore = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(responseBefore.getStatus(), is(200));
    	
        List<Course> before = responseBefore.readEntity(new GenericType<List<Course>>(){});
        assertThat(before, is(not(empty()))); 
        
        
        Course deleteCourse = new Course();       
        deleteCourse = before.get(before.size() - 1);       
        int deleteLastID = deleteCourse.getId();                
    	
        Response responseDelete = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME + "/" + deleteLastID)
            .request()
            .delete();
        
        assertThat(responseDelete.getStatus(), is(200));//successful
        
        Response responseAfter = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .get();
        
        List<Course> after = responseAfter.readEntity(new GenericType<List<Course>>(){});
        assertThat(after, is(not(empty())));
        
        assertEquals(1, before.size() - after.size());

    }
    
    // DELETE Course By ID (user)
    @Test
    public void test08_delete_course_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                //.register(adminAuth)
                .path(COURSE_RESOURCE_NAME + "/" + 1)
                .request()
                .delete();

        assertThat(response.getStatus(), is(403));
       
    }  
    
    @Test
	public void test09_Update_Course_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		int id = 1;
		String courseCode = "CST8102";
		String courseTitle = "Project Management";
		int year = 2024;
		String semester = "WINTER";
		byte online = 0;
		
		Course courseToBeUpdated = new Course();
		courseToBeUpdated.setCourse(courseCode, courseTitle, year, semester, year, online);
		 
		Response response = webTarget.register(adminAuth).path(COURSE_RESOURCE_NAME + "/" + id).request()
				.put(Entity.entity(courseToBeUpdated, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(200));

 
		Course courseWithUpdates = response.readEntity(Course.class);  
		assertEquals(id, courseWithUpdates.getId());
		assertEquals(courseCode, courseWithUpdates.getCourseCode());
		assertEquals(courseTitle, courseWithUpdates.getCourseTitle());
		assertEquals(year, courseWithUpdates.getYear());
		assertEquals(semester, courseWithUpdates.getSemester());
		assertEquals(online, courseWithUpdates.getOnline());

	}
}
