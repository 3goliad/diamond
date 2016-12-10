indy:
	javac *.java
	jar cvfie Indy.jar Indy.App *.class
clean:
	rm -f *.class
	rm -f *.jar
