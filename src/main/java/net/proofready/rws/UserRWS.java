package net.proofready.rws;

import net.proofready.common.CommonUtilities;
import net.proofready.common.Constants;
import net.proofready.common.exception.InstanceNotFoundException;
import net.proofready.core.entity.User;
import net.proofready.core.entity.UserRating;
import net.proofready.core.session.UserServiceRemote;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

@Path("/user/")
public class UserRWS {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private RWSFactory factory = null;
	private UserServiceRemote userService = null;


	public UserRWS() {
		factory = RWSFactory.getInstance(); // Get the WSFactory
	}

	@HEAD()
	@Path("test")
	public Response availabilityTest() {
		return Response.status(Status.OK).header("Message", this.getClass().getName() + " AVAILABLE").build();
	}

	@POST()
	@Path("")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response addUser(User user, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("addUser()");
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			User userAdded = userService.addUser(user);
			userService.addUserRating(new UserRating(user.getId(), Constants.USER_RATING_INITIAL_VALUE));
			return Response.ok(userAdded).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@POST()
	@Path("create")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response addUser(@FormParam("email") String email, @FormParam("id") String id,
							@Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("addUser("+email+", "+id+")");
		// Check variables
		if (email == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "email parameter cannot be null").build();
		if (id == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "id parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			User user= userService.addUser(new User(email, id));
			userService.addUserRating(new UserRating(user.getId(), Constants.USER_RATING_INITIAL_VALUE));
			return Response.ok(user).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@POST()
	@Path("update")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response updateUser(User user, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("updateUser("+user.toString()+")");
		// Check variables
		if (user == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "User parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			userService.updateUser(user);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@GET()
	@Path("{user_id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response getUser(@PathParam("user_id") long userId, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("getUser("+userId+")");
		// Check variables
		if (!CommonUtilities.isValidLongId(userId))
			return Response.status(Status.BAD_REQUEST).header("Message", "userId parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			User user = userService.getUser(userId);
			return Response.ok(user).build();
		} catch (InstanceNotFoundException ex) {
			return Response.status(Status.NO_CONTENT).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@GET()
	@Path("")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response getUserWithMail(@QueryParam("mail") String userMail, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("getUserWithMail("+userMail+")");
		// Check variables
		if (userMail == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "userMail parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			User user = userService.getUserWithMail(userMail);
			return Response.ok(user).build();
		} catch (InstanceNotFoundException ex) {
			return Response.status(Status.NO_CONTENT).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@DELETE()
	@Path("{user_id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response deleteUser(@PathParam("user_id") long userId, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("deleteUser("+userId+")");
		// Check variables
		if (!CommonUtilities.isValidLongId(userId))
			return Response.status(Status.BAD_REQUEST).header("Message", "userId parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		try {
			//LOGIC
			userService.deleteUserById(userId);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
