package net.proofready.rws;

import net.proofready.core.entity.Admin;
import net.proofready.core.session.AdminServiceRemote;
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

@Path("/admin/")
public class AdminRWS {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private RWSFactory factory = null;
	private AdminServiceRemote adminService = null;


	public AdminRWS() {
		factory = RWSFactory.getInstance(); // Get the RWSFactory
	}

	@HEAD()
	@Path("test")
	public Response availabilityTest() {
		return Response.status(Status.OK).header("Message", this.getClass().getName() + " AVAILABLE").build();
	}

	@POST()
	@Path("add")
	@Produces(MediaType.TEXT_HTML)
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response addAdmin(@Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("addAdmin()");
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		adminService = factory.getAdminService();
		if (adminService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			Admin admin = new Admin();
			adminService.addAdmin(admin);
			return Response.ok().build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}

	@GET()
	@Path("encrypt/{data}")
	@Produces(MediaType.TEXT_HTML)
	//@RolesAllowed({ Permission.ROLE_CONSULTA, Permission.ROLE_ADMIN })
	public Response encryptData(@PathParam("data") String data,
								@Context SecurityContext securityContext, @Context HttpServletRequest hsreq) {
		System.out.println("encryptData("+data+")");
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		adminService = factory.getAdminService();
		if (adminService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			return Response.ok(adminService.encryptDESede(data)).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
