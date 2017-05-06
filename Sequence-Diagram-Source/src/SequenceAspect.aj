import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import net.sourceforge.plantuml.SourceStringReader;

public aspect SequenceAspect {

	private int methodExecutionCount;
	private int constructorCallCount;
	private Stack<String> classStack = new Stack<String>();
	private StringBuilder sb = new StringBuilder();
	
	pointcut methodExecution() : execution(* *.*(..)) && !within(SequenceAspect);
	pointcut constructorCall() : call(*.new(..)) && !within(SequenceAspect);
	
	before(): constructorCall(){
		constructorCallCount++;
	}
	
	after() : constructorCall(){
		constructorCallCount--;
	}
	
	before(): methodExecution(){
		System.out.println(constructorCallCount);
		if(constructorCallCount == 0){
			String targetClass;
			if(methodExecutionCount==0){
				targetClass = thisJoinPoint.getSignature().getDeclaringTypeName();
				sb.append("@startuml\n");
				sb.append("skinparam classAttributeIconSize 0\n");
				sb.append("hide footbox\n");
				sb.append("activate ").append(targetClass).append("\n");
			}else{
				targetClass = getUmlGrammar(thisJoinPoint);
			}
			classStack.push(targetClass);
			methodExecutionCount++;
		}
	}
	
	after(): methodExecution(){
		if(constructorCallCount == 0){
			methodExecutionCount--;
			sb.append("deactivate ").append(classStack.peek()).append("\n");
			classStack.pop();
			if(methodExecutionCount==0){
				sb.append("@enduml");
				System.out.println(sb.toString());
				diagram(sb.toString(),"output");
			}
		}
	}
	
	private String getUmlGrammar(JoinPoint jp){
		String methodSignature = getMethodSignature(jp);
		String targetClass = jp.getTarget().toString();
		for(int k=0; k<targetClass.length(); k++){
			if(targetClass.charAt(k) == '@'){
				targetClass = targetClass.substring(0, k);
				break;
			}
		}
		sb.append(classStack.peek()).append(" -> ").append(targetClass);
		sb.append(" : ").append(methodSignature).append("\n");
		sb.append("activate ").append(targetClass).append("\n");
		return targetClass;
	}
	
	private String getMethodSignature(JoinPoint jp){
		MethodSignature signature = (MethodSignature) jp.getStaticPart().getSignature();
		String[] parameterNames = signature.getParameterNames();
		Class[] parameterTypes = signature.getParameterTypes();
		StringBuilder mn = new StringBuilder();
		mn.append(signature.getName()).append("(");
		for(int j=0; j < parameterNames.length; j++){
			if(j>0 && j<parameterNames.length){
				mn.append(", ");
			}
			mn.append(parameterNames[j]).append(" : ").append(parameterTypes[j].getSimpleName());
		}
		mn.append(") : ").append(signature.getReturnType().getSimpleName());
		return mn.toString();
	}
	
	private void diagram(String source, String outputFile){
		SourceStringReader reader = new SourceStringReader(source); // Read from the source String
        String desc = null;
		try {
			OutputStream png = new FileOutputStream(outputFile + ".png"); // Create Target File for Output
			desc = reader.generateImage(png); // Write the image generation result
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
