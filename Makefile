indy:
	javac *.java
	jar cvfe Indy.jar Indy.App *.class
clean:
	rm -f *.class
	rm -f *.jar
