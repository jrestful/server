# seo

`seo` improves your SEO by generating a sitemap via JAXB and prerendering page for bots via PhantomJS through Selenium.

## How to install it?

**First, install the [`core` module](https://github.com/jrestful/server/tree/master/core).**

### In your pom.xml

Add the dependency:
    
    <dependencies>
    
      <dependency>
        <groupId>org.jrestful</groupId>
        <artifactId>seo</artifactId>
        <version>${jrestful.version}</version>
      </dependency>
    
    </dependencies>

### In your web.xml

Add the context parameter:

    <context-param>
      <description>The Spring resource location of the SEO properties</description>
      <param-name>jrestfulSeoProps</param-name>
      <!-- Example: -->
      <param-value>WEB-INF/seo.properties</param-value>
    </context-param>

### In your code

Implement `org.jrestful.web.seo.sitemap.SitemapBuilder`, and register it as a Spring component.

#### Example

	@Component
	public class MyAppSitemapBuilder implements SitemapBuilder {
	
	  @Override
	  public UrlEntries build(String appUrl) {
	    UrlEntries urlEntries = new UrlEntries();
	
	    UrlEntry homeEntry = new UrlEntry(appUrl);
	    homeEntry.setChangeFreq(ChangeFreq.DAILY);
	    homeEntry.setPriority(1f);
	    urlEntries.add(homeEntry);
	
	    return urlEntries;
	  }
	
	}

## What is expected in the properties files?

### `jrestfulAppProps`

#### The application paths

 - `app.url`: The application base URL (used to generate the sitemap).
 - `app.dir`: The application directory (where writing must be allowed).

`seo` generates a sitemap in `${app.dir}/resources/sitemap.xml` on server startup and every day at 5 am. Spring MVC must be able to serve that file when requested. To do so, add the following tag in your Spring MVC servlet context:

	<mvc:resources
	  mapping="/**"
	  location="file:${app.dir}/resources/" />

`seo` also caches prerendered pages for one day in `${app.dir}/prerendered`.

#### Example

    app.url=http://www.domain.tld
    app.dir=/local/myapp/data/1.3.9

### `jrestfulSeoProps`

#### The PhantomJS parameters

 - `phantomjs.path`: The path to the PhantomJS executable.
 - `phantomjs.bind`: The IP/port PhantomJS GhostDriver will bind on. It can be either a single port (e.g. `23456`) or an IP/port (e.g. `192.168.1.42:23456`).

`seo` uses PhantomJS to prerender pages when bots are requesting them, so that they can see the fully loaded content. While PhantomJS default binds on localhost, some servers do not allow it (e.g. [OpenShift](https://www.openshift.com/)). A patched version of PhantomJS can be found at the root directory of `seo` (see [this post](http://stackoverflow.com/q/30506496/1225328) on Stack Overflow), which allows specifying the IP to bind on (hence the possibility of giving the IP for the `phantomjs.bind` property).

#### Example

    phantomjs.path=/local/myapp/phantomjs-1.9.8-patched/phantomjs
    phantomjs.bind=192.168.1.42:23456

## What does `seo` provide?

TBC

