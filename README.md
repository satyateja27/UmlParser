# UML Parser to generate UML Class and Sequence Diagrams

## About

This is a Java based project which generates the UML Class & Sequence diagrams by parsing the given Java source code.
This repository has two applications:
1. UML Class diagram generator
2. UML Sequence diagram generator


## Tools and libraries used

This project uses the following tools
1. **[Javaparser](http://javaparser.org/index.html)** Easy to understand and use, it gives Abstract Syntax Tree (AST) from java code. Parsed java class can be easily processed to generate the UML diagram.

2. **[PlantUML](http://plantuml.com/)** UML diagrams can be generated using simple and intuitive language used by PlantUML.

3. **[GraphViz](http://plantuml.com/graphviz-dot)** Works with PlantUML to generate diagrams.

4. **[AspectJ](https://eclipse.org/aspectj/doc/next/progguide/language-joinPoints.html)** Using AspectJ to parse the Java code and then create relevant grammar for PlantUML to generate the UML Sequence Diagram.


## How it works

**1. UML Class diagram generator**: The java files provided either directly or through the ZIP files are parsed using Javaparser for all the variables, methods, constructors, and interfaces. During parsing process, the code also creates the relationships between the classes. All the relationships and classes are stored in objects. Finally, grammar is created using these objects and given to PlantUML to generate the class diagram.

**2. UML Sequence diagram generator**: Uses AspectJ to understand when a method call is started and when it is ended. Pointcut is added to parse the input Java files. Code works fine for both folder path or ZIP file with java files. The output AspectJ parsing is used to create a grammar for the PlantUML, using this Sequence diagram is generated.

## Sample Screenshots of UML Diagrams generated

**1. Class Diagram**

![Class Diagram](https://raw.githubusercontent.com/satyateja27/UmlParser/master/Class-Diagram-Source/output%20images/output%205.png)



**2. Sequence Diagram**

![Sequence Diagram](https://raw.githubusercontent.com/satyateja27/UmlParser/master/Sequence-Diagram-Source/output%20images/output.png)
