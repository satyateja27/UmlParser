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

    
}
