package net.proofready.rws;

import net.proofready.common.CommonUtilities;
import net.proofready.common.Constants;
import net.proofready.core.session.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.Serializable;
import java.util.Properties;

/**
 * The <code><b>RWSFactory</b></code> singleton class performs common tasks to initialize <b>remote</b> EJB access
 * and to locate and maintain the references to all Session interfaces.
 * <p/>
 * The class is a <b>context listener</b> and implements methods <code>contextInitialized()</code> and
 * <code>contextDestroyed()</code>. The listener registration is done in file <code>web.xml</code>.<p/>
 * Web service resource classes should use the instance of this class (created during web application startup) to obtain
 * access to business methods in EJBs.
 * <p/>
 * Getting the instance must be done in the following way:<blockquote>
 * <font color="blue" size="3"><code><b>RWsFactory.getInstance();</b></code></font>
 * </blockquote>
 * The class contains logging statements which show the initialization progress in the JBoss application server log. Instantiating EJBs and testing
 * basic business methods to validate the business logic availability is visible in this log.
 *
 * @author Victor
 */
public class RWSFactory implements ServletContextListener, Serializable {
	private static final Log _log = LogFactory.getLog(RWSFactory.class);
	private static final long serialVersionUID = 1L;
	private Context initialContext;
	private static final String PKG_INTERFACES = "org.jboss.ejb.client.naming";
	private static RWSFactory INSTANCE = null; // This will hold the instance of this class
	private boolean initialized = false;

	// SERVICES
	private AdminServiceRemote adminService = null;
	private UserServiceRemote userService = null;
	private DomainServiceRemote domainService = null;
	private MisspellServiceRemote misspellService = null;
	private NotificationServiceRemote notificationService = null;
	private CommonServiceRemote commonService = null;

	/**
	 * Default constructor. Will be called by the web application startup process, so it must be <b>public</b>.
	 */
	public RWSFactory() {
		_log.info("RWSFactory INSTANTIATED");
	}

	/**
	 * This method is called when this web application is stopped
	 *
	 * @param servletContext The servlet context instance
	 */
	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {
		this.initialized = false;
		_log.info("Servet context DESTROYED");
	}

	/**
	 * Returns the instance of this class, created by the web application environment
	 *
	 * @return The instance. If the web application context is not yet initialized, this method will return <b>null</b>.
	 */
	public static RWSFactory getInstance() {
		return INSTANCE;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public AdminServiceRemote getAdminService() {
		return adminService;
	}

	public UserServiceRemote getUserService() {
		return userService;
	}

	public DomainServiceRemote getDomainService() {
		return domainService;
	}

	public MisspellServiceRemote getMisspellService() {
		return misspellService;
	}

	public NotificationServiceRemote getNotificationService() {
		return notificationService;
	}

	public CommonServiceRemote getCommonService() {
		return commonService;
	}

	/**
	 * This method is called when this web application is started
	 *
	 * @param servletContextEvent The servlet context instance
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		_log.info("Servlet context INITIALIZED");
		// Prepare the initial context for remote EJB access at JBoss-7.1 server:
		Properties properties = new Properties();
		properties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);
		try {
			initialContext = new InitialContext(properties);
			_log.info("Initial context created");

			// GET EJB REFERENCE
			_log.info("Look up ["+ AdminServiceRemote.class.getName() +"] EJB...");
			adminService = (AdminServiceRemote) initialContext.lookup(
					CommonUtilities.getLookupEJBName(Constants.CORE_MODULE_NAME, "AdminServiceBean", AdminServiceRemote.class.getName()));
			_log.info("["+ AdminServiceRemote.class.getName() +"] EJB LOCATED");

			_log.info("Look up ["+ UserServiceRemote.class.getName() +"] EJB...");
			userService = (UserServiceRemote) initialContext.lookup(
					CommonUtilities.getLookupEJBName(Constants.CORE_MODULE_NAME, "UserServiceBean", UserServiceRemote.class.getName()));
			_log.info("["+ UserServiceRemote.class.getName() +"] EJB LOCATED");

			_log.info("Look up ["+ DomainServiceRemote.class.getName() +"] EJB...");
			domainService = (DomainServiceRemote) initialContext.lookup(
					CommonUtilities.getLookupEJBName(Constants.CORE_MODULE_NAME, "DomainServiceBean", DomainServiceRemote.class.getName()));
			_log.info("["+ DomainServiceRemote.class.getName() +"] EJB LOCATED");

			_log.info("Look up ["+ MisspellServiceRemote.class.getName() +"] EJB...");
			misspellService = (MisspellServiceRemote) initialContext.lookup(
					CommonUtilities.getLookupEJBName(Constants.CORE_MODULE_NAME, "MisspellServiceBean", MisspellServiceRemote.class.getName()));
			_log.info("["+ MisspellServiceRemote.class.getName() +"] EJB LOCATED");

			_log.info("Look up ["+ NotificationServiceRemote.class.getName() +"] EJB...");
			notificationService = (NotificationServiceRemote) initialContext.lookup(
					CommonUtilities.getLookupEJBName(Constants.CORE_MODULE_NAME, "NotificationServiceBean", NotificationServiceRemote.class.getName()));
			_log.info("["+ NotificationServiceRemote.class.getName() +"] EJB LOCATED");

			_log.info("Look up ["+ CommonServiceRemote.class.getName() +"] EJB...");
			commonService = (CommonServiceRemote) initialContext.lookup(
					CommonUtilities.getLookupEJBName(Constants.CORE_MODULE_NAME, "CommonServiceBean", CommonServiceRemote.class.getName()));
			_log.info("["+ CommonServiceRemote.class.getName() +"] EJB LOCATED");

			// Store the reference to this instance to make it available via method getInstance():
			INSTANCE = this; // Set the instance to this one.
			this.initialized = true; // Mark factory initialized
			_log.info("Application INITIALIZED");
		} catch (Exception ex) {
			this.initialized = false;
			_log.info("Failed to obtain the EJB references");
			_log.error(ExceptionUtils.getStackTrace(ex));
		}

	}

}
