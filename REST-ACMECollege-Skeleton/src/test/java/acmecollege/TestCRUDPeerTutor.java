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
package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD; 
import static acmecollege.utility.MyConstants.PEER_TUTOR_SUBRESOURCE_NAME; 
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
 

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDPeerTutor {
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
    
    // The next are C-R-U-D test for PeerTutor

    @Test
    public void test01_all_PeerTutors_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<PeerTutor> peerTutors = response.readEntity(new GenericType<List<PeerTutor>>(){});
        assertThat(peerTutors, is(not(empty())));
    }
    
    @Test
    public void test02_all_PeerTutors_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            //.register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));

    }
    
    //GET PeerTutor By ID (admin)
    @Test
    public void test03_get_PeerTutor_byID_with_adminrole() throws JsonMappingException, JsonProcessingException {
        int peerTutor_Id = 1;
    	Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME + "/" + peerTutor_Id)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        
        // Deserialize the response into a PeerTutor object
        PeerTutor peerTutor = response.readEntity(PeerTutor.class);

        // Assert that the peerTutor is not null
        assertThat(peerTutor, is(notNullValue()));
        assertThat(peerTutor.getId(), is(peerTutor_Id)); 

    }
    
    //GET PeerTutor By ID (user) //We change the rule to give the permission to userrole
    @Test
    public void test04_get_PeerTutor_byID_with_userrole() throws JsonMappingException, JsonProcessingException {
        int peerTutor_Id = 1;
    	Response response = webTarget
            .register(userAuth)
            //.register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME + "/" + peerTutor_Id)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));  
        
    }    
    

    // POST New PeerTutor (admin)
    @Test
    public void test05_create_PeerTutor_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	PeerTutor peerTutor = new PeerTutor();
        peerTutor.setFirstName("Charles");
        peerTutor.setLastName("Xavier");
        peerTutor.setProgram("Physics");
        
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME)
            .request()
            .post(Entity.entity(peerTutor, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));
        
        PeerTutor postReturned = response.readEntity(PeerTutor.class);
        assertThat(postReturned.getFirstName(), is("Charles"));
        assertThat(postReturned.getLastName(), is("Xavier"));
        assertThat(postReturned.getProgram(), is("Physics"));

    }
   
    // POST New PeerTutor (user)
    @Test
    public void test06_create_PeerTutor_with_userrole() throws JsonMappingException, JsonProcessingException {
    	PeerTutor peerTutor = new PeerTutor();
        peerTutor.setFirstName("Charles");
        peerTutor.setLastName("Xavier");
        peerTutor.setProgram("Physics");
  
        Response response = webTarget
            .register(userAuth)
            //.register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME)
            .request()
            .post(Entity.entity(peerTutor, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(403));
       
    }   

    // DELETE PeerTutor By ID (admin)
    @Test
    public void test07_delete_PeerTutor_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response responseBefore = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(responseBefore.getStatus(), is(200));
    	
        List<PeerTutor> before = responseBefore.readEntity(new GenericType<List<PeerTutor>>(){});
        assertThat(before, is(not(empty()))); 
        
        PeerTutor deletePeerTutor = new PeerTutor();        
        deletePeerTutor = before.get(before.size() - 1);        
        int deleteLastID = deletePeerTutor.getId();
        deletePeerTutor.getPeerTutorRegistrations().clear();
    	
        Response responseDelete = webTarget
            .register(adminAuth)
            .path(PEER_TUTOR_SUBRESOURCE_NAME + "/" + deleteLastID)
            .request()
            .delete();
        
        assertThat(responseDelete.getStatus(), is(200));//successful
        
        Response responseAfter = webTarget
                .register(adminAuth)
                .path(PEER_TUTOR_SUBRESOURCE_NAME)
                .request()
                .get();
        assertThat(responseBefore.getStatus(), is(200));
        
        List<PeerTutor> after = responseAfter.readEntity(new GenericType<List<PeerTutor>>(){});
        assertThat(after, is(not(empty())));
        
        assertEquals(1, before.size() - after.size());

    }
    
    // DELETE PeerTutor By ID (user)
    @Test
    public void test08_delete_PeerTutor_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(userAuth)
                //.register(adminAuth)
                .path(PEER_TUTOR_SUBRESOURCE_NAME + "/" + 1)
                .request()
                .delete();

        assertThat(response.getStatus(), is(403));
       
    }  
    
    @Test
	public void test09_Update_PeerTutor_with_ADMINrole() throws JsonMappingException, JsonProcessingException {
		int id = 1;
		String firstName = "Abdullah";
		String lastName = "Sabbagh";
		String program= "Physics";
		PeerTutor peerTutorToBeUpdated = new PeerTutor();
		peerTutorToBeUpdated.setFirstName(firstName);
		peerTutorToBeUpdated.setLastName(lastName);
		peerTutorToBeUpdated.setProgram(program);
		Response response = webTarget.register(adminAuth).path(PEER_TUTOR_SUBRESOURCE_NAME + "/" + id).request()
				.put(Entity.entity(peerTutorToBeUpdated, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(200));

		// Deserialize the response into 1 Student object
		PeerTutor peerTutorWithUpdates = response.readEntity(PeerTutor.class);  
		assertEquals(firstName, peerTutorWithUpdates.getFirstName());
		assertEquals(lastName, peerTutorWithUpdates.getLastName());
		assertEquals(program, peerTutorWithUpdates.getProgram());

	}
}

