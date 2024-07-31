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

package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal; 

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.SecurityUser; 

@Path(MEMBERSHIP_CARD_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MembershipCardResource {
	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected ACMECollegeService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getAllMembershipCard() {
		LOG.debug("retrieving all Membership Cards ...");
		List<MembershipCard> cards = service.getAll(MembershipCard.class, MembershipCard.CARD_QUERY_NAME);
		Response response = Response.ok(cards).build();
		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getMembershipCardById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific Membership Card " + id);
		Response response = null;
		MembershipCard card = null;

		if (sc.isCallerInRole(ADMIN_ROLE)) {
			card = service.getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, id);
			response = Response.status(card == null ? Status.NOT_FOUND : Status.OK).entity(card).build();
		} else if (sc.isCallerInRole(USER_ROLE)) {
			WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
			SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
			Set<MembershipCard> cards = sUser.getStudent().getMembershipCards();
			if (cards != null) {
				for (MembershipCard c : cards) {
					if (c.getId() == id) {
						response = Response.status(Status.OK).entity(c).build();
					}
				}
			} else {
				throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
			}
		} else {
			response = Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}

	@RolesAllowed({ ADMIN_ROLE })
	@POST
	public Response addMembershipCard(MembershipCard card ) {
		 
		MembershipCard tempCard = service.persistMembershipCard(card);
		if(tempCard==null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("can't persist").build();
		} 
	 
		return Response.ok(tempCard).build();
	}

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path("/{membershipCardId}")
	public Response deleteMembershipCard(@PathParam("membershipCardId") int cardId) {
		LOG.debug("Deleting Membership Card with id = {}", cardId);
		MembershipCard mc = service.deleteMembershipCard(cardId);
		Response response = null;
	 
		
		if (mc!=null) {
            String message = String.format("Membership Card with id %d successfully deleted", cardId);
            response =Response.ok(message).build();
        } else {
            String message = String.format("Membership Card with id %d not found", cardId);
            response = Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }
		 
		 
		return response;
		
		
	}

	@RolesAllowed({ ADMIN_ROLE})
	@PUT
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateMembershipCard(
			@PathParam(RESOURCE_PATH_ID_ELEMENT) int mcId,
			MembershipCard newCard) {
		LOG.debug("Updating a specific Membership Card with id = {}", mcId);
		Response response =null;
		MembershipCard updatedMembershipCard = service.updateMembershipCard(mcId, newCard);
		if(updatedMembershipCard != null) {
		response = Response.ok(updatedMembershipCard).build();}
		else { Response.status(Response.Status.NOT_FOUND).build();}
		return response;
	}

}
