package net.proofready.rws;

import net.proofready.common.CommonUtilities;
import net.proofready.core.entity.UserStatistic;
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

@Path("/userStatistic/")
public class UserStatisticRWS {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private RWSFactory factory = null;
	private UserServiceRemote userService = null;


	public UserStatisticRWS() {
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
	public Response addUserStatistic(UserStatistic userStatistic, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("addUserStatistic()");
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			UserStatistic userStatisticAdded = userService.addUserStatistic(userStatistic);
			return Response.ok(userStatisticAdded).build();
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
	public Response updateUserStatistic(UserStatistic userStatistic, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("updateUserStatistic("+userStatistic.toString()+")");
		// Check variables
		if (userStatistic == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "UserStatistic parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			userService.updateUserStatistic(userStatistic);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@GET()
	@Path("{userStatistic_id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response getUserStatistic(@PathParam("userStatistic_id") long userStatisticId, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("getUserStatistic("+userStatisticId+")");
		// Check variables
		if (!CommonUtilities.isValidLongId(userStatisticId))
			return Response.status(Status.BAD_REQUEST).header("Message", "userStatisticId parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			return Response.ok(userService.getUserStatistic(userStatisticId)).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@DELETE()
	@Path("{userStatistic_id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response deleteUserStatistic(@PathParam("userStatistic_id") long userStatisticId, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("deleteUserStatistic("+userStatisticId+")");
		// Check variables
		if (!CommonUtilities.isValidLongId(userStatisticId))
			return Response.status(Status.BAD_REQUEST).header("Message", "userStatisticId parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		try {
			//LOGIC
			userService.deleteUserStatisticById(userStatisticId);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
