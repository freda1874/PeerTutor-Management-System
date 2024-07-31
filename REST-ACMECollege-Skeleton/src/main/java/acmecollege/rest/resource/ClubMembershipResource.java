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
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
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
import acmecollege.entity.ClubMembership;
import acmecollege.entity.DurationAndStatus;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.SecurityUser; 

@Path(CLUB_MEMBERSHIP_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource {
	private static final Logger LOG = LogManager.getLogger();
	@EJB
	protected ACMECollegeService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE,USER_ROLE })
	public Response getAllClubMembership() {
		LOG.debug("retrieving all Club Membership ...");
		List<ClubMembership> cm = service.getAll(ClubMembership.class, ClubMembership.FIND_ALL);
		Response response = Response.ok(cm).build();
		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getClubMembershipById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific Club Membership " + id);
		Response response = null;
		ClubMembership clubMembership = null;
		
		if (sc.isCallerInRole(ADMIN_ROLE)) {
			clubMembership = service.getById(ClubMembership.class, ClubMembership.FIND_BY_ID, id);
			response = Response.status(clubMembership == null ? Status.NOT_FOUND : Status.OK).entity(clubMembership)
					.build();
		} else if (sc.isCallerInRole(USER_ROLE)) {
			WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
			SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
			Set<MembershipCard> cards = sUser.getStudent().getMembershipCards();
			if (cards != null) {
				for (MembershipCard c : cards) {
					if (c.getClubMembership().getId() == id) {
						response = Response.status(Status.OK).entity(c.getClubMembership()).build();
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

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deleteClubMembership(@PathParam(RESOURCE_PATH_ID_ELEMENT) int cmId) {
		LOG.debug("Deleting Club Membership with id = {}", cmId);
		ClubMembership cm = service.deleteClubMembership(cmId);
		Response response=null;
		if (cm!=null) {
            String message = String.format("Club embership with id %d successfully deleted", cmId);
            response =Response.ok(message).build();
        } else {
            String message = String.format("Club embership with id %d not found", cmId);
            response = Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }
		 
		  response = Response.ok(cm).build();
		return response;
	}

	@RolesAllowed({ ADMIN_ROLE })
	@POST
	public Response addClubMembership(ClubMembership newCM) {
		LOG.debug("Adding a Club Membership = {}", newCM);
		ClubMembership tempCM = service.persistClubMembership(newCM);
		if(tempCM == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Referenced Student Club not found").build();
		}
		return Response.ok(tempCM).build();
	}

	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@PUT
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateClubMembershipDuration(@PathParam(RESOURCE_PATH_ID_ELEMENT) int cmId,
			DurationAndStatus updatingDurationAndStatus) {
		LOG.debug("Updating a specific Club Membership with id = {}", cmId);
		Response response = null;
		ClubMembership updatedClubMembership = service.updateClubMembershipDuration(cmId, updatingDurationAndStatus);
		 
		if (updatedClubMembership != null) {
			response = Response.ok(updatedClubMembership).build();
		} else {
			response = Response.status(Status.NOT_FOUND).entity(updatedClubMembership).type(MediaType.APPLICATION_JSON).build();
		}
		return response;
	}

}
