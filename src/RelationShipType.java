public enum RelationShipType {

	EXTEND("<|--"),
    IMPLEMENT("<|.."),
    ASSOCIATION("--"),
    DEPENDENCY("<.."),

    private String s;
    RelationShipType(String s) {
        this.s = s;
    }
    public String getS() {
        return s;
    }

}
