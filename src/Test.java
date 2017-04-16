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

	private void getClassOrInterfaceSignature(ClassOrInterfaceDeclaration c){

			if(c.isInterface()){
				sb.append("interface ");
				sb.append(c.getName());
				sb.append(" {\n");
				sb.append("}\n");
			}else{
				sb.append("class ");
				sb.append(c.getName());
				sb.append(" {\n");
				sb.append("}\n");
			}

			List<ClassOrInterfaceType> extendsContent = c.getExtends();
			if(extendsContent != null){
				for(ClassOrInterfaceType type : extendsContent){
					String name = type.getName();
					if(map.containsKey(name)){
						sb.append(name + " <-- " + type.getName());
					}
				}
			}

			List<ClassOrInterfaceType> implementsContent = c.getImplements();
			if(implementsContent != null){
				for(ClassOrInterfaceType type : implementsContent){
					String name = type.getName();
					if(map.containsKey(name)){
						sb.append(name + " <-- " + type.getName());
					}
				}
			}

		}

		private void parseClassOrInterface(CompilationUnit cu){
				List<TypeDeclaration> types = cu.getTypes();
				if(types != null){
					for(TypeDeclaration type : types){
						if(type instanceof ClassOrInterfaceDeclaration){
							if(((ClassOrInterfaceDeclaration)type).isInterface()){
								sb.append("interface ").append(type.getName()).append(" <<interface>> {\n");
							}else{
								sb.append("class ").append(type.getName()).append(" {\n");
							}
						}
					}
				}
			}

		private void parseBody(CompilationUnit cu){
			List<TypeDeclaration> types = cu.getTypes();
			if(types != null){
				for(TypeDeclaration type: types){
					List<BodyDeclaration> bodyDeclaration = type.getMembers();
					for(BodyDeclaration body : bodyDeclaration){
						if(body instanceof FieldDeclaration){
							parseField((FieldDeclaration)body);
						}else if(body instanceof MethodDeclaration){
							parseMethod((FieldDeclaration)body);
						}else if(body instanceof ConstructorDeclaration){
							parseConstructor((FieldDeclaration)body);
						}
					}
				}
			}
			sb.append("}");
		}

		private void parseField(FieldDeclaration field){
			System.out.println(field.getModifiers());
			System.out.println(field);
			System.out.println(field);
		}

		public void diagram(String source){

		SourceStringReader reader = new SourceStringReader(source); // Read from the source String
        String desc = null;

		try {
			OutputStream png = new FileOutputStream(outputFile); // Create Target File for Output
			desc = reader.generateImage(png); // Write the first image to "png"
		} catch (IOException e) {
			e.printStackTrace();
		}

        if(desc != null){
        	System.out.println("UML Successfully Rendered");
        }else{
        	System.out.println("UML Generation Unsuccessful");
        }
	}

}
