package net.proofready.rws;

import net.proofready.common.CommonUtilities;
import net.proofready.common.EncryptionUtils;
import net.proofready.common.exception.InstanceNotFoundException;
import net.proofready.common.http.HttpParameterFormatter;
import net.proofready.common.template.VelocityUtils;
import net.proofready.core.entity.Misspell;
import net.proofready.core.session.MisspellServiceRemote;
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

@Path("/misspell/")
public class MisspellRWS {
	private static final Log _log = LogFactory.getLog(MisspellRWS.class);
	private RWSFactory factory = null;
	private MisspellServiceRemote misspellService = null;


	public MisspellRWS() {
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
	public Response addMisspell(Misspell misspell, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("addMisspell()");
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		misspellService = factory.getMisspellService();
		if (misspellService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Misspell misspellAdded = misspellService.addMisspell(misspell);
			return Response.ok(misspellAdded).build();
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
	public Response addMisspell(@FormParam("url") String url, @FormParam("report") String report,
								@FormParam("selected") String selected,	@FormParam("suggestion") String suggestion,
								@FormParam("domainId") long domainId, @FormParam("userId") long userId,
								@Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("addMisspell("+url+", "+report+", "+selected+", "+suggestion+", "+domainId+", "+userId+")");
		// Check variables
		if (url == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "url parameter cannot be null").build();
		if (report == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "report parameter cannot be null").build();
		if (selected == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "selected parameter cannot be null").build();
		if (suggestion == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "suggestion parameter cannot be null").build();
		if (!CommonUtilities.isValidLongId(domainId))
			return Response.status(Status.BAD_REQUEST).header("Message", "domainId parameter is invalid").build();
		if (!CommonUtilities.isValidLongId(userId))
			return Response.status(Status.BAD_REQUEST).header("Message", "userId parameter is invalid").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		misspellService = factory.getMisspellService();
		if (misspellService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Misspell misspell = new Misspell();
			misspell.setUrl(url);
			misspell.setReport(report.trim());
			misspell.setSelected(selected.trim());
			misspell.setSuggestion(suggestion.trim());
			misspell.setUserId(userId);
			misspell.setDomainId(domainId);
			misspellService.processMisspell(misspell);
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
	public Response updateMisspell(Misspell misspell, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("updateMisspell("+misspell.toString()+")");
		// Check variables
		if (misspell == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Misspell parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		misspellService = factory.getMisspellService();
		if (misspellService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			misspellService.updateMisspell(misspell);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@POST()
	@Path("update")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response updateMisspellComment(@FormParam("data") String data, @FormParam("comment") String comment,
										  @FormParam("l") String language,
								   @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("updateMisspellComment("+data+", "+comment+")");
		// Check variables
		if (data == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "Data parameter can't be null").build();
		if (comment == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "comment parameter cannot be null").build();
		if (language == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "l parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		misspellService = factory.getMisspellService();
		if (misspellService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Map<String, String> parameters = HttpParameterFormatter.extract(EncryptionUtils.decryptDESede(data));
			Misspell misspell = misspellService.getMisspell(Long.valueOf(parameters.get("misspellId")).longValue());
			misspell.setComment(comment);
			misspellService.updateMisspell(misspell);
			return Response.ok(VelocityUtils.formatDisapprovedOKPage(language)).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@GET()
	@Path("{misspell_id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response getMisspell(@PathParam("misspell_id") long misspellId, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("getMisspell("+misspellId+")");
		// Check variables
		if (!CommonUtilities.isValidLongId(misspellId))
			return Response.status(Status.BAD_REQUEST).header("Message", "misspellId parameter is invalid").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		misspellService = factory.getMisspellService();
		if (misspellService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Misspell misspell = misspellService.getMisspell(misspellId);
			return Response.ok(misspell).build();
		} catch (InstanceNotFoundException ex) {
			return Response.status(Status.NO_CONTENT).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@DELETE()
	@Path("{misspell_id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response deleteMisspell(@PathParam("misspell_id") long misspellId, @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("deleteMisspell("+misspellId+")");
		// Check variables
		if (!CommonUtilities.isValidLongId(misspellId))
			return Response.status(Status.BAD_REQUEST).header("Message", "misspellId parameter cannot be null").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		misspellService = factory.getMisspellService();
		if (misspellService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();
		try {
			//LOGIC
			misspellService.deleteMisspellById(misspellId);
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
	public Response getMisspellByURLAndSelectedAndReport(@QueryParam("url") String url, @QueryParam("selected") String selected,
														 @QueryParam("report") String report,
														 @Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		_log.info("getMisspellByURLAndSelectedAndReport("+url+", "+selected+", "+report+")");
		// Check variables
		if (url == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "url parameter is invalid").build();
		if (selected == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "selected parameter is invalid").build();
		if (report == null)
			return Response.status(Status.BAD_REQUEST).header("Message", "report parameter is invalid").build();
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		misspellService = factory.getMisspellService();
		if (misspellService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Misspell misspell = misspellService.getMisspellByUrlAndReport(url, selected, report);
			return Response.ok(misspell).build();
		} catch (InstanceNotFoundException ex) {
			return Response.status(Status.NO_CONTENT).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
