package grygrflzr.mods.glowstonewire;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(
        modid           = GlowstoneWireMod.MODID,
        name            = GlowstoneWireMod.NAME,
        version         = GlowstoneWireMod.VERSION,
        /* First version to introduce GUI configurations */
        dependencies    = "required-after:Forge@[10.12.2.1146,)",
        guiFactory      = "grygrflzr.mods.glowstonewire.GlowstoneWireGUIFactory"
        )
public class GlowstoneWireMod {
    public static final String MODID = "GrygrFlzr_GlowstoneWire";
    public static final String NAME = "Glowstone Wire";
    public static final String VERSION = "1.0.2.113";
    
    public static Configuration config;
    
    public static Block glowstoneWire;
    public static int gsWireColor  = 0xFFFF00;
    public static int gsWireColorR = 255;
    public static int gsWireColorG = 255;
    public static int gsWireColorB = 0;

    @SidedProxy(clientSide = "grygrflzr.mods.glowstonewire.ClientProxy", serverSide = "grygrflzr.mods.glowstonewire.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance("GrygrFlzr_GlowstoneWire")
    public static GlowstoneWireMod instance;
    @Mod.EventHandler
    public void preload(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
        
        glowstoneWire = new BlockGlowstoneWire().setHardness(0.0F).setLightLevel(0.625F).setStepSound(Block.soundTypeStone).setBlockName("glowstoneDust").setBlockTextureName("redstone_dust");
        
        GameRegistry.registerBlock(glowstoneWire, "glowstone_wire");
        proxy.registerRenderInformation();
        MinecraftForge.EVENT_BUS.register(new GlowstoneWireEventHook());
    }
    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(instance);
        
        // Use when Waila supports icon-only changes
        //FMLInterModComms.sendMessage("Waila", "register", "grygrflzr.mods.glowstonewire.WailaHandler.callbackRegister");
        FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addVersionCheck", "https://raw.githubusercontent.com/GrygrFlzr/GlowstoneWire/master/version.json");
    }
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if(eventArgs.modID.equals(MODID)) {
            syncConfig();
        }
    }
    
    /**
     * Syncs the config for the mod
     */
    public static void syncConfig() {
        List<String> propOrder = new ArrayList<String>();
        Property prop;
        
        // Custom Category
        config.addCustomCategoryComment("color", "The color of Glowstone Wire");
        
        // Register settings
        prop = config.get("color", "Wire Color Red", 255, "The amount of red in the color of glowstone wires", 0, 255);
        gsWireColorR = prop.getInt(255);
        propOrder.add(prop.getName());
        
        prop = config.get("color", "Wire Color Green", 255, "The amount of green in the color of glowstone wires", 0, 255);
        gsWireColorG = prop.getInt(255);
        propOrder.add(prop.getName());
        
        prop = config.get("color", "Wire Color Blue", 0, "The amount of blue in the color of glowstone wires", 0, 255);
        gsWireColorB = prop.getInt(0);
        propOrder.add(prop.getName());
        
        // Force ordering
        config.setCategoryPropertyOrder("color", propOrder);
        
        // Calculate wire color
        gsWireColor = (gsWireColorR * 65536) +
                      (gsWireColorG *   256) +
                      (gsWireColorB);
        
        // Save when changed
        if(config.hasChanged())
            config.save();
    }
}
