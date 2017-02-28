package com.talanlabs.guiceresteasy.test.utils;

import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContextListener;
import java.io.Closeable;
import java.io.IOException;
import java.util.EnumSet;

public class ServerBootstrap implements Closeable {

	private Server server;

	public ServerBootstrap(ServletContextListener servletContextListener) throws Exception {
		super();

		server = new Server(0);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		context.addFilter(GuiceFilter.class, "/*", EnumSet.noneOf(DispatcherType.class));
		context.addEventListener(servletContextListener);
		server.setHandler(context);
		server.start();
	}

	public String buildUrl(String path) {
		String url = server.getURI().toString();
		return url.substring(0, url.length() - 1) + path;
	}

	@Override
	public void close() throws IOException {
		try {
			server.stop();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
