package com.talanlabs.guiceresteasy.test.data;

import com.talanlabs.guiceresteasy.ApiVersion;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
@ApiVersion("v2")
public class Hello2Resource {

	@GET
	public String get() {
		return "hello2";
	}
}
