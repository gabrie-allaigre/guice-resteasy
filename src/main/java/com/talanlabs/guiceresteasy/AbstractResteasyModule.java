package com.talanlabs.guiceresteasy;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.servlet.ServletModule;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.util.GetRestful;

import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractResteasyModule extends AbstractModule {

	private List<ResourceBuilder> resourceBuilders;

	public AbstractResteasyModule() {
		super();

		this.resourceBuilders = new ArrayList<>();
	}

	@Override
	protected void configure() {
		configureReasteay();

		install(new ServletModule() {
			@Override
			protected void configureServlets() {
				super.configureServlets();

				if (resourceBuilders != null && !resourceBuilders.isEmpty()) {
					for (ResourceBuilder resteasyBuilder : resourceBuilders) {
						Map<String, String> params = new HashMap<String, String>();
						params.put(
								ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX,
								resteasyBuilder.prefix);
						if (resteasyBuilder.regex) {
							filterRegex(resteasyBuilder.url,
							            resteasyBuilder.mores).through(
									new ListGuiceRestEasyFilterDispatcher(
											resteasyBuilder.providerKeys,
											resteasyBuilder.resourceKeys),
									params);
						} else {
							filter(resteasyBuilder.url, resteasyBuilder.mores)
									.through(
											new ListGuiceRestEasyFilterDispatcher(
													resteasyBuilder.providerKeys,
													resteasyBuilder.resourceKeys),
											params);
						}
					}
				}
			}
		});
	}

	protected abstract void configureReasteay();

	/**
	 * Add resources
	 *
	 * @param urlPattern   pattern
	 * @param morePatterns more pattern
	 */
	public ResourceBuilder serveResources(String urlPattern,
	                                      String... morePatterns) {
		ResourceBuilder resteasyBuilder = new ResourceBuilder(false,
		                                                      urlPattern, morePatterns);
		resourceBuilders.add(resteasyBuilder);
		return resteasyBuilder;
	}

	/**
	 * Add resources with regex
	 *
	 * @param regex
	 * @param regexes more regex
	 */
	public ResourceBuilder serveResourcesRegex(String regex,
	                                           String... regexes) {
		ResourceBuilder resteasyBuilder = new ResourceBuilder(true, regex,
		                                                      regexes);
		resourceBuilders.add(resteasyBuilder);
		return resteasyBuilder;
	}

	public interface ListResourceBuilder {

		/**
		 * Add provider class
		 *
		 * @param providerClass
		 * @return
		 */
		public ListResourceBuilder provider(Class<?> providerClass);

		/**
		 * Add provider key
		 *
		 * @param providerKey
		 * @return
		 */
		public ListResourceBuilder provider(Key<?> providerKey);

		/**
		 * Add resource class
		 *
		 * @param resourceClass
		 * @return
		 */
		public ListResourceBuilder resource(Class<?> resourceClass);

		/**
		 * Add resource key
		 *
		 * @param resourceKey
		 * @return
		 */
		public ListResourceBuilder resource(Key<?> resourceKey);
	}

	public class ResourceBuilder implements ListResourceBuilder {

		private boolean regex;

		private String url;

		private String[] mores;

		private List<Key<?>> providerKeys;

		private List<Key<?>> resourceKeys;

		private String prefix;

		ResourceBuilder(boolean regx, String url, String[] mores) {
			super();

			this.regex = regx;
			this.url = url;
			this.mores = mores != null ? Arrays.copyOf(mores, mores.length) : new String[0];

			this.providerKeys = new ArrayList<>();
			this.resourceKeys = new ArrayList<>();
		}

		public ListResourceBuilder prefix(String prefix) {
			this.prefix = prefix;
			return this;
		}

		@Override
		public ListResourceBuilder provider(Class<?> providerClass) {
			return provider(Key.get(providerClass));
		}

		@Override
		public ListResourceBuilder provider(Key<?> providerKey) {
			Type type = providerKey.getTypeLiteral().getType();
			if (!(type instanceof Class)
					|| !((Class<?>) type).isAnnotationPresent(Provider.class)) {
				throw new IllegalArgumentException(
						"Type is not a Class or not a @Provider");
			}
			providerKeys.add(providerKey);
			return this;
		}

		@Override
		public ListResourceBuilder resource(Class<?> resourceClass) {
			return resource(Key.get(resourceClass));
		}

		@Override
		public ListResourceBuilder resource(Key<?> resourceKey) {
			Type type = resourceKey.getTypeLiteral().getType();
			if (!(type instanceof Class)
					|| !GetRestful.isRootResource((Class<?>) type)) {
				throw new IllegalArgumentException(
						"Type is not a Class or not a @Path");
			}
			resourceKeys.add(resourceKey);
			return this;
		}
	}
}
