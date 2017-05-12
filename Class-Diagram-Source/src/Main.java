import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {


	public static void main(String[] args) throws IOException {
		
		boolean tempFolderCreated = false;
		
		if(args[0].contains(".zip")){
			args[0] = unZip(args[0]);
			tempFolderCreated = true;
		}
		
		Parse parse = new Parse(args[0]);
		DrawUml diagram = new DrawUml(args[1]);
		diagram.generateUml(parse.start());
		
		if(tempFolderCreated){
			deleteTempFolder();
		}
		
	}
	
	/**
	 * Zip File extraction starts from here
	 * @param zipFilePath
	 * @author satya
	 */
	private static String unZip(String zipFilePath) throws IOException {
    	String destinationDirectory = "temp";
        File tempDirectory = new File(destinationDirectory);
        if (!tempDirectory.exists()) {
        	tempDirectory.mkdir();
        }
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipInputStream.getNextEntry();
        while (entry != null) {
            String filePath = tempDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
                byte[] buffer = new byte[4096];
        		int read = 0;
                while ((read = zipInputStream.read(buffer)) != -1) {
                	outputStream.write(buffer, 0, read);
                }
                outputStream.close();
            }else {
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipInputStream.closeEntry();
            entry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
        return tempDirectory.getAbsolutePath();
    }
	
	
	/**
	 * Temporary folder deletion starts from here
	 * @author satya
	 */
	private static void deleteTempFolder(){
    	String destinationDirectory = "temp";
        File tempDirectory = new File(destinationDirectory);
        if (tempDirectory.exists()) {
            for(File file : tempDirectory.listFiles()){
            	file.delete();
            }
            tempDirectory.delete();
        }
    }

}

