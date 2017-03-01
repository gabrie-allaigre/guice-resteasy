package com.talanlabs.guiceresteasy.test.data;

import com.talanlabs.guiceresteasy.ApiVersion;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/bye")
@ApiVersion("v1")
public class ByeResource {

	@GET
	public String get() {
		return "bye";
	}
}
