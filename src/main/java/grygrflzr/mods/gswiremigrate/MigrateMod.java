package grygrflzr.mods.gswiremigrate;

import grygrflzr.mods.glowstonewire.GlowstoneWireMod;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = MigrateMod.MODID, name = MigrateMod.NAME, version = MigrateMod.VERSION)
public class MigrateMod {
    public static final String MODID = "GrygrFlzr_GlowstoneWire";
    public static final String NAME = "Glowstone Wire Migration";
    public static final String VERSION = GlowstoneWireMod.VERSION;
    
    public static Block glowstoneWire;

    @Instance("GrygrFlzr_GlowstoneWire")
    public static MigrateMod instance;
    @EventHandler
    public void preload(FMLPreInitializationEvent event) {
        glowstoneWire = new BlockMigration().setHardness(0.0F).setLightLevel(0.625F).setStepSound(Block.soundTypeStone).setBlockName("glowstoneDust");
        GameRegistry.registerBlock(glowstoneWire, "glowstone_wire");
    }
    @EventHandler
    public void load(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new MigrateEventHook());
    }
}
