package net.proofready.rws;

import net.proofready.common.Constants;
import net.proofready.common.EncryptionUtils;
import net.proofready.common.http.HttpParameterFormatter;
import net.proofready.core.entity.Misspell;
import net.proofready.core.session.DomainServiceRemote;
import net.proofready.core.session.MisspellServiceRemote;
import net.proofready.core.session.NotificationServiceRemote;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Path("/pages/")
public class PageRWS {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private RWSFactory factory = null;
	private NotificationServiceRemote notificationService = null;
	private MisspellServiceRemote misspellService = null;
	private DomainServiceRemote domainService = null;

	public PageRWS() {
		factory = RWSFactory.getInstance(); // Get the RWSFactory
	}

	@HEAD()
	@Path("test")
	public Response availabilityTest() {
		return Response.status(Status.OK).header("Message", this.getClass().getName() + " AVAILABLE").build();
	}

	@GET()
	@Path("approves")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public Response approves(@QueryParam("data") String data, @QueryParam("l") String language,
							 @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("approves("+data+", "+language+")");
		// CHECK VARIABLES:
		if (data == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Data parameter can't be null").build();
		if (language == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "l parameter can't be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		notificationService = factory.getNotificationService();
		misspellService = factory.getMisspellService();
		if (notificationService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		if (misspellService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Map<String, String> parameters = HttpParameterFormatter.extract(EncryptionUtils.decryptDESede(data));
			Misspell misspell = misspellService.getMisspell(Long.valueOf(parameters.get("misspellId")).longValue());
			if (misspell.acceptAssessment()) {
				misspellService.updateMisspellStatusAndUserStats(misspell.getId(), Constants.MISSPELL_STATUS_APPROVED);
				return Response.ok(notificationService.generateApprovedPage(misspell.getUserId(), language)).build();
			} else {
				return Response.ok(notificationService.generateAlreadyApprovedOrDisapprovedPage(language)).build();
			}
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@GET()
	@Path("disapproves")
	@Produces(MediaType.TEXT_HTML)
	@PermitAll
	public Response disapproves(@QueryParam("data") String data, @QueryParam("l") String language,
							 @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("disapproves("+data+", "+language+")");
		// CHECK VARIABLES:
		if (data == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Data parameter can't be null").build();
		if (language == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "l parameter can't be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		notificationService = factory.getNotificationService();
		misspellService = factory.getMisspellService();
		domainService = factory.getDomainService();
		if (notificationService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		if (misspellService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		if (domainService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Map<String, String> parameters = HttpParameterFormatter.extract(EncryptionUtils.decryptDESede(data));
			Misspell misspell = misspellService.getMisspell(Long.valueOf(parameters.get("misspellId")).longValue());
			if (misspell.acceptAssessment()) {
				misspellService.updateMisspellStatusAndUserStats(misspell.getId(), Constants.MISSPELL_STATUS_DISAPPROVED);
				domainService.increaseCreditForDomainId(misspell.getDomainId());
				return Response.ok(notificationService.generateDisapprovedPage(misspell.getId(), language)).build();
			}else {
				return Response.ok(notificationService.generateAlreadyApprovedOrDisapprovedPage(language)).build();
			}
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
