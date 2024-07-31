/********************************************************************************************************2*4*w*
 * File:  StudentResource.java Course materials CST 8277
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
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.PEER_TUTOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;
 

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE; 
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
import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.PeerTutor; 

@Path(PEER_TUTOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerTutorResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected ACMECollegeService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getPeerTutors() {
		LOG.debug("retrieving all peer tutors ...");
		List<PeerTutor> peerTutors = service.getAll(PeerTutor.class, PeerTutor.QUERY_PEER_TUTOR_ALL);
		Response response = Response.ok(peerTutors).build();
		return response;
	}

	// all allow any id research
	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getPeerTutorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific  Peer Tutor{}" + id);
		Response response = null;
		PeerTutor peerTutor = null;
		peerTutor = service.getById(PeerTutor.class, PeerTutor.QUERY_PEER_TUTOR_BY_ID, id);
		response = Response.status(peerTutor == null ? Status.NOT_FOUND : Status.OK).entity(peerTutor).build();

		return response;
	}

	@POST
	@RolesAllowed({ ADMIN_ROLE })
	public Response addPeerTutor(PeerTutor peerTutor) {
		Response response = null;
		PeerTutor newPeerTutor = service.addEntity(peerTutor);
		response = Response.status(Response.Status.CREATED).entity(newPeerTutor).build();
		return response;
	}

	 
	
	@PUT
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updatePeerTutor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, PeerTutor peerTutor) {
		Response response = null;
		PeerTutor peerTutorToBeUpdated = service.updatePeerTutor(id, peerTutor);
		if (peerTutorToBeUpdated != null) {
			response = Response.ok(peerTutorToBeUpdated).build();
		} else {
			response = Response.status(Status.NOT_FOUND).entity(peerTutorToBeUpdated).type(MediaType.APPLICATION_JSON).build();
		}
		return response;
	}

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deletePeerTutorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to delete specific peer tutor " + id);
		Response response = null;
		PeerTutor peerTutorToBeDeleted = service.deletePeerTutor(id);
		if (peerTutorToBeDeleted!=null) {
            String message = String.format("Peertutor with id %d successfully deleted", id);
            response =Response.ok(message).build();
        } else {
            String message = String.format("Peertutor with id %d not found", id);
            response = Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }
		return response;
	}

}