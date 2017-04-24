public class ClassField {

	public char accessModifier;
	public String fieldName;
	public String returnType;
	public boolean hasAssociation;
	public boolean hasMultiplicity;
	public boolean hasGetter;
	public boolean hasSetter;
	
	public ClassField(){
		this.accessModifier = 'a';
		this.fieldName = null;
		this.returnType = null;
		this.hasAssociation = false;
		this.hasMultiplicity = false;
		this.hasGetter = false;
		this.hasSetter = false;
	}
	
}
