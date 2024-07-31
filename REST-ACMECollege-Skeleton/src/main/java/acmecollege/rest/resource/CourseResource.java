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
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
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
import acmecollege.entity.Course;

import static acmecollege.utility.MyConstants.USER_ROLE;

@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE}) 
    public Response getCourses() {
        LOG.debug("retrieving all courses ...");
        List<Course> courses = service.getAll(Course.class, Course.ALL_COURSES_QUERY_NAME);
        Response response = Response.ok(courses).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE})  
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific course " + id);
        Course course = service.getById(Course.class, Course.COURSE_BY_ID_QUERY_NAME, id);
        Response response = Response.status(course == null ? Status.NOT_FOUND : Status.OK).entity(course).build();
        return response;
    }
    
    
    @POST
    @RolesAllowed({ADMIN_ROLE}) 
    public Response addCourse(Course course) {
    	Response response = null;
        Course newCourse = service.addEntity(course);
        response = Response.status(Response.Status.CREATED)
                       .entity(newCourse)
                       .build();
        return response;
    }
    
	@PUT
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateCourseInfo(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Course courseWithUpdates) {
		Response response = null;
		Course courseToBeUpdated = service.updateCourse(id, courseWithUpdates);
		if (courseToBeUpdated != null) {
			response = Response.ok(courseToBeUpdated).build();
		} else {
			response = Response.status(Status.NOT_FOUND).entity(courseToBeUpdated).type(MediaType.APPLICATION_JSON).build();
		}
		return response;
	}

    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to delete specific course " + id);
        Response response = null;
       Course course= service.deleteCourse(id);
        
        if (course!=null) {
            String message = String.format("Course with id %d successfully deleted", id);
            response =Response.ok(message).build();
        } else {
            String message = String.format("Course with id %d not found", id);
            response = Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }
        
        
        return response;
    }
    
}