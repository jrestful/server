# core

`core` includes generic classes for services and controllers via Spring and Spring MVC, as well as CSRF protection via Spring Security and other utilities.

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

### In your Spring Security context

Register the CSRF filter:

	<http ...>
	  ...
	  <custom-filter ref="statelessCsrfFilter" position="CSRF_FILTER" />
	  ...
	</http>

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

### `jrestfulSecProps`

#### The CSRF parameters

 - `csrf.headerName`: The request header that will be read for CSRF protection.
 - `csrf.cookieName`: The cookie that will be read for CSRF protection.

The CSRF protection is implemented as explained in [Robbert van Waveren article](http://blog.jdriven.com/2014/10/stateless-spring-security-part-1-stateless-csrf-protection/).

#### Example

    csrf.headerName=X-CSRF-TOKEN
    csrf.cookieName=MYWEBSITE-CSRF-TOKEN

## What does `core` provide?

TBC
