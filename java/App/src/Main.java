import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import imgui.ImFontConfig;
import imgui.ImFontGlyphRangesBuilder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.extension.implot.flag.ImPlotFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.type.ImBoolean;

public class Main extends Application {
	//Class modelling the Skeleton of the JSOn File
	public static class Data{
		public Data() {
		}
		public float temp,hum;
		public long timestamp;
	}
	//The Datapoints that get stored internally
	public static class Datapoint{
		//The datapoint
		private final float data;
		//The timestamp
		private final long timestamp;
		public Datapoint(float data,long timestamp) {
			this.data=data;
			this.timestamp=timestamp;
		}
		public float getData() {
			return data;
		}
		public long getTimestamp() {
			return timestamp;
		}
		@Override
		public String toString() {
			return data+"";
		}
	}
	
	
	// Queue for sending sensor data from the Sensor thread to the Render Thread
	volatile BlockingQueue<Data> sensordataqueue=new LinkedBlockingQueue<>(200);
	// A Map saving the differen Datapoints the keys are the Name of the Values and the entry is a LinkedList of Datapoints
	public static Map<String, List<Datapoint>> data=new HashMap<>();
	// The Thread responsible for communicating with the sensorthread
	Thread sensorthread;
    @Override
    protected void configure(Configuration config) {
    	//Setting the Window Title
	    config.setTitle("Sensor UI");
	    
	    //Init the Data we are getting from the Sensors
	    data.put("Temperatur", new LinkedList<>());
	    data.put("Feuchtigkeit", new LinkedList<>());
	    
	    //Setting up the Thread polling the Sensor
	    SensorJob sj=new SensorJob();
	    sensorthread = new Thread(sj);
	    sensorthread.setName("Sensor-Thread");
	    sensorthread.start();
	    
    }
  
    
    @Override
    protected void initImGui(final Configuration config) {
    	super.initImGui(config);
    	//Init Plot Library
    	ImPlot.createContext();
    	//Init Config
    	final ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
        io.setConfigViewportsNoTaskBarIcon(true);
        //Init Fonts
        initFonts(io);
    }
    
    private void initFonts(final ImGuiIO io) {
        io.getFonts().addFontDefault();

        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesDefault());
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesCyrillic());

        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setName("Bebas");
//        fontConfig.setMergeMode(true);

        final short[] glyphRanges = rangesBuilder.buildRanges();
        io.getFonts().addFontFromMemoryTTF(loadFromResources("Bebas.ttf"), 20, fontConfig, glyphRanges);
        io.getFonts().build();

        fontConfig.destroy();
    }
    
    //Thread polling the Data from the arduino
    class SensorJob implements Runnable{
    	//Last data so we dont poll duplicate Data
    	Data last;
		@Override
		public void run() {
			while(true) {
				//try to read the local site
				try(Scanner sc = new Scanner(new URL("http://192.168.4.1/get").openStream())) {
			        StringBuffer sb = new StringBuffer();
			        while(sc.hasNext()) {
			           sb.append(sc.next());
			        }
			        String result = sb.toString();
			        
			        //Deserialize the JSON
			    	Gson g=new Gson();
			    	Data d=g.fromJson(result, Data.class);
			    	System.out.println(result);
			    	
			    	//Check if it is the same value from last time polling
			    	if(last==null) {
			    		last=g.fromJson(result, Data.class);
			    	}else if(last.timestamp==d.timestamp) {
			    		continue;
			    	}
			    	last=g.fromJson(result, Data.class);
			    	sensordataqueue.put(d);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Sleep a bit
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    	
    }
    //if The Plot should autoscroll
    boolean autoscroll=false;
    @Override
    public void process() {
    	//Enable Window Docking
    	ImGui.dockSpaceOverViewport();
    	//Menu Bar for Saving
    	if (ImGui.beginMainMenuBar()) {
	    	if(ImGui.beginMenu("File")) {
	    		if(ImGui.menuItem("Print to File")) {
	    			File f=ExtractorUtils.showFileDialog(new File("sdfh"));
	    			if(f.getName().endsWith(".csv"))
	    				ExtractorUtils.writeCVS(ExtractorUtils.convertList(data), f.getAbsolutePath());
	    			else
	    				ExtractorUtils.writeCVS(ExtractorUtils.convertList(data), f.getAbsolutePath()+".csv");
	    		}
	    		ImGui.endMenu();
	    	}
	    	
	    	ImGui.endMainMenuBar();
	    }
    	
		//Remove data if its too much 
		data.forEach((a,b)->{
    		if(b.size()>5_000)
    			b.remove(0);
    	});
		//Refresh the sensordata from the poll thread
		while(sensordataqueue.peek()!=null) {
			Data d=sensordataqueue.poll();
			data.get("Temperatur").add(new Datapoint(d.temp, d.timestamp));
	    	data.get("Feuchtigkeit").add(new Datapoint(d.hum, d.timestamp));
		}
		//The only and Main Window responsible for the Plot rednering
    	if (ImGui.begin("Plot")) {
    		if(ImGui.button("AutoScroll"))
        		autoscroll=!autoscroll;
	        if (ImPlot.beginPlot("Plot","x","y",ImGui.getContentRegionAvail(),ImPlotFlags.None , ImPlotAxisFlags.None|(autoscroll?ImPlotAxisFlags.AutoFit:ImPlotAxisFlags.None), ImPlotAxisFlags.AutoFit)) {
	            //Get all the data in the map and plot it
	        	data.forEach((a,b)->{
	        		List<Float> vals=b.stream().map((data)->data.getData()).collect(Collectors.toList());
	        		List<Float> time=b.stream().map((data)->(float)data.getTimestamp()).collect(Collectors.toList());
	        		
	        		if(vals.isEmpty()||time.isEmpty())
	        			return;
	        		ImPlot.plotLine(a, 
	        			(autoscroll?time.subList(Math.max(time.size()-100, 0), time.size()):time).toArray(new Float[1]),
	        			(autoscroll?vals.subList(Math.max(vals.size()-100, 0), vals.size()):vals).toArray(new Float[1]));
	        	});
	        	
	            
	            ImPlot.endPlot();
	        }
    	}
    	ImGui.end();
//        ImPlot.showDemoWindow(new ImBoolean(true));
    }
    
    private static byte[] loadFromResources(String name) {
        try {
            return Files.readAllBytes(Paths.get(Main.class.getResource(name).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(new Main());
    }
}
