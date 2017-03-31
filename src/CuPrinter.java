import java.io.*;
import japa.parser.*;
import japa.parser.ast.*;
import net.sourceforge.plantuml.SourceStringReader;
public class CuPrinter {

	public static void main(String[] args) throws Exception {
		File file = new File("/Users/satya/Documents/Workspace/UmlParser/src/Test.java");

		// creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(file);

        // parse the file
        CompilationUnit cu = JavaParser.parse(in);

        // prints the resulting compilation unit to default system output
        System.out.println(cu.toString());

        String source = "@startuml\nskinparam classAttributeIconSize 0\n";
        source += "class Test{\n";
        source += "-hello : String\n";
        source += "+getHello() : String\n}\n";
        source += "@enduml\n";

        System.out.println("****UML Souce****");
        //System.out.println(source);

				diagram(String source){

				}


	}

}
