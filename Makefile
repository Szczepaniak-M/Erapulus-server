app:
	mvn clean install
	cp ./target/erapulus-server-1.0.0-SNAPSHOT.jar ./docker/app/erapulus-server.jar
	docker build . -t erapulus-server:latest

