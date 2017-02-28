package com.talanlabs.guiceresteasy.test.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

public class AppServletContextListener extends GuiceServletContextListener {

	private final Module[] modules;

	public AppServletContextListener(Module... modules) {
		super();

		this.modules = modules;
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(modules);
	}
}
