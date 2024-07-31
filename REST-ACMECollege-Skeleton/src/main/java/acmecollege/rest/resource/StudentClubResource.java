/********************************************************************************************************2*4*w*
 * File:  StudentClubResource.java Course materials CST 8277
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
 * 
 */
package acmecollege.rest.resource;
 
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
import acmecollege.entity.StudentClub;
import acmecollege.entity.ClubMembership;

import static acmecollege.utility.MyConstants.*;

@Path(STUDENT_CLUB_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentClubResource {
    
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
	@GET
	@RolesAllowed({ADMIN_ROLE,USER_ROLE}) 
    public Response getStudentClubs() {
        LOG.debug("Retrieving all student clubs...");
        List<StudentClub> studentClubs = service.getAllStudentClubs();
        LOG.debug("Student clubs found = {}", studentClubs);
        Response response = Response.ok(studentClubs).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getStudentClubById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int studentClubId) {
        LOG.debug("Retrieving student club with id = {}", studentClubId);
        StudentClub studentClub = service.getStudentClubById(studentClubId);
        Response response = Response.ok(studentClub).build();
        return response;
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteStudentClub(@PathParam(RESOURCE_PATH_ID_ELEMENT) int scId) {
        LOG.debug("Deleting student club with id = {}", scId);
        StudentClub sc = service.deleteStudentClub(scId);
        Response response=null;
        if (sc!=null) {
            String message = String.format("Student Club with id %d successfully deleted", scId);
            response =Response.ok(message).build();
        } else {
            String message = String.format("Student Club with id %d not found", scId);
            response = Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }
		 
        return response;
    }
    

    @RolesAllowed({ADMIN_ROLE})
    @POST
    public Response addStudentClub(StudentClub newStudentClub) {
        LOG.debug("Adding a new student club = {}", newStudentClub);
        if (service.isDuplicated(newStudentClub)) {
            HttpErrorResponse err = new HttpErrorResponse(Status.CONFLICT.getStatusCode(), "Entity already exists");
            return Response.status(Status.CONFLICT).entity(err).build();
        }
        else {
            StudentClub tempStudentClub = service.persistStudentClub(newStudentClub);
            return Response.ok(tempStudentClub).build();
        }
    }

    @RolesAllowed({ADMIN_ROLE})
    @POST
    @Path("/{studentClubId}/clubmembership")
    public Response addClubMembershipToStudentClub(@PathParam("studentClubId") int scId, ClubMembership newClubMembership) {
        LOG.debug( "Adding a new ClubMembership to student club with id = {}", scId);
        
        StudentClub sc = service.getStudentClubById(scId);
        newClubMembership.setStudentClub(sc);
        sc.getClubMemberships().add(newClubMembership);
        service.updateStudentClub(scId, sc);
        
        return Response.ok(sc).build();
    }

    @RolesAllowed({ADMIN_ROLE})
    @PUT
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateStudentClub(@PathParam(RESOURCE_PATH_ID_ELEMENT) int scId, StudentClub updatingStudentClub) {
        LOG.debug("Updating a specific student club with id = {}", scId);
        Response response = null;
        StudentClub updatedStudentClub = service.updateStudentClub(scId, updatingStudentClub);
		if (updatedStudentClub != null) {
			response = Response.ok(updatedStudentClub).build();
		} else {
			response = Response.status(Status.NOT_FOUND).entity(updatedStudentClub).type(MediaType.APPLICATION_JSON).build();
		}
		return response; 
    }
    
  
    
}
