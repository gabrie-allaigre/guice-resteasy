package com.talanlabs.guiceresteasy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Add api version for provider or resource
 */
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface ApiVersions {

	ApiVersion[] value();

}
