# CommuteCompanion
##### A product for the project course **Large-scale Software Engineering** (ETSN05) at Lund University.
###### &nbsp;&nbsp;&nbsp;&nbsp; By Group 1

This README provides a *how-to* for downloading, compiling, and running the application.

### Prerequisites
- An IDE such as IntelliJ or Eclipse.
- git.
- Maven (or Maven plugins for IDE - usually included).


#### Cloning the repository
Open CMD or terminal in an appropriate folder.

Enter the command:
`git clone https://github.com/CommuteCompanion/etsn05-project.git`

You can also avoid using git by simply downloading a zip from the project's home page on GitHub, or downloading a specific release from the "Releases" page.


#### Importing into dev-environment
We recommend using IntelliJ or Eclipse as they both offer Maven integration. To import the downloaded project follow these general steps:
- Import new project as Maven.
- Choose the folder etsn05-project as root. It contains the pom.xml that Maven needs.
- Use JDK 1.8
- Project name = "etsn05-project"
- If prompted, make sure "group id" = base, and "artefact name" = etsn05-project.

#### Building
Running `mvn package` will build the project and also run back-end tests to make sure the server is able to launch and operate.

#### Executing
Through an IDE you can just run `BaseServer.main()`.

*OR:* 

Maven will put an executable jar (with dependencies) in the folder "target". This can be used to run the server through CLI using `java -jar base-server-jar-with-dependencies` (also provides live logging in the console).

One can also start the server by double-clicking the .jar (not recommended, the program will run quietly in the background).

**Please note** that on first run, the database needs to be initialized. Either by running `CreateSchema.main()`, or by CLI:

`java -jar database-initializer-jar-with-dependencies`

You may also run this any time you want the database to be reset.

#### Testing
`mvn test` will run back-end tests.

IntelliJ and Eclipse supports a simple right-click on the test folder followed by clicking "Run all tests". Through these IDE's you can also run individual tests (both test-classes and methods).

To run front-end tests, you must start the server and visit "localhost:8080/specs.html" (assuming you did not change url).

#### Development
A video explaining the setup and contribution procedure is available here: https://www.youtube.com/watch?v=uCdUykhIhFM (Swedish)

A video explaining the general architecture and design of the system: https://www.youtube.com/watch?v=T3RKcLt2H_4 (Swedish)

`master` is our release branch. This should be the versioned branch which we release for production.

`develop` is our development branch. Ideally, it should be treated as if its ready for deployment to customer/production server at any time.

All bugfixes and features are done in separated branches. When ready for review; a pull request is made from said branch into develop. 

#### Troubleshooting
 - The project uses Bootstrap and sass-files to offer an esthetically nice look to the website. This means that before the server can be launched for the first time (or after changing a .scss-file), one needs to run a sass-compiler. This is integrated into maven, so running `mvn package` is fine. If only css files need to be updated (e.g. no need to run tests and rebuild java classes), simply run the `mvn sass:update-stylesheets` from the sass-plugin.
 - If there is no database installed, CreateSchema.java will provide one when executing the server. After that, it can be very easy to forget that you have to manually run `CreateSchema.main()` to reset it. You may want to do that after changing the database.
 - Emails include a link to the website hosted for global access. This website will not reflect any drives, users, or other things changed in a development launch (i.e. running it from localhost:8080).