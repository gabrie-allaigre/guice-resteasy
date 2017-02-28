package com.talanlabs.guiceresteasy.test.junit;

import com.google.inject.servlet.ServletModule;
import com.talanlabs.guiceresteasy.DefaultGuiceRestEasyFilterDispatcher;
import com.talanlabs.guiceresteasy.test.data.HelloResource;
import com.talanlabs.guiceresteasy.test.data.WelcomeResource;
import com.talanlabs.guiceresteasy.test.utils.AppServletContextListener;
import com.talanlabs.guiceresteasy.test.utils.ServerBootstrap;
import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DefaultTest {

	@Test
	public void testSimple() throws Exception {
		try (ServerBootstrap serverBootstrap = new ServerBootstrap(new AppServletContextListener(new MyModule()))) {
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/rest/hello")).asString())
					.isEqualTo("hello");
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/rest/welcome")).asString())
					.isEqualTo("welcome");
		}
	}

	private static class MyModule extends ServletModule {

		@Override
		protected void configureServlets() {
			Map<String, String> params = new HashMap<>();
			params.put(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/rest");
			filter("/rest/*").through(DefaultGuiceRestEasyFilterDispatcher.class, params);

			bind(HelloResource.class);
			bind(WelcomeResource.class);
		}
	}
}
