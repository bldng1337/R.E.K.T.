import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import imgui.ImFontConfig;
import imgui.ImFontGlyphRangesBuilder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.type.ImBoolean;

public class Main extends Application {
	public static List<Float> data=new LinkedList<>();
	
    @Override
    protected void configure(Configuration config) {
        config.setTitle("Dear ImGui is Awesome!");
       
        for(int i=0;i<80;i++)
        	data.add((float)Math.random());
       String FileName = "C:\\5BHet\\test1.csv";
       ExtractorUtils.writeCVS(ExtractorUtils.convertList(data), FileName);
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

    @Override
    public void process() {
    	ImGui.dockSpaceOverViewport();
    	ImGui.showStyleEditor();
    	if (ImGui.begin("Plot")) {
    		
	        if (ImPlot.beginPlot("Plot","x","y",ImGui.getContentRegionAvail())) {
	            ImPlot.plotLine("Line", getxs(data.size()), data.toArray(new Float[data.size()]));
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
