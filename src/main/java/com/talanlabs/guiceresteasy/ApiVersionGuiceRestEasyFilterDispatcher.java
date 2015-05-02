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
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApiVersionGuiceRestEasyFilterDispatcher extends FilterDispatcher {

	private final String version;
	private final boolean addNotAnnoted;

	@Inject
	private Injector injector;

	public ApiVersionGuiceRestEasyFilterDispatcher(String version) {
		this(version, true);
	}

	public ApiVersionGuiceRestEasyFilterDispatcher(
			String version, boolean addNotAnnoted) {
		super();

		this.version = version;
		this.addNotAnnoted = addNotAnnoted;
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
				List<String> versions = new ArrayList<>();
				ApiVersions apiVersions = beanClass.getAnnotation(ApiVersions.class);
				if (apiVersions != null) {
					versions.addAll(
							Arrays.stream(apiVersions.value()).map(ApiVersion::value).collect(Collectors.toList()));
				}
				ApiVersion apiVersion = beanClass.getAnnotation(ApiVersion.class);
				if (apiVersion != null) {
					versions.add(apiVersion.value());
				}
				if ((versions.isEmpty() && addNotAnnoted) ||
						(!versions.isEmpty() && versions.contains(version))) {
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
}
