package com.talanlabs.guiceresteasy.test.data;

import com.talanlabs.guiceresteasy.ApiVersion;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
@ApiVersion("v1")
public class Hello1Resource {

	@GET
	public String get() {
		return "hello1";
	}
}
