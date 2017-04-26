import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import japa.parser.ast.body.Parameter;
import japa.parser.ast.type.ClassOrInterfaceType;
import net.sourceforge.plantuml.SourceStringReader;

public class DrawUml {

	private String outputFile;
	private List<ClassBody> classes;
	private StringBuilder cb = new StringBuilder(); //Class Builder
	private StringBuilder eib = new StringBuilder(); //Extends Implements Builder
	private StringBuilder ab = new StringBuilder(); //Association Builder
	private StringBuilder db = new StringBuilder(); //Dependency Builder
	
	public DrawUml(String outputFile){
		this.outputFile = outputFile;
	}
	
	/**
	 * UML Diagram Generation starts from here
	 * @param classes
	 * @author satya
	 */
	public void generateUml(List<ClassBody> classes){
		this.classes = classes;
		cb.append("@startuml\n");
		cb.append("skinparam classAttributeIconSize 0\n");
		for( ClassBody classBody : classes){
			if(classBody.isInterface){
				cb.append("interface ").append(classBody.className).append(" <<interface>> {\n");
			}else{
				cb.append("class ").append(classBody.className).append(" {\n");
			}
			addFieldString(classBody);
			addMethodString(classBody);
			addExtendsImplementString(classBody);
			cb.append("}\n");
		}
		addAssociationString();
		addDependencyString();
		cb.append(eib.toString());
		cb.append(ab.toString());
		cb.append(db.toString());
		cb.append("@enduml");
		System.out.println(cb.toString());
		diagram(cb.toString());
	}
	
	
	/**
	 * String required for fields or attributes generation is appended here
	 * @param classBody
	 * @author satya
	 */
	private void addFieldString(ClassBody classBody){
		for(ClassField field : classBody.fields){
			if(!field.hasAssociation){
				if(field.accessModifier == '+' || field.accessModifier == '-'){
					cb.append(field.isStatic ? "{static} " : "");
					cb.append(field.accessModifier).append(" ").append(field.fieldName).append(" : ").append(field.returnType).append("\n");
				}
			}
		}
	}
	
	
	/**
	 * String required for methods or operation generation is appended here
	 * @param classBody
	 * @author satya
	 */
	private void addMethodString(ClassBody classBody){
		for(ClassMethod method : classBody.methods){
			if(method.accessModifier == '+' && !method.isGetterSetter){
				cb.append(method.isStatic ? "{static} " : "");
				cb.append(method.isAbstract ? "{abstract} " : "");
				cb.append("+ ").append(method.methodName).append("(");
				int count = 1;
				if(method.parameters != null){
					for(Parameter p : method.parameters){
						if(count>1 && count<=method.parameters.size()){
							cb.append(", ");
						}
						cb.append(p.getId().getName()).append(" : ").append(p.getType());
						count ++;
					}
				}
				if(method.isConstructor){
					cb.append(")").append("\n");
				}else{
					cb.append(") : ").append(method.returnType).append("\n");
				}
			}
		}
	}
	
	
	/**
	 * String required for extends and implements relationship generation is appended here
	 * @param classBody
	 * @author satya
	 */
	private void addExtendsImplementString(ClassBody classBody){
		if(classBody.extendsContent != null){
			for(ClassOrInterfaceType content : classBody.extendsContent){
				eib.append(content).append(" <|-- ").append(classBody.className).append("\n");
			}
		}
		if(classBody.implementsContent != null){
			for(ClassOrInterfaceType content : classBody.implementsContent){
				eib.append(content).append(" <|.. ").append(classBody.className).append("\n");
			}
		}
	}
	
	
	/**
	 * String required for association relationship generation is appended here
	 * @author satya
	 */
	private void addAssociationString(){
		for(Map.Entry<String, List<String>> entry : Parse.associationMap.entrySet()){
			if(entry.getValue().size() > 0){
				for(String name : entry.getValue()){
					ab.append(entry.getKey());
					ab.append(getMultiplicity(name, entry.getKey()) ? " \"*\" " : " \"1\" ");
					ab.append("--");
					ab.append(getMultiplicity(entry.getKey(), name) ? " \"*\" " : " \"1\" ");
					ab.append(name).append("\n");
				}
			}
		}
	}
	
	
	/**
	 * Multiplicity check for fields starts here
	 * @param sourceClass
	 * @param targetClass
	 * @author satya
	 */
	private boolean getMultiplicity(String sourceClass, String targetClass){
		for(ClassBody classBody : classes){
			if(classBody.className.equals(sourceClass)){
				for(ClassField field : classBody.fields){
					String returnType = field.returnType;
					if(returnType.contains("[]")){
						returnType = returnType.substring(0, returnType.indexOf('['));
					}
					if(returnType.contains("<")){
						int start = 0, end = 0;
						for(int i=0; i<returnType.length(); i++){
							if(returnType.charAt(i) == '<'){ start = i+1; }
							if(returnType.charAt(i) == '>'){ end = i; }
						}
						returnType = returnType.substring(start, end);
					}
					if(returnType.equals(targetClass)){
						return field.hasMultiplicity;
					}
				}
			}
		}
		return false;
	}
	
	
	/**
	 * String required for dependency relationship generation is appended here
	 * @author satya
	 */
	private void addDependencyString(){
		for(Map.Entry<String, List<String>> entry : Parse.dependencyMap.entrySet()){
			if(entry.getValue().size() > 0){
				for(String name : entry.getValue()){
					db.append(entry.getKey()).append(" ..> ").append(name).append("\n");
				}
			}
		}
	}
	
	
	/**
	 * Output diagram generation starts here
	 * @param source
	 * @author satya
	 */
	private void diagram(String source){
		SourceStringReader reader = new SourceStringReader(source); // Read from the source String
        String desc = null;
		try {
			OutputStream png = new FileOutputStream(outputFile); // Create Target File for Output
			desc = reader.generateImage(png); // Write the image generation result
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
