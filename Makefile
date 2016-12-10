indy:
	javac *.java
jar: indy
	jar cvfie Indy.jar Indy.App *.class
clean:
	rm -f *.class
	rm -f *.jar
