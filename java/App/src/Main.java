import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	class Datapoint{
		private final float data;
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
	}
	public static Map<String, List<Datapoint>> data=new HashMap<>();
	
    @Override
    protected void configure(Configuration config) {
	    config.setTitle("Dear ImGui is Awesome!");
	    data.put("Temperatur", new LinkedList<>());
	    data.put("Feuchtigkeit", new LinkedList<>());
	    
//	    String FileName = "C:\\Users\\BF\\Desktop\\t.csv";
//		ExtractorUtils.writeCVS(ExtractorUtils.convertList(data), FileName);
	  //Start Demo Data
	    for(int i=0;i<1000;i++)
	    	data.forEach((a,b)->b.add(new Datapoint((float) (Math.random()*50f),b.size())));
    }
  
    
    @Override
    protected void initImGui(final Configuration config) {
    	super.initImGui(config);
    	ImPlot.createContext();
    	final ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
        io.setConfigViewportsNoTaskBarIcon(true);
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
    
    public Float[] getxs(int size) {
    	Float[] f=new Float[size];
    	for(int i=0;i<size;i++)
    		f[i]=(float) i;
    	return f;
    }
    boolean autoscroll=false;
    @Override
    public void process() {
    	ImGui.dockSpaceOverViewport();
    	//ImGui.showStyleEditor();
    	data.forEach((a,b)->{
    		if(b.size()>10_000)
    			b.remove(0);
    	});
    	data.forEach((a,b)->b.add(new Datapoint((float) (Math.random()*50f),b.size())));
    	
    	
    	if (ImGui.begin("Plot")) {
    		if(ImGui.button("AutoScroll"))
        		autoscroll=!autoscroll;
	        if (ImPlot.beginPlot("Plot","x","y",ImGui.getContentRegionAvail(),ImPlotFlags.None , ImPlotAxisFlags.None|(autoscroll?ImPlotAxisFlags.AutoFit:ImPlotAxisFlags.None), ImPlotAxisFlags.AutoFit)) {
	            
	        	data.forEach((a,b)->{
	        		List<Float> vals=b.stream().map((data)->data.getData()).collect(Collectors.toList());
	        		List<Float> time=b.stream().map((data)->(float)data.getTimestamp()).collect(Collectors.toList());
	        		
	        		
	        		ImPlot.plotLine(a, 
	        			(autoscroll?time.subList(Math.max(time.size()-100, 0), time.size()):time).toArray(new Float[1]),
	        			(autoscroll?vals.subList(Math.max(vals.size()-100, 0), vals.size()):vals).toArray(new Float[1]));
	        	});
	        	
	            
	            ImPlot.endPlot();
	        }
    	}
    	ImGui.end();
        ImPlot.showDemoWindow(new ImBoolean(true));
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
