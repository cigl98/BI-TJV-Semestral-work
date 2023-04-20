***Music rehearsal room system***

**Update** -  Added concurrency control - conditional PUT, by implementing strong ETag. All PUT requests must contain *If-Match* header with corresponding version of entity to update.

Written in Java and Spring framework.

System provides an evidence of music bands and it's players which use music rehearsal room. 

Main feature is the ability to display complete schedule of the room.
Every band, which wants to practise, must be registered, after then there's an option to reserve a free time of the rehearsal's room schedule.

**Instalation**

You need to run a database on your computer (there is a dependency in pom.xml for oracle jdbc) and add datasource into `application.properties`. It's a maven project, so you can just download the repository, 
compile the code and run main in Main class (src/main/java/cz/cvut/fit/cihlaond/Main.java). App runs on http://localhost:8080. Tested only on UBUNTU 18.04.

There is also queries.http file - it contains some basic http requests to test the application.
