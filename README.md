# SmartGWT Jakarta Example

This project serves as an example to upgrade a SmartGWT project to the Jakarta EE namespace. The process uses
the [Eclipse Transformer](https://projects.eclipse.org/projects/technology.transformer) to handle the Jakarta namespace renaming. Also with this example enables
the following:

- Java 21
- Jetty 11
- Servlet 5.0
- Spring 6
- Project Lombok annotations for GWT client code
- IntelliJ run configurations

This project is based on the `archetype-smartgwt-quickstart-relogin` archetype
from https://smartclient.com/smartgwtee-release/javadoc/com/smartgwt/client/docs/MavenSupport.html

## Prerequisites

Variations on the following are possible, but archetypes & sample projects were created with the following environment/s in mind:

- [Java 21](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html) (Amazon Corretto used for this project)

- [Google Web Toolkit](https://www.gwtproject.org/download.html) version 2.10.0 (download isn't necessary, handled via Maven)

- [Apache Maven](https://maven.apache.org/download.cgi) version 3.5.0 or later

### Initialization

1. Take the `smartgwt-setup.sh` script file from the root of this project and place it in a new directory (can't be in a project directory with an existing
   pom.xml file).
2. Modify `smartgwt-setup.sh` to suit your needs. Required properties to change:
    1. `[REPOSITORY URL]` - Your Maven repository URL for deploying
    2. `[REPOSITORY ID]` - Your Maven repository ID for credentials supplied in `settings.xml`
3. Modify pom.xml with the SmartGWT version used above, and also update `[REPOSITORY URL]` and `[REPOSITORY ID]` with your information.

## Usage: Apache Maven

### Command line tools

1. Navigate to the project directory

        cd smartgwt-sample

2. Start the GWT codeserver.

        mvn gwt:codeserver -am -pl smartgwt-sample-client

3. At another command line from the same directory, start the servlet container

       mvn jetty:run -Pdev -pl smartgwt-sample-server

4. Wait for each process to indicate that it's ready, and then load the app in your browser at <http://localhost:8080>
