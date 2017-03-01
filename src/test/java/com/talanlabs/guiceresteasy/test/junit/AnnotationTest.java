package com.talanlabs.guiceresteasy.test.junit;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.talanlabs.guiceresteasy.AnnotationGuiceRestEasyFilterDispatcher;
import com.talanlabs.guiceresteasy.test.data.ByeResource;
import com.talanlabs.guiceresteasy.test.data.Hello1Resource;
import com.talanlabs.guiceresteasy.test.data.Hello2Resource;
import com.talanlabs.guiceresteasy.test.utils.AppServletContextListener;
import com.talanlabs.guiceresteasy.test.utils.ServerBootstrap;
import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AnnotationTest {

	@Test
	public void testSimple() throws Exception {
		try (ServerBootstrap serverBootstrap = new ServerBootstrap(new AppServletContextListener(new MyModule()))) {
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/api/v1/hello")).asString())
					.isEqualTo("hello1");
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/api/v2/hello")).asString())
					.isEqualTo("hello2");
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/api/v1/bye")).asString())
					.isEqualTo("bye");
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/api/v2/bye")).getStatusCode())
					.isEqualTo(404);
		}
	}

	private static class MyModule extends ServletModule {

		@Override
		protected void configureServlets() {
			Map<String, String> param1s = new HashMap<>();
			param1s.put(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/api/v1");
			filter("/api/v1/*").through(new AnnotationGuiceRestEasyFilterDispatcher(Names.named("v1")), param1s);

			Map<String, String> param2s = new HashMap<>();
			param2s.put(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/api/v2");
			filter("/api/v2/*").through(new AnnotationGuiceRestEasyFilterDispatcher(Names.named("v2")), param2s);

			bind(Hello1Resource.class).annotatedWith(Names.named("v1")).to(Hello1Resource.class);
			bind(Hello2Resource.class).annotatedWith(Names.named("v2")).to(Hello2Resource.class);
			bind(ByeResource.class).annotatedWith(Names.named("v1")).to(ByeResource.class);
		}
	}
}
