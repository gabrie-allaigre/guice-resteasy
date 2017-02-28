package com.talanlabs.guiceresteasy.test.data;

import com.talanlabs.guiceresteasy.ApiVersion;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/welcome")
@ApiVersion("v1")
@ApiVersion("v2")
public class WelcomeResource {

	@GET
	public String get() {
		return "welcome";
	}
}
