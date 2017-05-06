import java.util.List;

import japa.parser.ast.body.Parameter;
import japa.parser.ast.stmt.BlockStmt;

public class ClassMethod {

	public char accessModifier;
	public String methodName;
	public String returnType;
	List<Parameter> parameters;
	public BlockStmt methodBody;
	public boolean isConstructor;
	public boolean isGetterSetter;
	public boolean isStatic;
	public boolean isAbstract;
	
	public ClassMethod(){
		this.accessModifier = 'a';
		this.methodName = null;
		this.returnType = null;
		this.parameters = null;
		this.methodBody = null;
		this.isConstructor = false;
		this.isGetterSetter = false;
		this.isStatic = false;
		this.isAbstract = false;
	}
}
