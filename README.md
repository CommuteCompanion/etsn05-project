# ETSN05-project


### Prerequisites
git, maven, and a development environment (IntelliJ or Eclipse).

#### Cloning the repository
Open CMD or terminal in an appropriate folder.
Enter the command:
`git clone https://github.com/antondelorme/etsn05-project.git`

#### Importing into dev-environment
I have tested this setup with both Eclipse and IntelliJ and both of them should work fine with the same general steps:
- Import new project as Maven.
- Choose the folder etsn05-project as root. It contains the pom.xml that Maven needs.
- Use JDK 1.8
- Project name = "etsn05-project"
- If prompted, make sure "group id" = base, and "artefact name" = etsn05-project.

Test the imported project by running BaseServer.java and going to localhost:8080 in a browser.
