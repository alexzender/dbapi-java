dbapi-java
==========

Database API for Java is a lightweight JPA provider implementation for Cassandra.
It can also be extended with pluggable modules for other NoSQL document-oriented databases as well.

Overview
------------

The NoSQL field is repidly evolving and changing nowadays. It isn't mature enough and neither standardized yet. 
This project may help to separate business logic and object model from the database interaction layer.
So that it's relatively easy to switch from one vendor to another or support both if any.
It becomes possible at certain abstraction overhead though.

Requirements
------------
* JDK 1.6 (http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Cassandra 1.1.5 (http://cassandra.apache.org/download/ or http://www.datastax.com/download/community)


Building  
------------

The standard build process with Maven is supported.

To build the project from sources and run tests please perform:

```
$ mvn package
```
To generate eclipse project files please do:

```
$ mvn eclipse:eclipse
```

Usage  
------------

The dbapi.api.DBAPI class is an entry point to the system. It provides access to the factory that can issue JPA entity managers.
To create a factory you need to specify a map with configuration parameters and the list of persistent entity classes to be managed:

```java
 		
 		Map<String, String> config = new HashMap<String, String>();
        config.put("db.type", "cassandra");
        config.put("plugins.cassandra.keyspace", "tests");
        config.put("plugins.cassandra.host", "localhost");
        config.put("plugins.cassandra.port", "9160");
        
        
        Set<Class<?>> entities = new HashSet<Class<?>>();
        entities.add(User.class);
        
        EntityManagerFactory factory = DBAPI.getFactory(config, entities);

        EntityManager em = factory.createEntityManager();

```
