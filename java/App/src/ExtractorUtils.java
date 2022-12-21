import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;

public class ExtractorUtils {

	public static String convertList(Map<String, List<Main.Datapoint>> x) {
		StringBuilder bobdermeister = new StringBuilder();
		for (Entry<String, List<Main.Datapoint>> s : x.entrySet()) {

			bobdermeister.append(s.getKey());
			bobdermeister.append(";");
			int l = s.getValue().toString().length();
			bobdermeister
					.append(s.getValue().toString().replaceAll(",", ";").substring(1, l - 1).replaceAll("\\.", ","));
			bobdermeister.append("\n");
		}

		return transposeCsv(bobdermeister.toString());

	}
	
	public static String transposeCsv(String csv) {
	    // Split the CSV string into rows
	    String[] rows = csv.split("\n");
	    
	    // Split each row into columns
	    String[][] data = new String[rows.length][];
	    for (int i = 0; i < rows.length; i++) {
	        data[i] = rows[i].split(";");
	    }
	    
	    // Create a new 2D array to hold the transposed data
	    String[][] transposedData = new String[data[0].length][data.length];
	    
	    // Iterate over the original data and copy the values into the transposed array
	    for (int i = 0; i < data.length; i++) {
	        for (int j = 0; j < data[i].length; j++) {
	            transposedData[j][i] = data[i][j];
	        }
	    }
	    
	    // Convert the transposed data array back into a CSV string
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < transposedData.length; i++) {
	        for (int j = 0; j < transposedData[i].length; j++) {
	            sb.append(transposedData[i][j]);
	            if (j < transposedData[i].length - 1) {
	                sb.append(";");
	            }
	        }
	        sb.append("\n");
	    }
	    
	    return sb.toString();
	}

	static void writeCVS(String i, String FileName) {

		try (PrintWriter pw = new PrintWriter(FileName)) {

			pw.write(i);

			System.out.println("File wurde geschrieben");

		} catch (FileNotFoundException e) {
			System.out.println("ERROR File creating/schreiben");
			e.printStackTrace();
		}
		

		
	}
	
	public static File showFileDialog(File dir) {
		FileDialog filed= new FileDialog(new JFrame(),"Choose a File",FileDialog.LOAD);
		filed.setVisible(true);
		if(filed.getFile()==null)
			return null;
		return filed.getFiles()[0];
		
		
		
		
	}

}
