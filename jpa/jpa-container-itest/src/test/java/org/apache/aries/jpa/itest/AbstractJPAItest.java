package org.apache.aries.jpa.itest;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.CoreOptions.when;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;

import org.apache.aries.itest.AbstractIntegrationTest;
import org.apache.aries.jpa.container.PersistenceUnitConstants;
import org.apache.aries.jpa.container.context.PersistenceContextProvider;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public abstract class AbstractJPAItest extends AbstractIntegrationTest {
	protected static final String TEST_UNIT = "test-unit";
	protected static final String BP_TEST_UNIT = "bp-test-unit";
	protected static final String BP_XA_TEST_UNIT = "bp-xa-test-unit";
	protected static final String TEST_BUNDLE_NAME = "org.apache.aries.jpa.org.apache.aries.jpa.container.itest.bundle";
	private static final String FILTER_CONTAINER_MANAGED = "(" + PersistenceUnitConstants.CONTAINER_MANAGED_PERSISTENCE_UNIT + "=true)";
	private static final String FILTER_PROXY = "(" + PersistenceContextProvider.PROXY_FACTORY_EMF_ATTRIBUTE + "=*)";

	protected void registerClient(String unitName) {
		PersistenceContextProvider provider = context().getService(PersistenceContextProvider.class);
		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put(PersistenceContextProvider.PERSISTENCE_CONTEXT_TYPE, PersistenceContextType.TRANSACTION);
		provider.registerContext(unitName, bundleContext.getBundle(), props);
	}
	
	protected EntityManagerFactory getProxyEMF(String name) {
		return context().getService(EntityManagerFactory.class, "(&(osgi.unit.name=" + name + ")" 
				+ FILTER_CONTAINER_MANAGED + FILTER_PROXY +")");
	}
	
	protected EntityManagerFactory getEMF(String name) {
		return context().getService(EntityManagerFactory.class, "(&(osgi.unit.name=" + name + ")" + FILTER_CONTAINER_MANAGED + ")");
	}
	
	@SuppressWarnings("rawtypes")
	protected ServiceReference[] getEMFRefs(String name) throws InvalidSyntaxException {
		return bundleContext.getAllServiceReferences(EntityManagerFactory.class.getName(), "(&(osgi.unit.name=" + name + ")"
				+ FILTER_CONTAINER_MANAGED + ")");
	}
	
	@SuppressWarnings("rawtypes")
	protected ServiceReference[] getProxyEMFRefs(String name)
			throws InvalidSyntaxException {
		return bundleContext.getAllServiceReferences(EntityManagerFactory.class.getName(), "(&(osgi.unit.name=" + name + ")" 
			+ FILTER_CONTAINER_MANAGED + FILTER_PROXY + ")");
	}


	protected Option baseOptions() {
        String localRepo = System.getProperty("maven.repo.local");
     
        if (localRepo == null) {
            localRepo = System.getProperty("org.ops4j.pax.url.mvn.localRepository");
        }
        return composite(
                junitBundles(),
                mavenBundle("org.ops4j.pax.logging", "pax-logging-api", "1.7.2"),
                mavenBundle("org.ops4j.pax.logging", "pax-logging-service", "1.7.2"),
                mavenBundle("org.apache.aries.testsupport", "org.apache.aries.testsupport.unit").versionAsInProject(),
                // this is how you set the default log level when using pax
                // logging (logProfile)
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"),
                when(localRepo != null).useOptions(vmOption("-Dorg.ops4j.pax.url.mvn.localRepository=" + localRepo))
         );
    }
	
	protected Option ariesJpa() {
		return composite(
				frameworkProperty("org.osgi.framework.system.packages")
            	.value("javax.accessibility,javax.activation,javax.activity,javax.annotation,javax.annotation.processing,javax.crypto,javax.crypto.interfaces,javax.crypto.spec,javax.imageio,javax.imageio.event,javax.imageio.metadata,javax.imageio.plugins.bmp,javax.imageio.plugins.jpeg,javax.imageio.spi,javax.imageio.stream,javax.jws,javax.jws.soap,javax.lang.model,javax.lang.model.element,javax.lang.model.type,javax.lang.model.util,javax.management,javax.management.loading,javax.management.modelmbean,javax.management.monitor,javax.management.openmbean,javax.management.relation,javax.management.remote,javax.management.remote.rmi,javax.management.timer,javax.naming,javax.naming.directory,javax.naming.event,javax.naming.ldap,javax.naming.spi,javax.net,javax.net.ssl,javax.print,javax.print.attribute,javax.print.attribute.standard,javax.print.event,javax.rmi,javax.rmi.CORBA,javax.rmi.ssl,javax.script,javax.security.auth,javax.security.auth.callback,javax.security.auth.kerberos,javax.security.auth.login,javax.security.auth.spi,javax.security.auth.x500,javax.security.cert,javax.security.sasl,javax.sound.midi,javax.sound.midi.spi,javax.sound.sampled,javax.sound.sampled.spi,javax.sql,javax.sql.rowset,javax.sql.rowset.serial,javax.sql.rowset.spi,javax.swing,javax.swing.border,javax.swing.colorchooser,javax.swing.event,javax.swing.filechooser,javax.swing.plaf,javax.swing.plaf.basic,javax.swing.plaf.metal,javax.swing.plaf.multi,javax.swing.plaf.synth,javax.swing.table,javax.swing.text,javax.swing.text.html,javax.swing.text.html.parser,javax.swing.text.rtf,javax.swing.tree,javax.swing.undo,javax.tools,javax.xml,javax.xml.bind,javax.xml.bind.annotation,javax.xml.bind.annotation.adapters,javax.xml.bind.attachment,javax.xml.bind.helpers,javax.xml.bind.util,javax.xml.crypto,javax.xml.crypto.dom,javax.xml.crypto.dsig,javax.xml.crypto.dsig.dom,javax.xml.crypto.dsig.keyinfo,javax.xml.crypto.dsig.spec,javax.xml.datatype,javax.xml.namespace,javax.xml.parsers,javax.xml.soap,javax.xml.stream,javax.xml.stream.events,javax.xml.stream.util,javax.xml.transform,javax.xml.transform.dom,javax.xml.transform.sax,javax.xml.transform.stax,javax.xml.transform.stream,javax.xml.validation,javax.xml.ws,javax.xml.ws.handler,javax.xml.ws.handler.soap,javax.xml.ws.http,javax.xml.ws.soap,javax.xml.ws.spi,javax.xml.xpath,org.ietf.jgss,org.omg.CORBA,org.omg.CORBA.DynAnyPackage,org.omg.CORBA.ORBPackage,org.omg.CORBA.TypeCodePackage,org.omg.CORBA.portable,org.omg.CORBA_2_3,org.omg.CORBA_2_3.portable,org.omg.CosNaming,org.omg.CosNaming.NamingContextExtPackage,org.omg.CosNaming.NamingContextPackage,org.omg.Dynamic,org.omg.DynamicAny,org.omg.DynamicAny.DynAnyFactoryPackage,org.omg.DynamicAny.DynAnyPackage,org.omg.IOP,org.omg.IOP.CodecFactoryPackage,org.omg.IOP.CodecPackage,org.omg.Messaging,org.omg.PortableInterceptor,org.omg.PortableInterceptor.ORBInitInfoPackage,org.omg.PortableServer,org.omg.PortableServer.CurrentPackage,org.omg.PortableServer.POAManagerPackage,org.omg.PortableServer.POAPackage,org.omg.PortableServer.ServantLocatorPackage,org.omg.PortableServer.portable,org.omg.SendingContext,org.omg.stub.java.rmi,org.w3c.dom,org.w3c.dom.bootstrap,org.w3c.dom.css,org.w3c.dom.events,org.w3c.dom.html,org.w3c.dom.ls,org.w3c.dom.ranges,org.w3c.dom.stylesheets,org.w3c.dom.traversal,org.w3c.dom.views,org.xml.sax,org.xml.sax.ext,org.xml.sax.helpers"),
				//mavenBundle("org.osgi", "org.osgi.compendium").versionAsInProject(),
				//mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.cglib").versionAsInProject(),
            	mavenBundle("org.osgi", "org.osgi.enterprise").versionAsInProject(),
				mavenBundle("org.ow2.asm", "asm-all").versionAsInProject(),

				mavenBundle("org.apache.aries.proxy", "org.apache.aries.proxy.api").versionAsInProject(),
				mavenBundle("org.apache.aries.proxy", "org.apache.aries.proxy.impl").versionAsInProject(),

				mavenBundle("org.apache.aries.jndi", "org.apache.aries.jndi.api").versionAsInProject(),
				mavenBundle("org.apache.aries.jndi", "org.apache.aries.jndi.core").versionAsInProject(),
				mavenBundle("org.apache.aries.jndi", "org.apache.aries.jndi.url").versionAsInProject(),
				
				mavenBundle("org.apache.aries.quiesce", "org.apache.aries.quiesce.api").versionAsInProject(),
				mavenBundle("org.apache.aries", "org.apache.aries.util").versionAsInProject(),

				mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint.api").versionAsInProject(),
				mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint.core").versionAsInProject(),

                mavenBundle("org.apache.geronimo.specs", "geronimo-jpa_2.0_spec", "1.1"),
				// mavenBundle("org.hibernate.javax.persistence", "hibernate-jpa-2.1-api").versionAsInProject(),
				mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.api").versionAsInProject(),
				mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.container").versionAsInProject(),
				mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.container.context").versionAsInProject(),
				mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.blueprint.aries").versionAsInProject(),
				
				mavenBundle("org.apache.geronimo.specs", "geronimo-jta_1.1_spec").versionAsInProject(),
				mavenBundle("org.apache.aries.transaction", "org.apache.aries.transaction.manager").versionAsInProject(),
				
				mavenBundle("commons-lang", "commons-lang").versionAsInProject(),
				mavenBundle("commons-collections", "commons-collections").versionAsInProject(),
				mavenBundle("commons-pool", "commons-pool").versionAsInProject(),
				
				mavenBundle("org.apache.derby", "derby").versionAsInProject()
				
				);
	}
	
	protected Option transactionWrapper() {
		return mavenBundle("org.apache.aries.transaction", "org.apache.aries.transaction.wrappers" ).versionAsInProject();
	}
	
	protected Option eclipseLink() {
		return composite(
				mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa").versionAsInProject(),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.core").versionAsInProject(),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.asm").versionAsInProject(),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.antlr").versionAsInProject(),
                mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.eclipselink.adapter").versionAsInProject()
                );
	}
	
	protected Option openJpa() {
		return composite(
				mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.serp").versionAsInProject(),
				mavenBundle("org.apache.geronimo.specs", "geronimo-servlet_2.5_spec").versionAsInProject(),
				mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.commons-dbcp").versionAsInProject(),
				mavenBundle("org.apache.xbean","xbean-asm4-shaded").versionAsInProject(),
				mavenBundle("org.apache.openjpa", "openjpa").versionAsInProject()
				);
	}
	
	protected Option testDs() {
		return mavenBundle("org.apache.aries.transaction", "org.apache.aries.transaction.testds").versionAsInProject();
	}
	
	protected MavenArtifactProvisionOption derbyDataSourceFactory() {
		return mavenBundle("org.ops4j.pax.jdbc", "pax-jdbc-derby").versionAsInProject();
	}
	
	protected MavenArtifactProvisionOption testBundle() {
		return mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.container.itest.bundle").versionAsInProject();
	}
	
	protected MavenArtifactProvisionOption testBundleBlueprint() {
		return mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.blueprint.itest.bundle").versionAsInProject();
	}
	
	protected MavenArtifactProvisionOption testBundleEclipseLink() {
		return mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.container.itest.bundle.eclipselink");
	}
	
	protected MavenArtifactProvisionOption testBundleAdvanced() {
		return mavenBundle("org.apache.aries.jpa", "org.apache.aries.jpa.container.advanced.itest.bundle").versionAsInProject();
	}
}