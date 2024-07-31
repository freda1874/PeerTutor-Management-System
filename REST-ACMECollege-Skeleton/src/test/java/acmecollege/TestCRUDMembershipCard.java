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
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Student;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDMembershipCard {
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

	@Test
	@Order(11)
	public void test01_create_membershipCard_with_adminrole() throws JsonMappingException, JsonProcessingException {

		MembershipCard cardTobeAdded = new MembershipCard();

		int new_studentID = 1;
		int new_clubMembershipID = 3;
		
		Student student = new Student();

		student.setId(new_studentID);
		ClubMembership clubMembership = new ClubMembership();
		clubMembership.setId(new_clubMembershipID);

		cardTobeAdded.setOwner(student);
		cardTobeAdded.setClubMembership(clubMembership);

		Response response = webTarget.register(adminAuth).path(MEMBERSHIP_CARD_RESOURCE_NAME)
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(cardTobeAdded, MediaType.APPLICATION_JSON));

		logger.debug("Response Status: " + response.getStatus());

		assertThat(response.getStatus(), is(200));

		MembershipCard card = response.readEntity(MembershipCard.class);
		logger.debug("Response Status: " + card.toString());

		assertThat(new_clubMembershipID, is(equalTo(card.getClubMembership().getId())));
		assertThat(new_studentID, is(equalTo(card.getOwner().getId())));

	}

	// POST New MembershipCard (user)
	@Test
	@Order(12)
	public void test02_create_membershipCard_with_userrole() throws JsonMappingException, JsonProcessingException {
		MembershipCard cardTobeAdded = new MembershipCard();

		int new_studentID = 1;
		int new_clubMembershipID = 2;
		Student student = new Student();

		student.setId(new_studentID);
		ClubMembership clubMembership = new ClubMembership();
		clubMembership.setId(new_clubMembershipID);

		cardTobeAdded.setOwner(student);
		cardTobeAdded.setClubMembership(clubMembership);

		Response response = webTarget.register(userAuth).path(MEMBERSHIP_CARD_RESOURCE_NAME)
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(cardTobeAdded, MediaType.APPLICATION_JSON));

		logger.debug("Response Status: " + response.getStatus());

		assertThat(response.getStatus(), is(403));

	}

	@Test
	@Order(13)
	public void test03_all_membershipCards_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget
				// .register(userAuth)
				.register(adminAuth).path(MEMBERSHIP_CARD_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(200));
		List<MembershipCard> membershipCards = response.readEntity(new GenericType<List<MembershipCard>>() {
		});
		assertThat(membershipCards, is(not(empty())));
	}

	@Test
	@Order(14)
	public void test04_all_membershipCards_with_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth)
				// .register(adminAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(403));

	}

	// GET MembershipCard By ID (admin)
	@Test
	@Order(15)
	public void test05_get_membershipCard_byID_with_adminrole() throws JsonMappingException, JsonProcessingException {
		int membershipCard_Id = 1;
		Response response = webTarget.register(adminAuth).path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + membershipCard_Id)
				.request().get();
		assertThat(response.getStatus(), is(200));

		MembershipCard card = response.readEntity(MembershipCard.class);

		assertThat(card.getId(), is(membershipCard_Id));

	}

	@Test
	@Order(16)
	public void test06_get_membershipCard_byID_with_userrole() throws JsonMappingException, JsonProcessingException {
		int membershipCard_Id = 1;
		Response response = webTarget.register(userAuth)
				// .register(adminAuth)
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + membershipCard_Id).request().get();
		assertThat(response.getStatus(), is(200)); 

	}

	// DELETE MembershipCard By ID (admin)
	@Test
	@Order(17)
	public void test07_delete_membershipCard_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response responseBefore = webTarget 
				.register(adminAuth).path(MEMBERSHIP_CARD_RESOURCE_NAME).request().get();
		assertThat(responseBefore.getStatus(), is(200));

		List<MembershipCard> before = responseBefore.readEntity(new GenericType<List<MembershipCard>>() {
		});
		assertThat(before, is(not(empty())));

		MembershipCard deleteMembershipCard = new MembershipCard();
		deleteMembershipCard = before.get(before.size() - 1);
		int deleteLastID = deleteMembershipCard.getId();

		Response responseDelete = webTarget.register(adminAuth).path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + deleteLastID)
				.request().delete();

		assertThat(responseDelete.getStatus(), is(200));// successful

		Response responseAfter = webTarget.register(adminAuth).path(MEMBERSHIP_CARD_RESOURCE_NAME).request().get();

		List<MembershipCard> after = responseAfter.readEntity(new GenericType<List<MembershipCard>>() {
		});
		assertThat(after, is(not(empty())));

		assertEquals(1, before.size() - after.size());

	}

	// DELETE MembershipCard By ID (user)
	@Test
	@Order(18)
	public void test08_delete_membershipCard_with_userrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(userAuth) 
				.path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + 1).request().delete();

		assertThat(response.getStatus(), is(403));

	}

	// DELETE MembershipCard By ID (admin)
	@Test
	@Order(19)
	public void test09_update_membershipCard_with_adminrole() throws JsonMappingException, JsonProcessingException {
		int id = 1;
		MembershipCard cardTobeAdded = new MembershipCard();

		cardTobeAdded.setSigned(true);

		Response response = webTarget.register(adminAuth).path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + id)
				.request(MediaType.APPLICATION_JSON).put(Entity.entity(cardTobeAdded, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(200));

		MembershipCard card = response.readEntity(MembershipCard.class);

		assertThat((byte) 1, is(equalTo(card.getSigned())));
		assertThat(id, is(equalTo(card.getId())));

	}
}
