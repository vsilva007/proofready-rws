package net.proofready.rws;

import net.proofready.common.CommonUtilities;
import net.proofready.core.session.NotificationServiceRemote;
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

@Path("/notification/")
public class NotificationRWS {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private RWSFactory factory = null;
	private NotificationServiceRemote notificationService = null;


	public NotificationRWS() {
		factory = RWSFactory.getInstance(); // Get the RWSFactory
	}

	@HEAD()
	@Path("test")
	public Response availabilityTest() {
		return Response.status(Status.OK).header("Message", this.getClass().getName() + " AVAILABLE").build();
	}

	@POST()
	@Path("")
	@Produces(MediaType.TEXT_HTML)
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response sendNotification(@QueryParam("misspellId") long misspellId, @QueryParam("domainId") long domainId,
									 @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("sendNotification(" + misspellId + ", " + domainId + ")");
		// Check variables
		if (!CommonUtilities.isValidLongId(misspellId))
			return Response.status(Status.BAD_REQUEST).header("Message", "misspellId parameter cannot be null").build();
		if (!CommonUtilities.isValidLongId(domainId))
			return Response.status(Status.BAD_REQUEST).header("Message", "domainId parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		notificationService = factory.getNotificationService();
		if (notificationService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			notificationService.sendMisspellNotificationToDomainAdministrator(misspellId,domainId);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@POST()
	@Path("formatTweet")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response formatTweet(@FormParam("url") String url, @FormParam("selected") String selected,
									 @FormParam("domainId") long domainId,
									 @Context SecurityContext securityContext,
									 @Context HttpServletRequest hsreq) {
		System.out.println("formatTweet(" + selected + ", " + url + ", "+ domainId + ")");
		// Check variables
		if (!CommonUtilities.isValidLongId(domainId))
			return Response.status(Status.BAD_REQUEST).header("Message", "domainId parameter cannot be null").build();
		if (selected == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "selected parameter cannot be null").build();
		if (url == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "url parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		notificationService = factory.getNotificationService();
		if (notificationService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			return Response.ok(notificationService.formatTweet(url, selected, domainId)).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
