import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class ExtractorUtils {
	
	
	public static String convertList(List<Float> x) {
		int l = x.toString().replaceAll(",", ";").length();
		return x.toString().replaceAll(",", ";").substring(1,l-1);
		
	}

	static void writeCVS(String i,String FileName) {
		
		try(PrintWriter pw = new PrintWriter(FileName)){
			
			pw.write(i);
			
			
			System.out.println("File wurde geschrieben");
			
		} catch (FileNotFoundException e) {
			System.out.println("ERROR File creating/schreiben");
			e.printStackTrace();
		}
		
		
	}

}

