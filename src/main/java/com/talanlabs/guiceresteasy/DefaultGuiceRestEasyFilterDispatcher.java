package com.talanlabs.guiceresteasy;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.FilterDispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;

@Singleton
public class DefaultGuiceRestEasyFilterDispatcher extends FilterDispatcher {

	private Injector injector;

	public DefaultGuiceRestEasyFilterDispatcher() {
		super();
	}

	@Inject
	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	@Override
	public void init(FilterConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		Registry registry = getDispatcher().getRegistry();
		ResteasyProviderFactory providerFactory = getDispatcher()
				.getProviderFactory();

		for (final Binding<?> binding : injector.getBindings().values()) {
			Key<?> key = binding.getKey();
			Type type = key.getTypeLiteral().getType();
			if (type instanceof Class) {
				Class<?> beanClass = (Class<?>) type;
				if (GetRestful.isRootResource(beanClass)) {
					ResourceFactory resourceFactory = new GuiceResourceFactory(
							binding.getProvider(), beanClass);
					registry.addResourceFactory(resourceFactory);
				}

				if (beanClass.isAnnotationPresent(Provider.class)) {
					providerFactory.registerProviderInstance(binding
							.getProvider().get());
				}
			}
		}
	}
}
