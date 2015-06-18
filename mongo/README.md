# mongo

`mongo` allows you to easily plug your application to a MongoDB database via Spring Data.

## How to install it?

**First, install the [`core` module](https://github.com/jrestful/server/tree/master/core).**

### In your pom.xml

Add the dependency:
    
    <dependencies>
    
      <dependency>
        <groupId>org.jrestful</groupId>
        <artifactId>mongo</artifactId>
        <version>${jrestful.version}</version>
      </dependency>
    
    </dependencies>

### In your web.xml

Add the context parameter:

    <context-param>
      <description>The Spring resource location of the MongoDB properties</description>
      <param-name>jrestfulMongoProps</param-name>
      <!-- Example: -->
      <param-value>classpath:mongo.properties</param-value>
    </context-param>

## What is expected in the properties files?

### `jrestfulMongoProps`

#### The MongoDB parameters

 - `mongo.host`
 - `mongo.port`
 - `mongo.dbname`
 - `mongo.username`
 - `mongo.password`
 - `mongo.connectionsPerHost`
 - `mongo.threadsAllowedToBlockForConnectionMultiplier`
 - `mongo.connectTimeout`
 - `mongo.maxWaitTime`
 - `mongo.autoConnectRetry`
 - `mongo.socketKeepAlive`
 - `mongo.socketTimeout`
 - `mongo.slaveOk`
 - `mongo.writeNumber`
 - `mongo.writeTimeout`
 - `mongo.writeFsync`

For more details, see [the Spring Data documentation](http://docs.spring.io/spring-data/data-mongo/docs/1.6.2.RELEASE/reference/html/).

#### Example

	mongo.host=localhost
	mongo.port=27017
	mongo.dbname=myapp
	mongo.username=myapp
	mongo.password=myapp
	
	mongo.connectionsPerHost=8
	mongo.threadsAllowedToBlockForConnectionMultiplier=4
	mongo.connectTimeout=1000
	mongo.maxWaitTime=1500
	mongo.autoConnectRetry=true
	mongo.socketKeepAlive=true
	mongo.socketTimeout=1500
	mongo.slaveOk=true
	mongo.writeNumber=1
	mongo.writeTimeout=0
	mongo.writeFsync=true

## What does `mongo` provide?

### For your documents

`org.jrestful.data.documents.support.GenericDocument` is an abstract class for your documents.

`org.jrestful.data.documents.support.sequence.GenericSequencedDocument` is an abstract class for your documents that need an auto-incrementing sequence.

### For your repositories

`org.jrestful.data.repositories.support.GenericDocumentRepository` and `org.jrestful.data.repositories.support.GenericDocumentRepositoryImpl` are an interface and an abstract class for your documents repositories.

`org.jrestful.data.repositories.support.sequence.GenericSequencedDocumentRepository` and `org.jrestful.data.repositories.support.sequence.GenericSequencedDocumentRepositoryImpl` are an interface and an abstract class for your sequenced documents repositories.

### For your services

`org.jrestful.business.support.GenericDocumentService` and `org.jrestful.business.support.GenericDocumentServiceImpl` are an interface and an abstract class for your documents services.

`org.jrestful.business.support.sequence.GenericSequencedDocumentService` and `org.jrestful.business.support.sequence.GenericSequencedDocumentServiceImpl` are an interface and an abstract class for your sequenced documents services.

### For your controllers

`org.jrestful.web.controllers.rest.support.GenericDocumentRestController` is an abstract class for your documents REST controllers. Be careful: override the methods if your need authorizations.
