# jrestful-seo

`jrestful-seo` improves your SEO by generating a sitemap via JAXB and prerendering pages for bots via PhantomJS and Selenium.

## How to install it?

**First, install the [`jrestful-core` module](https://github.com/jrestful/server/tree/master/core).**

### In your pom.xml

Add the dependency:

```xml
<dependencies>

  <dependency>
    <groupId>org.jrestful</groupId>
    <artifactId>jrestful-seo</artifactId>
    <version>${jrestful.version}</version>
  </dependency>

</dependencies>
```

### In your web.xml

Add the context parameter:

```xml
<context-param>
  <description>The Spring resource location of the SEO properties</description>
  <param-name>jrestfulSeoProps</param-name>
  <!-- Example: -->
  <param-value>WEB-INF/seo.properties</param-value>
</context-param>
```

### In your contexts

No placeholder is defined by jrestful, but properties beans are available:

 - `jrestfulSeoProps` properties are available in a bean named `seoProps`

You can then register placeholders based on these beans if needed:

```xml
<context:property-placeholder properties-ref="seoProps" ignore-unresolvable="true" />
```

### In your URL rewriting

As the URL mapping is done client-side by AngularJS, the server has to forward those URL to `/`. This can easily get done with [Tuckey UrlRewriteFilter](http://tuckey.org/urlrewrite/), by adding a file `urlrewrite.xml` in your `WEB-INF` folder. Example:

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE urlrewrite PUBLIC "-//tuckey.org//DTD UrlRewrite 4.0//EN" "http://www.tuckey.org/res/dtds/urlrewrite4.0.dtd">
<urlrewrite>
  
  <rule>
    <from>^/home$</from>
    <to>/</to>
  </rule>
  
  <rule>
  	<from>^/article/.*$</from>
  	<to>/</to>
  </rule>

</urlrewrite>
```

The prerenderer has to know the original URL: it will look for it in a `prerenderUri` request attribute. To automatically add this request attribute, simply update your URL rewriting as follow:

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE urlrewrite PUBLIC "-//tuckey.org//DTD UrlRewrite 4.0//EN" "http://www.tuckey.org/res/dtds/urlrewrite4.0.dtd">
<urlrewrite>
  
  <rule>
    <from>^/home$</from>
    <set name="prerenderUri">$0</set>
    <to>/</to>
  </rule>
  
  <rule>
    <from>^/article/.*$</from>
    <set name="prerenderUri">$0</set>
    <to>/</to>
  </rule>

</urlrewrite>
```

### In your code (optional)

Implement `org.jrestful.web.seo.sitemap.SitemapBuilder`, and register it as a Spring component.

If not provided, no sitemap will be generated.

#### Example

```java
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
```

## What is expected in the properties files?

### `jrestfulAppProps`

#### The application paths

 - `app.url`: The application base URL (used to generate the sitemap).
 - `app.dir`: The application directory (where writing must be allowed).

`seo` generates a sitemap in `${app.dir}/resources/sitemap.xml` on server startup and every day at 5 am. Spring MVC must be able to serve that file when requested. To do so, add the following tag in your Spring MVC servlet context:

```xml
<mvc:resources
  mapping="/**"
  location="file:${app.dir}/resources/" />
```

`seo` also caches prerendered pages for one day in `${app.dir}/prerendered`.

#### Example

```properties
app.url=http://www.domain.tld
app.dir=/local/myapp/data/1.3.9
```

### `jrestfulSeoProps`

#### The PhantomJS parameters (optional)

 - `phantomjs.path`: The path to the PhantomJS executable.
 - `phantomjs.bind`: The IP/port PhantomJS GhostDriver will bind on. It can be either a single port (e.g. `23456`) or an IP/port (e.g. `192.168.1.42:23456`).

`seo` uses PhantomJS to prerender pages when bots are requesting them, so that they can see the fully loaded content. While PhantomJS default binds on localhost, some servers do not allow it (e.g. [OpenShift](https://www.openshift.com/)). A patched version of PhantomJS can be found at the root directory of `seo` (see [this post](http://stackoverflow.com/q/30506496/1225328) on Stack Overflow), which allows specifying the IP to bind on (hence the possibility of giving the IP for the `phantomjs.bind` property).

If not provided, prerendering won't be done.

#### Example

```properties
phantomjs.path=/local/myapp/phantomjs-1.9.8-patched/phantomjs
phantomjs.bind=192.168.1.42:23456
```
