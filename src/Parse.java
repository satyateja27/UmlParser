import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;


public class Parse {

	private String sourcePath;
	private List<ClassBody> classes;
	private ClassBody classBody;
	private ClassField classField;
	private ClassMethod classMethod;
	private List<String> classNames;
	public static HashMap<String, List<String>> associationMap;
	
	public Parse(String sourcePath){
		this.sourcePath = sourcePath;
		this.classes = new ArrayList<ClassBody>();
		this.classNames = new ArrayList<String>();
		associationMap = new HashMap<String, List<String>>();
	}
	
	public List<ClassBody> start(){
		File folder = new File(sourcePath);
		File[] files = folder.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.endsWith(".java");
			}
		});
		for(File file : files){
			try {
				CompilationUnit cu = JavaParser.parse(file);
				classBody = new ClassBody();
				parseClassOrInterface(cu);
				parseBody(cu);
				checkGetterSetter();
				classes.add(classBody);
				classNames.add(classBody.className);
			}catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		checkAssociation();
		return classes;
	}
	
	private void parseClassOrInterface(CompilationUnit cu){
		List<TypeDeclaration> types = cu.getTypes();
		if(types != null){
			for(TypeDeclaration type : types){
				if(type instanceof ClassOrInterfaceDeclaration){
					if(((ClassOrInterfaceDeclaration)type).isInterface()){
						classBody.isInterface = true;
					}
					classBody.className = type.getName();
					classBody.extendsContent = ((ClassOrInterfaceDeclaration) type).getExtends();
					classBody.implementsContent = ((ClassOrInterfaceDeclaration) type).getImplements();
				}
			}
		}
	}
	
	private void parseBody(CompilationUnit cu){
		List<TypeDeclaration> types = cu.getTypes();
		if(types != null){
			for(TypeDeclaration type: types){
				List<BodyDeclaration> bodyDeclaration = type.getMembers();
				for(BodyDeclaration body : bodyDeclaration){
					if(body instanceof FieldDeclaration){
						parseField((FieldDeclaration)body);
					}else if(body instanceof MethodDeclaration){
						parseMethod((MethodDeclaration)body);
					}else if(body instanceof ConstructorDeclaration){
						parseConstructor((ConstructorDeclaration)body);
					}
				}
			}
		}
	}
	
	private void parseField(FieldDeclaration field){
		classField = new ClassField();
		String name = null;
		for(VariableDeclarator variableName : field.getVariables()){
			name = variableName.getId().getName();
		}
		if(field.getModifiers() == 1){
			classField.accessModifier = '+';
		}
		if(field.getModifiers() == 2){
			classField.accessModifier = '-';
		}
		classField.fieldName = name;
		classField.returnType = field.getType().toString();
		if(classField.returnType.contains("[]")){
			classField.hasMultiplicity = true;
		}
		for(int i=0; i<classField.returnType.length(); i++){
			if(classField.returnType.charAt(i) == '<'){
				classField.hasMultiplicity = true;
				break;
			}
		}
		classBody.fields.add(classField);
	}
	
	private void parseMethod(MethodDeclaration method){
		classMethod = new ClassMethod();
		classMethod.methodName = method.getName();
		classMethod.returnType = method.getType().toString();
		classMethod.parameters = method.getParameters();
		System.out.println(method.getModifiers());
		if(method.getModifiers() == 1){
			classMethod.accessModifier = '+';
		}
		if(method.getModifiers() == 9){
			classMethod.accessModifier = '+';
			classMethod.isStatic = true;
		}
		classBody.methods.add(classMethod);
	}
	
	private void parseConstructor(ConstructorDeclaration constructor){
		classMethod = new ClassMethod();
		classMethod.accessModifier = '+';
		classMethod.methodName = constructor.getName();
		classMethod.parameters = constructor.getParameters();
		classMethod.isConstructor = true;
		classBody.methods.add(classMethod);
	}
	
	private void checkGetterSetter(){
		for(ClassField field : classBody.fields){
			for(ClassMethod method : classBody.methods){
				if(method.methodName.substring(0, 3).equals("get")){
					if(field.fieldName.contains(method.methodName.substring(3).toLowerCase())){
						field.hasGetter = true;
					}
				}
				if(method.methodName.substring(0, 3).equals("set")){
					if(field.fieldName.contains(method.methodName.substring(3).toLowerCase())){
						field.hasSetter = true;
					}
				}
			}
		}
		updateGetterSetter();
	}
	
	private void updateGetterSetter(){
		for(ClassField field : classBody.fields){
			if(field.hasGetter && field.hasSetter){
				field.accessModifier = '+';
				for(ClassMethod method : classBody.methods){
					if(field.fieldName.equals(method.methodName.substring(3).toLowerCase())){
						method.isGetterSetter = true;
					}
				}
			}
		}
	}
	
	private void checkAssociation(){
		for(ClassBody classBody : classes){
			List<String> list = new ArrayList<String>();
			for(ClassField field: classBody.fields){
				String returnType = field.returnType;
				if(returnType.contains("[]")){
					field.hasMultiplicity = true;
					returnType = returnType.substring(0, returnType.indexOf('['));
				}
				if(returnType.contains("<")){
					field.hasMultiplicity = true;
					int start = 0, end = 0;
					for(int i=0; i<returnType.length(); i++){
						if(returnType.charAt(i) == '<'){ start = i+1; }
						if(returnType.charAt(i) == '>'){ end = i; }
					}
					returnType = returnType.substring(start, end);
				}
				if(classNames.contains(returnType)){
					list.add(returnType);
					field.hasAssociation = true;
				}
			}
			associationMap.put(classBody.className, list);
		}
		cleanAssociationMap();
	}
	
	private void cleanAssociationMap(){
		for(ClassBody classBody : classes){
			for(String name : associationMap.get(classBody.className)){
				if(associationMap.get(name).contains(classBody.className)){
					associationMap.get(name).remove(classBody.className);
				}
			}
		}
	}
	
}
