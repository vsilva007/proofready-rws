package net.proofready.rws;

import net.proofready.core.session.CommonServiceRemote;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/service/")
public class ServiceRWS {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private RWSFactory factory = null;
	private CommonServiceRemote commonService;


	public ServiceRWS() {
		factory = RWSFactory.getInstance(); // Get the RWSFactory
	}

	@HEAD()
	@Path("test")
	public Response availabilityTest() {
		return Response.status(Status.OK).header("Message", this.getClass().getName() + " AVAILABLE").build();
	}

	@GET()
	@Path("version")
	@Produces(MediaType.TEXT_PLAIN)
	public Response serviceVersion() {
		System.out.println("serviceVersion()");
		// Get EJBs:
		if (factory == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Web service initialization error").build();
		commonService = factory.getCommonService();
		if (commonService == null)
			return Response.status(Status.SERVICE_UNAVAILABLE).header("Message", "Business logic unavailable").build();

		try {
			//LOGIC
			return Response.ok(commonService.getServiceVersion()).build();
		} catch (Exception ex) {
			_log.error(ExceptionUtils.getStackTrace(ex));
			return Response.status(Status.INTERNAL_SERVER_ERROR).header("Message", ex.getMessage()).build();
		}
	}
}
