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

import static acmecollege.utility.MyConstants.*;
import static org.hamcrest.CoreMatchers.is; 
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.time.LocalDateTime;  
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
import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.DurationAndStatus; 


@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDClubMembership {
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
    
    @Test   
   	public void test01_POST_clubmembership_with_ADMINrole() throws JsonMappingException, JsonProcessingException {

       	int studentClubId = 1;
   	 AcademicStudentClub studentClub = new AcademicStudentClub();
   	 studentClub.setId(studentClubId);
   	 DurationAndStatus durationAndStatus = new DurationAndStatus();
   	 LocalDateTime startDate = LocalDateTime.parse("2024-07-24T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2024-12-31T00:00:00");

        
   	 durationAndStatus.setDurationAndStatus( startDate, endDate, "ACTIVE");
   	 
   	 ClubMembership clubMembershipToAdd = new ClubMembership();
   	 clubMembershipToAdd.setDurationAndStatus(durationAndStatus);
   	 clubMembershipToAdd.setStudentClub(studentClub);
   		Response responseClub = webTarget
   				.register(adminAuth)
   				.path(CLUB_MEMBERSHIP_RESOURCE_NAME )
   				.request()
   				 .post(Entity.entity(clubMembershipToAdd, MediaType.APPLICATION_JSON));
   		
   		assertThat(responseClub.getStatus(), is(200));

   		ClubMembership clubToCheck = responseClub.readEntity(ClubMembership.class);
   		assertThat(clubToCheck, is(notNullValue()));

   		 
   		  
   	    assertThat(clubToCheck.getStudentClub().getId(), is(equalTo(studentClubId)));
   	    assertThat(clubToCheck.getDurationAndStatus().getStartDate(), is(equalTo(startDate)));
   	    assertThat(clubToCheck.getDurationAndStatus().getEndDate(), is(equalTo(endDate)));
   	}
       
       @Test  
   	public void test02_POST_clubmembership_with_USERrole() throws JsonMappingException, JsonProcessingException {

       	int studentClubId = 2;
      	 AcademicStudentClub studentClub = new AcademicStudentClub();
      	 studentClub.setId(studentClubId);
      	 DurationAndStatus durationAndStatus = new DurationAndStatus();
      	 LocalDateTime startDate = LocalDateTime.parse("2024-07-24T00:00:00");
           LocalDateTime endDate = LocalDateTime.parse("2024-12-31T00:00:00");

           
      	 durationAndStatus.setDurationAndStatus( startDate, endDate, "ACTIVE");
      	 
      	 ClubMembership clubMembershipToAdd = new ClubMembership();
      	 clubMembershipToAdd.setDurationAndStatus(durationAndStatus);
      	 clubMembershipToAdd.setStudentClub(studentClub);
      		Response responseClub = webTarget
      				.register(userAuth)
      				.path(CLUB_MEMBERSHIP_RESOURCE_NAME )
      				.request()
      				 .post(Entity.entity(clubMembershipToAdd, MediaType.APPLICATION_JSON));
      		
      		 
   		assertThat(responseClub.getStatus(), is(403));
   	}
    
    @Test
    public void test03_GET_all_clubmembership_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
            .request()
            .get();
        
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void test04_GET_all_clubmembership_with_USERrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); //successful
    }
    
    @Test
    public void test05_GET_specific_clubmembership_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); //successful
    }
    
    @Test
    public void test04_GET_specific_clubmembership_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));  
    }
    
   
    
    @Test
	public void test06_DELETE_clubmembership_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
    	int lastId = 2;
    	Response response1 = webTarget
    				.register(adminAuth)
    				.path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + lastId)
    				.request()
    				.get();
    	assertThat(response1.getStatus(), is(200));
    	
        Response responseDelete = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + lastId)
            .request()
            .delete();
    	assertThat(responseDelete.getStatus(), is(200)); 

        Response response2 = webTarget
                .register(adminAuth)
                .path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + lastId)
                .request()
                .get();
        assertThat(response2.getStatus(), is(500)); 
	}
}
