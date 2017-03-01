# Guice for Resteasy
 
## Description

Easy tu use Resteay with Guice

## Configuration

```xml
<dependency>
	<groupId>com.talanlabs</groupId>
	<artifactId>guice-resteasy</artifactId>
	<version>1.0.0</version>
</dependency>
```

## Usage

### Method 1: One repository

Add filter in our module ServletModule

```java
Map<String, String> params = new HashMap<String, String>();
params.put(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/rest");
filter("/rest/*").through(DefaultGuiceRestEasyFilterDispatcher.class, params);

bind(HelloProvider.class);
bind(HelloResource.class);
```

### Method 2 : Multi repository

Use module AbstractResteasyModule

```java
install(new AbstractResteasyModule() {
	@Override
	protected void configureReasteay() {
		serveResources("/api/v1/*").prefix("/api/v1")
			.provider(TokenSecurityInterceptor.class)
			.provider(AllowOriginInterceptor.class)
			.provider(ApplicationExceptionHandler.class)
			.provider(ThrowableHandler.class)
			.resource(AuthRest.class);

		serveResources("/i18n/*").prefix("/i18n")
			.resource(I18nResource.class);

		serveResources("/login").resource(LoginResource.class);

		serveResources("/page", "/page1")
			.provider(PageSecurityInterceptor.class)
			.resource(PageResource.class);
	}
});	
```

### Method 3 : Multi repository with annotation

```java
Map<String, String> param1s = new HashMap<String, String>();
param1s.put(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/rest");
filter("/rest/*").through(new AnnotationGuiceRestEasyFilterDispatcher(Names.named("domain1")), param1s);

bind(HelloProvider.class).annotatedWith(Names.named("domain1")).to(HelloProvider.class);
bind(HelloRessource.class).annotatedWith(Names.named("domain1")).to(HelloRessource.class);

Map<String, String> param2s = new HashMap<String, String>();
param2s.put(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/autre");
filter("/autre/*").through(new AnnotationGuiceRestEasyFilterDispatcher(Names.named("domain2")), param2s);

bind(WorldProvider.class).annotatedWith(Names.named("domain2")).to(WorldProvider.class);
bind(WorldRessource.class).annotatedWith(Names.named("domain2")).to(WorldRessource.class);
```

### Method 4 : ApiVersion repository

```java
Map<String, String> param1s = new HashMap<String, String>();
param1s.put(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/api/v1");
filter("/api/v1/*").through(new ApiVersionGuiceRestEasyFilterDispatcher("v1"), param1s);

Map<String, String> param2s = new HashMap<String, String>();
param2s.put(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, "/api/v2");
filter("/api/v2/*").through(new ApiVersionGuiceRestEasyFilterDispatcher("v2"), param2s);

bind(HelloProvider.class);
bind(HelloResource.class);
bind(Hello2Resource.class);
```

```java
@ApiVersion("v1")
public class HelloResource {
	...
}

@ApiVersion("v2")
public class Hello2Resource {
	...
}
```