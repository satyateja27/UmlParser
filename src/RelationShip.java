public class Relationship {
    private ClassOrInterfaceDeclaration a;
    private String multiplicityA;
    private ClassOrInterfaceDeclaration b;
    private String multiplicityB;
    private UmlRelationShipType type;

    public Relationship(ClassOrInterfaceDeclaration a, String multiplicityA, ClassOrInterfaceDeclaration b, String multiplicityB, UmlRelationShipType type) {
        this.a = a;
        this.multiplicityA = multiplicityA;
        this.b = b;
        this.multiplicityB = multiplicityB;
        this.type = type;
    }

    public ClassOrInterfaceDeclaration getA() {
        return a;
    }

    public void setA(ClassOrInterfaceDeclaration a) {
        this.a = a;
    }

    public String getMultiplicityA() {
        return multiplicityA;
    }

    public void setMultiplicityA(String multiplicityA) {
        this.multiplicityA = multiplicityA;
    }

    public ClassOrInterfaceDeclaration getB() {
        return b;
    }

    public void setB(ClassOrInterfaceDeclaration b) {
        this.b = b;
    }

    public String getMultiplicityB() {
        return multiplicityB;
    }

    public void setMultiplicityB(String multiplicityB) {
        this.multiplicityB = multiplicityB;
    }

    public UmlRelationShipType getType() {
        return type;
    }

    public void setType(UmlRelationShipType type) {
        this.type = type;
    }
}
