import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.ClassOrInterfaceType;
import net.sourceforge.plantuml.SourceStringReader;


public class Parse {

	private String sourcePath;
	private String outputFile;
	private StringBuilder cb = new StringBuilder();
	private StringBuilder rb = new StringBuilder();
	private LinkedHashMap<String, CompilationUnit> classMap = new LinkedHashMap<String, CompilationUnit>();

	public Parse(String sourcePath, String outputFile){
		this.sourcePath = sourcePath;
		this.outputFile = outputFile;
	}

	public void start(){
		File folder = new File(sourcePath);
		File[] files = folder.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.endsWith(".java");
			}
		});
		cb.append("@startuml\n");
		cb.append("skinparam classAttributeIconSize 0\n");
		for(File file : files){
			System.out.println(file.getName());
			try {
				CompilationUnit cu = JavaParser.parse(file);
				List<TypeDeclaration> types = cu.getTypes();
				if(types != null){
					for(TypeDeclaration type : types){
						if(type instanceof ClassOrInterfaceDeclaration){
							classMap.put(type.getName(), cu);
						}
					}
				}
			}catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		for(Map.Entry<String, CompilationUnit> entry : classMap.entrySet()){
			parseClassOrInterface(entry.getValue());
			parseBody(entry.getValue(), entry.getKey());
			parseImplementsExtends(entry.getValue());
		}
//		cb.append(rb.toString());
		cb.append("@enduml");
		System.out.println(cb.toString());
//		diagram(cb.toString());
	}

//	Identifying Class or Interface starts here
	private void parseClassOrInterface(CompilationUnit cu){
		List<TypeDeclaration> types = cu.getTypes();
		if(types != null){
			for(TypeDeclaration type : types){
				if(type instanceof ClassOrInterfaceDeclaration){
					if(((ClassOrInterfaceDeclaration)type).isInterface()){
						cb.append("interface ").append(type.getName()).append(" <<interface>> {\n");
					}else{
						cb.append("class ").append(type.getName()).append(" {\n");
					}
				}
			}
		}
	}

//	parsing Class or Interface Body starts here
	private void parseBody(CompilationUnit cu, String currentClass){
		List<TypeDeclaration> types = cu.getTypes();
		if(types != null){
			for(TypeDeclaration type: types){
				List<BodyDeclaration> bodyDeclaration = type.getMembers();
				for(BodyDeclaration body : bodyDeclaration){
					if(body instanceof FieldDeclaration){
						parseField((FieldDeclaration)body, currentClass);
					}else if(body instanceof MethodDeclaration){
						parseMethod((MethodDeclaration)body);
					}else if(body instanceof ConstructorDeclaration){
						parseConstructor((ConstructorDeclaration)body);
					}
				}
			}
		}
		cb.append("}\n");
	}

//	parsing Fields starts here
//	ignoring package & protected scope for fields
	private void parseField(FieldDeclaration field, String currentClass){
		String name = null;
		for(VariableDeclarator variableName : field.getVariables()){
			name = variableName.getId().getName();
		}
		char modifier = (field.getModifiers() == 2) ? '-' : '+';
		cb.append(modifier).append(" ").append(name).append(" : ").append(field.getType()).append("\n");
	}

//	parsing Method starts here
//	ignoring private, protected & package scope for methods
	private void parseMethod(MethodDeclaration body){
		cb.append("+ ").append(body.getName()).append("(");
		int count = 1;
		if(body.getParameters() != null){
			for(Parameter p : body.getParameters()){
				if(count>1 && count<=body.getParameters().size()){
					cb.append(", ");
				}
				cb.append(p.getId().getName()).append(" : ").append(p.getType());
				count ++;
			}
		}
		cb.append(") : ").append(body.getType()).append("\n");
	}


//	UML diagram generation starts here
	private void diagram(String source){
		SourceStringReader reader = new SourceStringReader(source); // Read from the source String
        String desc = null;
		try {
			OutputStream png = new FileOutputStream(outputFile); // Create Target File for Output
			desc = reader.generateImage(png); // Write the first image to "png"
		} catch (IOException e) {
			e.printStackTrace();
		}
        if(desc != null){
        	System.out.println("UML Successfully Rendered");
        }else{
        	System.out.println("UML Generation Unsuccessful");
        }
	}
}
