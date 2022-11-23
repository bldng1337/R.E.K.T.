import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ExtractorUtils {
	
	
	public static String convertList(Map<String, List<Main.Datapoint>> x) {
		StringBuilder bobdermeister = new StringBuilder ();
		for(Entry<String, List<Main.Datapoint>> s:x.entrySet()) {
			
			bobdermeister.append(s.getKey());
			bobdermeister.append(";");
			int l =  s.getValue().toString().length();
			bobdermeister.append( s.getValue().toString().replaceAll(",", ";").substring(1,l-1).replaceAll("\\.", ","));
			bobdermeister.append("\n");
		}
			
		
		int l = x.toString().replaceAll(",", ";").length();
//		return x.toString().replaceAll(",", ";").substring(1,l-1);
		return bobdermeister.toString();
		
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
