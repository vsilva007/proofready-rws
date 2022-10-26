package net.proofready.rws;

import net.proofready.common.EncryptionUtils;
import net.proofready.common.exception.InstanceNotFoundException;
import net.proofready.common.http.HttpParameterFormatter;
import net.proofready.common.template.VelocityUtils;
import net.proofready.core.entity.Domain;
import net.proofready.core.session.DomainServiceRemote;
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
import java.util.Map;

@Path("/domain/")
public class DomainRWS {
	private static final Log _log = LogFactory.getLog(DomainRWS.class);
	private RWSFactory factory = null;
	private DomainServiceRemote domainService = null;
	private NotificationServiceRemote notificationService = null;


	public DomainRWS() {
		factory = RWSFactory.getInstance(); // Get the RWSFactory
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
	public Response addDomain(Domain domain, @Context SecurityContext securityContext) {
		_log.info("addDomain()");
		// Check variables
		if (domain == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Domain parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		domainService = factory.getDomainService();
		if (domainService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			domainService.addDomain(domain);
			return Response.ok().build();
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
	public Response updateDomain(Domain domain, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("updateDomain("+domain.toString()+")");
		// Check variables
		if (domain == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Domain parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		domainService = factory.getDomainService();
		if (domainService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			domainService.updateDomain(domain);
			return Response.ok().build();
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
	public Response getDomainByURL(@QueryParam("url") String url, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("getDomainByURL("+url+")");
		// Check variables
		if (url == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "URL parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		domainService = factory.getDomainService();
		if (domainService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		try {
			//LOGIC
			Domain domain = domainService.getDomainByUrl(url);
			return Response.ok(domain).build();
		} catch (InstanceNotFoundException ex){
			return Response.status(Status.NO_CONTENT).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@DELETE()
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response deleteDomainByURL(@QueryParam("url") String url, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("deleteDomainByURL("+url+")");
		// Check variables
		if (url == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "URL parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		domainService = factory.getDomainService();
		if (domainService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		try {
			//LOGIC
			domainService.deleteDomainWithUrl(url);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@GET()
	@Path("active")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response isDomainActiveWithUrl(@QueryParam("url") String url, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("isDomainActiveWithUrl("+url+")");
		// Check variables
		if (url == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "URL parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		domainService = factory.getDomainService();
		if (domainService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		try {
			//LOGIC
			if (domainService.isDomainActiveWithUrl(url))
				return Response.ok().build();
			else
				return Response.status(Status.NO_CONTENT).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@GET()
	@Path("unsubscribe")
	@Produces(MediaType.TEXT_HTML)
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response unsubscribeDomain(@QueryParam("data") String data, @QueryParam("l") String language,
									  @Context SecurityContext securityContext,
									  @Context HttpServletRequest hsreq) {
		_log.info("unsubscribeDomain("+data+", "+language+")");
		// CHECK VARIABLES:
		if (data == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Data parameter can't be null").build();
		if (language == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "l parameter can't be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		domainService = factory.getDomainService();
		notificationService = factory.getNotificationService();
		if (domainService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		if (notificationService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		try {
			//LOGIC
			Map<String, String> parameters = HttpParameterFormatter.extract(EncryptionUtils.decryptDESede(data));
			domainService.unsubscribeDomain(Long.valueOf(parameters.get("domainId")).longValue(), parameters.get("mail"));
			return Response.ok(VelocityUtils.formatUnsubscribeOKPage(language)).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
