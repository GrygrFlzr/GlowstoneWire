package grygrflzr.mods.glowstonewire;

import java.security.CodeSource;
import java.security.cert.Certificate;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.CertificateHelper;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = GlowstoneWireMod.MODID, name = GlowstoneWireMod.NAME, version = GlowstoneWireMod.VERSION)
public class GlowstoneWireMod {
    public static final String MODID = "gswire";
    public static final String NAME = "Glowstone Wire";
    public static final String VERSION = "1.0.1.112";
    
    public static Block glowstoneWire;
    public static int gsWireColorR = 255;
    public static int gsWireColorG = 255;
    public static int gsWireColorB = 0;

    @SidedProxy(clientSide = "grygrflzr.mods.glowstonewire.ClientProxy", serverSide = "grygrflzr.mods.glowstonewire.CommonProxy")
    public static CommonProxy proxy;
    @Instance(MODID)
    public static GlowstoneWireMod instance;
    @EventHandler
    public void preload(FMLPreInitializationEvent event) {
        checkSignature();
        
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        config.addCustomCategoryComment("color", "Color Range: 0-255, uses default values if out of range");
        gsWireColorR = config.get("color", "Wire Color Red", 255).getInt();
        gsWireColorG = config.get("color", "Wire Color Green", 255).getInt();
        gsWireColorB = config.get("color", "Wire Color Blue", 0).getInt();
        
        if(gsWireColorG > 255 || gsWireColorG < 0 ||
           gsWireColorR > 255 || gsWireColorR < 0 ||
           gsWireColorB > 255 || gsWireColorB < 0)
        { //use default if ANY value is out of range.
            FMLLog.warning("Glowstone Wire: Invalid colors, resetting to default");
            config.removeCategory(config.getCategory("color"));
            config.addCustomCategoryComment("color", "Color Range: 0-255, uses default values if out of range");
            gsWireColorR = config.get("color", "Wire Color Red", 255).getInt();
            gsWireColorG = config.get("color", "Wire Color Green", 255).getInt();
            gsWireColorB = config.get("color", "Wire Color Blue", 0).getInt();
        }
        config.save();
        
        glowstoneWire = new BlockGlowstoneWire(gsWireColorR, gsWireColorG, gsWireColorB).setHardness(0.0F).setLightLevel(0.625F).setStepSound(Block.soundTypeStone).setBlockName("glowstoneDust").setBlockTextureName("redstone_dust");
        
        GameRegistry.registerBlock(glowstoneWire, "glowstone_wire");
        proxy.registerRenderInformation();
        MinecraftForge.EVENT_BUS.register(new GlowstoneWireEventHook());
    }
    
    void checkSignature() {
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        if (codeSource.getLocation().getProtocol().equals("jar")) {
            Certificate[] certificates = codeSource.getCertificates();
            if (certificates != null) {
                for (Certificate cert : certificates) {
                    String fingerprint = CertificateHelper.getFingerprint(cert);
                    FMLLog.info("Found fingerprint: %s", fingerprint);
                }
            } else {
                FMLLog.info("Mod %s is not signed", MODID);
            }
        }
    }
}
