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
import static acmecollege.utility.MyConstants.PEER_TUTOR_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
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
import acmecollege.entity.PeerTutorRegistration;

@Path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerTutorRegistrationResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected ACMECollegeService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getPeerTutorRegistrations() {
		LOG.debug("retrieving all registrations ...");
		List<PeerTutorRegistration> peerTutorRegistrations = service.getAll(PeerTutorRegistration.class,
				PeerTutorRegistration.ALL_REGISTRATIONS_QUERY_NAME);
		Response response = Response.ok(peerTutorRegistrations).build();
		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path("/{studentId}/course/{courseId}")
	public Response getPeerTutorRegistrationById(@PathParam("studentId") int studentId,
			@PathParam("courseId") int courseId) {
		LOG.debug("try to retrieve specific registration " + "Student:" + studentId + "Course:" + courseId);
		PeerTutorRegistration peerTutorRegistration = service.getPeerTutorRegistrationById(courseId, studentId);
		Response response = Response.status(peerTutorRegistration == null ? Status.NOT_FOUND : Status.OK)
				.entity(peerTutorRegistration).build();
		return response;
	}

	@POST
	@RolesAllowed({ ADMIN_ROLE })
	@Path("/student/{studentId}/course/{courseId}/peertutor/{peertutorId}")
	public Response associateCourseAndPeerTutorToStudent(@PathParam("studentId") int studentId,
			@PathParam("courseId") int courseId, @PathParam("peertutorId") int peertutorId,PeerTutorRegistration ptr) {

		LOG.debug("Associating PeerTutor or Course to Student ...");
	     LOG.debug("Received studentId={}, courseId={}, peerTutorId={}", studentId, courseId, peertutorId);
		try {
			PeerTutorRegistration peerTutorRegistration = service.associateCourseAndPeerTutorToStudent(studentId,
					courseId, peertutorId,ptr);
			return Response.ok(peerTutorRegistration).build();
		} catch (EntityNotFoundException e) {
			LOG.warn("Entity not found: {}", e.getMessage());
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}

	}

	@PUT
	@RolesAllowed({ ADMIN_ROLE })
	@Path("/student/{studentId}/course/{courseId}/peertutor")
	public Response updateRegistration(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId,
			PeerTutor peerTutor) { 
		LOG.debug("Updating PeerTutor or Course to Student ...");

		PeerTutorRegistration peerTutorRegistration = service.updateRegistrationInfo(studentId, courseId, peerTutor);
		Response response = null;
		 if (peerTutorRegistration != null) {
		        response = Response.ok(peerTutorRegistration).build();
		    } else {
		        response = Response.status(Response.Status.NOT_FOUND).entity("PeerTutorRegistration not found").build();
		    }
		    return response;
		}

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path("/{studentId}/course/{courseId}")
	public Response deletePeerTutorRegistration(@PathParam("studentId") int studentId,
			@PathParam("courseId") int courseId) {

		LOG.debug("Deleting PeerTutorRegistration for studentId {} and courseId {}...", studentId, courseId);
		PeerTutorRegistration ptr = service.deletePeerTutorRegistration(studentId, courseId);

		if (ptr != null) {
			String message = "Delete successful";
			return Response.ok(message).build();
		} else {
			String message = String.format("PeerTutorRegistration with studentId %d and courseId %d not found",
					studentId, courseId);
			return Response.status(Response.Status.NOT_FOUND).entity(message).build();
		}
	}

}