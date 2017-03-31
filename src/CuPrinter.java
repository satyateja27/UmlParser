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

        //System.out.println("****UML Souce****");

				diagram(String source){

					SourceStringReader reader = new SourceStringReader(source); // Read from the source String

	        OutputStream png = new FileOutputStream("/Users/satya/Documents/Workspace/UmlParser/Output.png"); // Create Target File for Output

	        String desc = null; // Write the first image to "png"

					try {
						OutputStream png = new FileOutputStream("/Users/satya/Documents/Workspace/UmlParser/Output.png"); // Create Target File for Output
						desc = reader.generateImage(png); // Write the first image to "png"
					} catch (IOException e) {
						e.printStackTrace();
					}

	        System.out.println("****Output Message****");
	        if(desc != null){
	        	System.out.println("UML Successfully Rendered");
	        }else{
	        	System.out.println("UML Generation Unsuccessful");
	        }
				}


	}

}
