# Camel REST server standalone
Start a Camel- based REST server from command line.

Some of the Camel examples seem to have problems running via `mvn camel:run`
and documentation how to run Camel via `java -jar` is difficult to come by.

This project aims to provide an example that combines Camel, blueprint, cxf,
JSON etc. into a bundle that can be run inside of a Heroku container.

## Quickstart

Before you do anything else:

	mvn clean install

Regardless of how you start the server, you can test it for example:

	wget http://localhost:8080/greeting/hello/spongebob --header="Accept: application/json"

This endpoint supports both XML and JSON.

You could also try the RESTTest.java unit test in `serviceA/` (in Eclipse or whatever).


### maven

Switch into the `serviceA/` directory and start camel:

	mvn camel:run

Note that this requires a couple of implicit dependencies to be listed in `serviceA`.
Normally, a container such as Karaf would provide them.

See the corresponding section in `serviceA/pom.xml`

### java -jar

Well, not quite `java -jar`. The maven plugin `appassembler` is used to build
a script that will build & execute the correct command.

The plugin will copy all maven dependencies (including transitive) into a
directory and add them all to the classpath.

It is not executed by default, though. Instead you have to activate the maven
profile by means of setting an environment variable:

	cd dist/
	export BUNDLE_STUFF="true"
	mvn clean install
	
This will create a new directory `target/appassembler/` that holds all dependencies
and a couple of scripts that can now be executed to start the server:

	target/appassembler/bin/app

### Heroku

1. Create a new app in Heroku
2. Fork this repo into your Github account
3. In your app, under `Settings` click `Reveal Config Vars` and add:
  1. `BUNDLE_STUFF` with value `true`
  2. `HEROKU_BUILD` with value `true` 
4. Link your Heroku app to your Github account & select the forked repo

Test your REST server, e.g. with the `RESTTest` test.

Trouble is that Heroku exports the port the server is supposed to bind to via `$PORT`.
For some reason, using this value in `blueprint.xml`s doesn't work.

Luckily, both maven and blueprint use the same variable format: `${port}`. During the build
on the Heroku server, maven will filter the blueprint file and replace the port with `${PORT}`.

During normal operation, this will not happen & blueprint will manage the variable's value. All this
is defined in `serviceA/pom.xml`.

Also note the `Procfile`, which points to the script built by the `appassembler` plugin.