package com.talanlabs.guiceresteasy;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.FilterDispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

import javax.inject.Inject;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListGuiceRestEasyFilterDispatcher extends FilterDispatcher {

	private final List<Key<?>> providerKeys;

	private final List<Key<?>> ressourceKeys;

	@Inject
	private Injector injector;

	public ListGuiceRestEasyFilterDispatcher(List<Key<?>> providerKeys,
			List<Key<?>> ressourceKeys) {
		super();
		this.providerKeys = providerKeys != null ? new ArrayList<>(providerKeys) : new ArrayList<>();
		this.ressourceKeys = ressourceKeys != null ? new ArrayList<>(ressourceKeys) : new ArrayList<>();
	}

	@Override
	public void init(FilterConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		Registry registry = getDispatcher().getRegistry();
		ResteasyProviderFactory providerFactory = getDispatcher()
				.getProviderFactory();

		if (providerKeys != null && !providerKeys.isEmpty()) {
			for (Key<?> provideKey : providerKeys) {
				Binding<?> binding = injector.getBinding(provideKey);
				Key<?> key = binding.getKey();
				Type type = key.getTypeLiteral().getType();
				if (type instanceof Class
						&& ((Class<?>) type)
								.isAnnotationPresent(Provider.class)) {
					providerFactory.registerProviderInstance(binding
							.getProvider().get());
				}
			}
		}
		if (ressourceKeys != null && !ressourceKeys.isEmpty()) {
			for (Key<?> ressourceKey : ressourceKeys) {
				Binding<?> binding = injector.getBinding(ressourceKey);
				Key<?> key = binding.getKey();
				Type type = key.getTypeLiteral().getType();
				if (type instanceof Class
						&& GetRestful.isRootResource((Class<?>) type)) {
					registry.addResourceFactory(new GuiceResourceFactory(
							binding.getProvider(), (Class<?>) type));
				}
			}
		}
	}
}
