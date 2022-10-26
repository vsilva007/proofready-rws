package net.proofready.rws.providers;

import net.proofready.rws.RWSFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
@Produces({ MediaType.APPLICATION_JSON, "text/json" })
public class JacksonConfigurator extends ResteasyJacksonProvider {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);

	public JacksonConfigurator() {
		super();
		_log.info("Seting up resteasy provider to: JacksonConfigurator");
	}
}