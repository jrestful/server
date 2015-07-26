# core

`core` includes generic classes for services and controllers via Spring and Spring MVC, HATEOAS support over HAL, as well as CSRF protection via Spring Security and other utilities.

## How to install it?

### In your pom.xml

Add the repository:

    <repositories>
      <repository>
        <id>jrestul-mvn-repo</id>
        <url>https://raw.github.com/jrestful/server/mvn-repo/</url>
        <snapshots>
          <enabled>true</enabled>
          <updatePolicy>always</updatePolicy>
        </snapshots>
      </repository>
    </repositories>

Add the dependency:
    
    <dependencies>
    
      <dependency>
        <groupId>org.jrestful</groupId>
        <artifactId>core</artifactId>
        <version>${jrestful.version}</version>
      </dependency>
    
    </dependencies>

### In your web.xml

Add the context parameters:

    <context-param>
      <description>The Spring resource location of the application properties</description>
      <param-name>jrestfulAppProps</param-name>
      <!-- Example: -->
      <param-value>classpath:application.properties</param-value>
    </context-param>
    
    <context-param>
      <description>The Spring resource location of the security properties</description>
      <param-name>jrestfulSecProps</param-name>
      <!-- Example: -->
      <param-value>WEB-INF/security.properties</param-value>
    </context-param>

Update the Spring `contextConfigLocation` parameters:

    <context-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>
        ...
        classpath*:jrestful/applicationContext.xml
        ...
      </param-value>
    </context-param>
    
    <servlet>
      <servlet-name>dispatcher</servlet-name>
      <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
      <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
          ...
          classpath*:jrestful/servlet/servletContext.xml
          ...
        </param-value>
      </init-param>
      <load-on-startup>1</load-on-startup>
    </servlet>

## What is expected in the properties files?

### `jrestfulAppProps`

#### The application version

 - `app.version`: The application version.

jrestful registers request interceptors, but will exclude the URLs matching `/static-${app.version}/**`: it is expecting you to map your static resources (images, JS, CSS...) to the URL `/static-${app.version}/**`, e.g. `http://www.domain.tld/static-1.3.9/img/logo.png` (this way, for new versions, browsers will automatically recache the resources).

To implement this mapping, add the following tag in your Spring MVC servlet context:

    <mvc:resources
      mapping="/static-${app.version}/**"
      location="/static/"
      cache-period="31556926" />

Here, your resources are expected to be located in a `/static` folder.

#### Example

    app.version=1.3.9

### The API version

 - `app.apiVersion`: The API version.

jrestful registers request interceptors that only match URL starting with `/api-${app.apiVersion}/**`. To implement your API behind this mapping, use a placeholder in the `@RequestMapping` annotation:

    @RestController
    @RequestMapping("/api-${app.apiVersion}")
    public class ArticleController {
    
    }

#### Example

    app.apiVersion=1.3

### `jrestfulSecProps`

#### The CSRF parameters (optional)

 - `csrf.headerName`: The request header that will be read for CSRF protection.
 - `csrf.cookieName`: The cookie that will be read for CSRF protection.

The CSRF protection is implemented as explained in [Robbert van Waveren article](http://blog.jdriven.com/2014/10/stateless-spring-security-part-1-stateless-csrf-protection/).

If not provided, the filter won't be registrable.

#### Example

    csrf.headerName=X-CSRF-TOKEN
    csrf.cookieName=MYWEBSITE-CSRF-TOKEN

#### The CORS parameters (optional)

 - `cors.allowOrigin`: The `Access-Control-Allow-Origin` header to set on HTTP responses when requests match `/api-${app.apiVersion}/**`.

If not provided, no header will be added.

#### Example

    cors.allowOrigin=*

## What does `core` provide?

### For your services

`org.jrestful.business.support.GenericService` and `org.jrestful.business.support.GenericServiceImpl` are an interface and an abstract class for your services.

### For your controllers

`org.jrestful.web.controllers.support.GenericController` and `org.jrestful.web.controllers.rest.support.GenericRestController` are abstract classes for your controllers and REST controllers.

`org.jrestful.web.hateoas.Resource` and `org.jrestful.web.hateoas.Resources` help you responding to REST requests with HATEOAS over HAL. Example, where the `linkTo` and `methodOn` methods belong to `org.springframework.hateoas.mvc.ControllerLinkBuilder`:

    @RequestMapping(value = "/articles/{id}", method = RequestMethod.GET, produces = Resource.HAL_MEDIA_TYPE)
    @ResponseBody
    public ResponseEntity<Resource<Article>> get(@PathVariable String id) {
      Article article = articleService.findOne(id);
      if (article == null) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      } else {
        Resource<Article> resource = new Resource<>(article, linkTo(methodOn(getClass()).get(article.getId())));
        return new ResponseEntity<>(resource, HttpStatus.OK);
      }
    }
    
    @RequestMapping(value = "/articles", method = RequestMethod.GET, produces = Resource.HAL_MEDIA_TYPE)
    @ResponseBody
    public ResponseEntity<Resources<Article>> list() {
      List<Article> articles = articleService.findAll();
      List<Resource<Article>> articleResources = new ArrayList<>();
      for (Article article : articles) {
        Resource<Article> articleResource = new Resource<>(article, linkTo(methodOn(getClass()).get(article.getId())));
        articleResources.add(articleResource);
      }
      Resources<Article> resources = new Resources<>(articleResources, linkTo(methodOn(getClass()).list()));
      return new ResponseEntity<>(resources, HttpStatus.OK);
    }

Be aware that `ControllerLinkBuilder` isn't able to resolve placeholders yet: you may need to hardcode them in your mapping until a fix is provided (see https://github.com/spring-projects/spring-hateoas/issues/220).

`org.jrestful.web.util.UrlInterceptor` (automatically registered) adds attributes for each request (excluding those matching `/static-${app.version}/**`).

Example with a request on `http://domain.tld/context/url?param=value`:

 - `UrlInterceptor.DOMAIN_URL`: http://domain.tld
 - `UrlInterceptor.BASE_URL`: http://domain.tld/context
 - `UrlInterceptor.REQUEST_URL`: http://domain.tld/context/url
 - `UrlInterceptor.FULL_URL`: http://domain.tld/context/url?param=value
 - `UrlInterceptor.SHORT_URL`: /url

### For the security

A CSRF protection filter as available. To register it in your Spring Security context:

    <http ...>
      ...
      <custom-filter ref="statelessCsrfFilter" position="CSRF_FILTER" />
      ...
    </http>

### Utilities

 - `org.jrestful.util.Base64Utils`: base 64 encoding and decoding.
 - `org.jrestful.util.DateUtils`: operations on dates.
 - `org.jrestful.util.Json64Utils`: JSON serialization and deserialization.
 - `org.jrestful.util.UrlUtils`: operations on URLs.
 - `org.jrestful.util.web.HttpUtils`: operations on HTTP requests and responses.
