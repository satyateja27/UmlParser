public class Test {
	private String sourcePath;
	private String outputFile;
	private StringBuilder sb = new StringBuilder();
	private HashMap<String, ClassOrInterfaceDeclaration> map = new HashMap<>();

	public Parse(String sourcePath, String outputFile){
		this.sourcePath = sourcePath;
		this.outputFile = outputFile;
	}

	public void start(){

		File folder = new File(sourcePath);
		File[] files = folder.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.endsWith(".java");
			}
		});

		for(File file : files){
			System.out.println(file.getName());
			try {
				CompilationUnit cu = JavaParser.parse(file);
				ClassOrInterfaceDeclaration c = getClassOrInterfaceNode(cu);
				map.put(c.getName(), c);
			}catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}

		sb.append("@startuml\n");
		sb.append("skinparam classAttributeIconSize 0\n");
		for (Map.Entry<String, ClassOrInterfaceDeclaration> entry : map.entrySet()) {
            getClassOrInterfaceSignature(entry.getValue());
        }
		sb.append("@enduml");
		diagram(sb.toString());
	}

	private ClassOrInterfaceDeclaration getClassOrInterfaceNode(CompilationUnit cu){
			ClassOrInterfaceDeclaration c = null;
			List<TypeDeclaration> types = cu.getTypes();
			if(types != null){
				for(TypeDeclaration type : types){
					if(type instanceof ClassOrInterfaceDeclaration){
						return (ClassOrInterfaceDeclaration)type;
					}
				}
			}
			return c;
		}

}
