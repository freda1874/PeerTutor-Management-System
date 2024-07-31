/********************************************************************************************************2*4*w*
 * File:  StudentResource.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 16 
 * 041068533, Lei, Luo 
 * 041062508, Yang, Mi 
 * 041066092, Yueying, Li 
 * 041079885, Miao, Yang  
 * Date modified: 2024-07-24
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.STUDENT_COURSE_PEER_TUTOR_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

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
import acmecollege.entity.PeerTutor;
import acmecollege.entity.PeerTutorRegistration;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

@Path(STUDENT_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected ACMECollegeService service;

	@Inject
	protected SecurityContext sc;

	@GET
	@RolesAllowed({ ADMIN_ROLE })
	public Response getStudents() {
		LOG.debug("retrieving all students ...");

		List<Student> students = service.getAllStudents();
		Response response = Response.ok(students).build();
		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getStudentById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("try to retrieve specific student " + id);
		Response response = null;
		Student student = null;
		if (sc.isCallerInRole(ADMIN_ROLE)) {
			student = service.getStudentById(id);
			response = Response.status(student == null ? Status.NOT_FOUND : Status.OK).entity(student).build();
		} else if (sc.isCallerInRole(USER_ROLE)) {

			WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
			SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();

			student = sUser.getStudent();

			if (student != null && student.getId() == id) {
				response = Response.status(Status.OK).entity(student).build();
			} else {
				throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
			}
		} else {
			response = Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}

	@POST
	@RolesAllowed({ ADMIN_ROLE })
	public Response addStudent(Student newStudent) {
		Response response = null;
		Student newStudentWithIdTimestamps = service.persistStudent(newStudent);
		// Build a SecurityUser linked to the new student
		service.buildUserForNewStudent(newStudentWithIdTimestamps);
		response = Response.ok(newStudentWithIdTimestamps).build();
		return response;
	}

	@PUT
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateStudentInfo(@PathParam(RESOURCE_PATH_ID_ELEMENT) int studentId, Student student) {
		Response response = null;
		Student studentToBeUpdated = service.updateStudentById(studentId, student);
		if (studentToBeUpdated != null) {
			response = Response.ok(studentToBeUpdated).build();
		} else {
			response = Response.status(Status.NOT_FOUND).entity(student).type(MediaType.APPLICATION_JSON).build();
		}
		return response;
	}

	@PUT
	@RolesAllowed({ ADMIN_ROLE })
	@Path(STUDENT_COURSE_PEER_TUTOR_RESOURCE_PATH)
	public Response updatePeerTutorForStudentCourse(@PathParam("studentId") int studentId,
			@PathParam("courseId") int courseId, PeerTutor newPeerTutor) {
		Response response = null;
		PeerTutor peerTutor = service.setPeerTutorForStudentCourse(studentId, courseId, newPeerTutor);
		response = Response.ok(peerTutor).build();
		return response;
	}

	@PUT
	@RolesAllowed({ ADMIN_ROLE })
	@Path("/{studentId}/course/{courseId}/setCourse")
	public Response updateCourserForStudent(@PathParam("studentId") int studentId,
			@PathParam("courseId") int courseId) {
		Response response = null;
		PeerTutorRegistration peerTutorRegistration = service.setCourseForStudent(studentId, courseId);
		if (peerTutorRegistration != null) {
			String message = String.format("Student with id %d updated", studentId);
			response = Response.ok(message).build();
		} else {
			HttpErrorResponse errorResponse = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(),
					"Failed to update");
			response = Response.status(Status.NOT_FOUND).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
		}
		return response;
	}

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deleteStudent(@PathParam(RESOURCE_PATH_ID_ELEMENT) int studentId) {
		LOG.debug("Deleting student with id {}", studentId);
		Response response = null;

		Student studentToBeDeledted = service.deleteStudentById(studentId);

		if (studentToBeDeledted != null) {
			String message = String.format("Student with id %d deleted", studentId);
			response = Response.ok(message).build();
		} else {
			HttpErrorResponse errorResponse = new HttpErrorResponse(Status.NOT_FOUND.getStatusCode(),
					"Student not found");
			response = Response.status(Status.NOT_FOUND).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
		}

		return response;
	}
}