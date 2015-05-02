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
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class AnnotationGuiceRestEasyFilterDispatcher extends FilterDispatcher {

	private final Class<? extends Annotation> annotationClass;
	private final Annotation annotation;
	private final boolean addNotAnnoted;

	@Inject
	private Injector injector;

	public AnnotationGuiceRestEasyFilterDispatcher(
			Class<? extends Annotation> annotationClass) {
		this(annotationClass, false);
	}

	public AnnotationGuiceRestEasyFilterDispatcher(
			Class<? extends Annotation> annotationClass, boolean addNotAnnoted) {
		this(annotationClass, null, addNotAnnoted);
	}

	public AnnotationGuiceRestEasyFilterDispatcher(Annotation annotation) {
		this(annotation, false);
	}

	public AnnotationGuiceRestEasyFilterDispatcher(Annotation annotation, boolean addNotAnnoted) {
		this(null, annotation, addNotAnnoted);
	}

	private AnnotationGuiceRestEasyFilterDispatcher(
			Class<? extends Annotation> annotationClass, Annotation annotation, boolean addNotAnnoted) {
		super();

		this.annotationClass = annotationClass;
		this.annotation = annotation;
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
			Annotation a = key.getAnnotation();
			Class<? extends Annotation> ac = key.getAnnotationType();
			Type type = key.getTypeLiteral().getType();
			if (type instanceof Class
					&& (((annotationClass != null && annotationClass.equals(ac)) || (annotation != null && annotation
					.equals(a))) || (a == null && ac == null && addNotAnnoted))) {
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
