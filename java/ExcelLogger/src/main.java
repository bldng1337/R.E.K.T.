import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class main {

	public static void main(String[] args) {
		String FileName = "C:\\5BHet\\kg.csv";
		writeCVS(FileName);
	}
	
	private static void writeCVS(String FileName) {
		
		try(PrintWriter pw = new PrintWriter(FileName)){
			
			pw.write(12 + ";");
			pw.write(69 + ";");
			pw.write(159 + ";");
			pw.write(69 + ";");
		
			
			System.out.println("File wurde geschrieben");
			
		} catch (FileNotFoundException e) {
			System.out.println("ERROR File creating/schreiben");
			e.printStackTrace();
		}
		
		
	}

}
