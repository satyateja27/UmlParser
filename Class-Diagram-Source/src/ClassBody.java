import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.type.ClassOrInterfaceType;

public class ClassBody {

	public boolean isInterface;
	public String className;
	public List<ClassField> fields;
	public List<ClassMethod> methods;
	public List<ClassOrInterfaceType> extendsContent;
	public List<ClassOrInterfaceType> implementsContent;
	
	public ClassBody(){
		this.isInterface = false;
		this.className = null;
		this.fields = new ArrayList<ClassField>();
		this.methods = new ArrayList<ClassMethod>();
		this.extendsContent = null;
		this.implementsContent = null;
	}
}
