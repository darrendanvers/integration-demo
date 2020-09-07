# An Integration Demo

This is a sample programs that illustrates a few key patterns I like to follow 
when writing integrations. More details forthcoming.

### Overview

In the spirit of many demonstration applications, this program supports
a system that maintains the core database of a record store. It takes a file that contains
a list of albums and songs in JSON format, stores it in a staging database, parses it, and 
moves the data into the core database.

I built the application using [Spring Batch](https://spring.io/projects/spring-batch) as I
like the structure it provides to applications, is widely used, is easy to understand, and
a batch job allowed for a simple infrastructure. The main point of this integration, though, 
is not the code itself, but rather to highlight the tracking of data through the integration.

### Running the Application.

#### Docker Only

If you have Docker installed, the easiest way to just run the application is:

1. Open a terminal and navigate to the project's root directory.
2. Type in `docker-compose build`.
3. Type in `docker-compose up`.

This will construct a MySQL container and an application container. It
will then start up MySQL, build the schemas, load bootstrap data, and
run the integration.

#### Running the Source with the Database in a Docker Container

If you have Docker and Java 12+ installed (others versions may work, but I haven't tested them), 
you can run the database as a container and run the source locally. You will need to run the 
application with the *local* profile active.

1. Open a terminal and navigate to the project's root directory.
2. Type in `docker-compose up db`.
3. Open another terminal and navigate to the project's root directory.
4. Type in `./gradlew build`.
5. Type in `java -Dspring.profiles.active=local -jar ./build/libs/integration-demo-0.1.0.jar`.

#### No Docker
If you do not have Docker, you'll need to set up your own DB. The schemas are defined in *schema.sql*,
and the bootstrap data is in *data.sql*. The DDL and DML is all specific for MySQL. Connection
parameters are in *application-local.properties*.

The application may not necessarily work with other databases, but the SQL is not taking 
advantage of any vendor-specific extensions and should port to most databases fairly easily.