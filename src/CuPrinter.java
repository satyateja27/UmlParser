import java.io.*;
import japa.parser.*;
import japa.parser.ast.*;
public class CuPrinter {

	public static void main(String[] args) throws Exception {
		File file = new File("C:/Users/Pothuru/workspace/UmlParser/src/Test.java");
		// creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(file);

        // parse the file
        CompilationUnit cu = JavaParser.parse(in);

        // prints the resulting compilation unit to default system output
        System.out.println(cu.toString());
	}

}
