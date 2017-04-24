import java.util.List;

import japa.parser.ast.body.Parameter;

public class ClassMethod {

	public char accessModifier;
	public String methodName;
	public String returnType;
	List<Parameter> parameters;
	public boolean isConstructor;
	public boolean isGetterSetter;
	public boolean isStatic;
	
	public ClassMethod(){
		this.accessModifier = 'a';
		this.methodName = null;
		this.returnType = null;
		this.parameters = null;
		this.isConstructor = false;
		this.isGetterSetter = false;
		this.isStatic = false;
	}
}
