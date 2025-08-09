Clean && compile && run:
rm -rf bin
mkdir bin
javac -cp "lib/mysql-connector-j-9.4.0.jar" -d bin src/*.java
java -cp "bin:lib/mysql-connector-j-9.4.0.jar" Main
