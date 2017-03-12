public enum RelationShipType {

	EX("<|--"),
    IM("<|.."),
    AS("--"),
    DEP("<.."),
    LOLI("()--");

    private String s;
    RelationShipType(String s) {
        this.s = s;
    }
    public String getS() {
        return s;
    }

}
