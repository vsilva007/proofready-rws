package net.proofready.rws;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class RWSApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();

	public RWSApplication() {
		singletons.add(new AdminRWS());
		singletons.add(new DomainRWS());
		singletons.add(new MisspellRWS());
		singletons.add(new UserRWS());
		singletons.add(new UserRatingRWS());
		singletons.add(new UserStatisticRWS());
		singletons.add(new NotificationRWS());
		singletons.add(new PageRWS());
		singletons.add(new ServiceRWS());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return empty;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
