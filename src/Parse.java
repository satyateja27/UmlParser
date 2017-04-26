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
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;


public class Parse {

	private String sourcePath;
	private List<ClassBody> classes;
	private ClassBody classBody;
	private ClassField classField;
	private ClassMethod classMethod;
	private List<String> classNames;
	private List<String> interfaceNames;
	public static HashMap<String, List<String>> associationMap;
	public static HashMap<String, List<String>> dependencyMap;
	
	public Parse(String sourcePath){
		this.sourcePath = sourcePath;
		this.classes = new ArrayList<ClassBody>();
		this.classNames = new ArrayList<String>();
		this.interfaceNames = new ArrayList<String>();
		associationMap = new HashMap<String, List<String>>();
		dependencyMap = new HashMap<String, List<String>>();
	}
	
	
	/**
	 * Parsing starts here
	 * @author satya
	 */
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
		checkDependency();
		return classes;
	}
	
	
	/**
	 * Class, Interface parsing starts here
	 * @param cu
	 * @author satya
	 */
	private void parseClassOrInterface(CompilationUnit cu){
		List<TypeDeclaration> types = cu.getTypes();
		if(types != null){
			for(TypeDeclaration type : types){
				if(type instanceof ClassOrInterfaceDeclaration){
					if(((ClassOrInterfaceDeclaration)type).isInterface()){
						classBody.isInterface = true;
						interfaceNames.add(type.getName().toString());
					}
					classBody.className = type.getName();
					classBody.extendsContent = ((ClassOrInterfaceDeclaration) type).getExtends();
					classBody.implementsContent = ((ClassOrInterfaceDeclaration) type).getImplements();
				}
			}
		}
	}
	
	
	/**
	 * Class, Interface Body parsing starts here
	 * @param cu
	 * @author satya
	 */
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
	
	
	/**
	 * Fields or Attributes parsing starts here
	 * @param field
	 * @author satya
	 */
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
		if(field.getModifiers() == 9){
			classField.accessModifier = '+';
			classField.isStatic = true;
		}
		if(field.getModifiers() == 10){
			classField.accessModifier = '-';
			classField.isStatic = true;
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
	
	
	/**
	 * Method or Operation parsing starts here
	 * @param method
	 * @author satya
	 */
	private void parseMethod(MethodDeclaration method){
		classMethod = new ClassMethod();
		classMethod.methodName = method.getName();
		classMethod.returnType = method.getType().toString();
		classMethod.parameters = method.getParameters();
		classMethod.methodBody = method.getBody();
		if(method.getModifiers() == 1){
			classMethod.accessModifier = '+';
		}
		if(method.getModifiers() == 9){
			classMethod.accessModifier = '+';
			classMethod.isStatic = true;
		}
		if(method.getModifiers() == 1025){
			classMethod.accessModifier = '+';
			classMethod.isAbstract = true;
		}
		if(method.getModifiers() == 1033){
			classMethod.accessModifier = '+';
			classMethod.isAbstract = true;
			classMethod.isStatic = true;
		}
		classBody.methods.add(classMethod);
	}
	
	
	/**
	 * Constructor parsing starts from here
	 * @param constructor
	 * @author satya
	 */
	private void parseConstructor(ConstructorDeclaration constructor){
		classMethod = new ClassMethod();
		classMethod.accessModifier = '+';
		classMethod.methodName = constructor.getName();
		classMethod.parameters = constructor.getParameters();
		classMethod.isConstructor = true;
		classBody.methods.add(classMethod);
	}
	
	
	/**
	 * Checks the availability of getters and setters methods for the fields or attributes 
	 * @author satya
	 */
	private void checkGetterSetter(){
		for(ClassField field : classBody.fields){
			for(ClassMethod method : classBody.methods){
				if(method.methodName.length() > 3){
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
		}
		updateGetterSetter();
	}
	
	
	/**
	 * Updates the access modifier to public for fields having public getters and setters
	 * @author satya
	 */
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
	
	
	/**
	 * Class Association relationship parsing starts from here
	 * @author satya
	 */
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
	
	
	/**
	 * Removing redundant association relationships
	 * @author satya
	 */
	private void cleanAssociationMap(){
		for(ClassBody classBody : classes){
			for(String name : associationMap.get(classBody.className)){
				if(associationMap.get(name).contains(classBody.className)){
					associationMap.get(name).remove(classBody.className);
				}
			}
		}
	}
	
	
	/**
	 * Class dependency relationship parsing starts from here
	 * Method Parameter level dependency parsing starts here
	 * @author satya
	 */
	private void checkDependency(){
		for(ClassBody classBody : classes){
			List<String> list = new ArrayList<String>();
			for(ClassMethod method: classBody.methods){
				if(method.parameters != null){
					for(Parameter param: method.parameters){
						String returnType = param.getType().toString();
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
						if(interfaceNames.contains(returnType) && !list.contains(returnType) && !classBody.isInterface){
							list.add(returnType);
						}
					}
				}
				checkMethodBodyDependency(method, list);
			}
			dependencyMap.put(classBody.className, list);
		}
	}
	
	
	/**
	 * Method Body level dependency parsing starts from here
	 * @param method
	 * @param list
	 * @author satya
	 */
	private void checkMethodBodyDependency(ClassMethod method, List<String> list){
		if(method.methodBody != null){
			List<Statement> statementList = method.methodBody.getStmts();
			if(statementList != null){
				for(Statement statement : statementList){
					if(statement instanceof ExpressionStmt){
						ExpressionStmt state = (ExpressionStmt)statement;
						if(state.getExpression() instanceof VariableDeclarationExpr){
							VariableDeclarationExpr expr = (VariableDeclarationExpr) state.getExpression();
							String returnType = expr.getType().toString();
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
							if(interfaceNames.contains(returnType) && !list.contains(returnType) && !classBody.isInterface){
								list.add(returnType);
							}
						}
					}
				}
			}
		}
	}
	
}
