
public class Main {

	public static void main(String[] args) {

		int index = 5;
		args = new String[2];
		args[0] = "test/uml-parser-test-" + index;
		args[1] = "output " + index + ".png";
		
		Parse parse = new Parse(args[0]);
		DrawUml diagram = new DrawUml(args[1]);
		diagram.generateUml(parse.start());
		
	}

}
