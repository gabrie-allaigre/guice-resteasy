package com.talanlabs.guiceresteasy.test.junit;

import com.google.inject.servlet.ServletModule;
import com.talanlabs.guiceresteasy.AbstractResteasyModule;
import com.talanlabs.guiceresteasy.test.data.Hello1Resource;
import com.talanlabs.guiceresteasy.test.data.HelloResource;
import com.talanlabs.guiceresteasy.test.data.WelcomeResource;
import com.talanlabs.guiceresteasy.test.utils.AppServletContextListener;
import com.talanlabs.guiceresteasy.test.utils.ServerBootstrap;
import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ListTest {

	@Test
	public void testSimple() throws Exception {
		try (ServerBootstrap serverBootstrap = new ServerBootstrap(new AppServletContextListener(new MyModule()))) {
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/rest/hello")).asString())
					.isEqualTo("hello");
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/api/welcome")).asString())
					.isEqualTo("welcome");
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/api/hello")).asString())
					.isEqualTo("hello1");
			Assertions.assertThat(RestAssured.get(serverBootstrap.buildUrl("/rest/welcome")).getStatusCode())
					.isEqualTo(404);
		}
	}

	private static class MyModule extends ServletModule {

		@Override
		protected void configureServlets() {
			install(new AbstractResteasyModule() {
				@Override
				protected void configureReasteay() {
					serveResources("/rest/*").prefix("rest")
							.resource(HelloResource.class);

					serveResources("/api/*").prefix("/api")
							.resource(WelcomeResource.class)
							.resource(Hello1Resource.class);
				}
			});
		}
	}
}
