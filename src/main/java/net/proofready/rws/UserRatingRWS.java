package net.proofready.rws;

import net.proofready.common.CommonUtilities;
import net.proofready.common.EncryptionUtils;
import net.proofready.common.http.HttpParameterFormatter;
import net.proofready.common.template.VelocityUtils;
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
import java.util.Map;

@Path("/userRating/")
public class UserRatingRWS {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private RWSFactory factory = null;
	private UserServiceRemote userService = null;


	public UserRatingRWS() {
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
	public Response addUserRating(UserRating userRating, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("addUserRating()");
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			UserRating userRatingAdded = userService.addUserRating(userRating);
			return Response.ok(userRatingAdded).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@POST()
	@Path("rateUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response rateUser(@FormParam("data") String data, @FormParam("rating") byte value,
							 @FormParam("l") String language,
							 @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("rateUser("+data+","+value+")");
		// Check variables
		if (data == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Data parameter can't be null").build();
		if (language == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "l parameter can't be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Map<String, String> parameters = HttpParameterFormatter.extract(EncryptionUtils.decryptDESede(data));
			UserRating userRating = new UserRating();
			userRating.setUserId(Long.valueOf(parameters.get("userId")).longValue());
			userRating.setRatingValue(value);
			userService.addUserRating(userRating);
			return Response.ok(VelocityUtils.formatApprovedOKPage(language)).build();
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
	public Response updateUserRating(UserRating userRating, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("updateUserRating("+userRating.toString()+")");
		// Check variables
		if (userRating == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "UserRating parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			userService.updateUserRating(userRating);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@GET()
	@Path("{userRating_id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response getUserRating(@PathParam("userRating_id") long userRatingId, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("getUserRating("+userRatingId+")");
		// Check variables
		if (!CommonUtilities.isValidLongId(userRatingId))
			return Response.status(Status.BAD_REQUEST).header("Message", "userRatingId parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			return Response.ok(userService.getUserRating(userRatingId)).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@DELETE()
	@Path("{userRating_id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response deleteUserRating(@PathParam("userRating_id") long userRatingId, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("deleteUserRating("+userRatingId+")");
		// Check variables
		if (!CommonUtilities.isValidLongId(userRatingId))
			return Response.status(Status.BAD_REQUEST).header("Message", "userRatingId parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		userService = factory.getUserService();
		if (userService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		try {
			//LOGIC
			userService.deleteUserRatingById(userRatingId);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
