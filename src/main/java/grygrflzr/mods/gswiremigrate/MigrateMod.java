package grygrflzr.mods.gswiremigrate;

import grygrflzr.mods.glowstonewire.GlowstoneWireMod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = MigrateMod.MODID, name = MigrateMod.NAME, version = MigrateMod.VERSION)
public class MigrateMod {
    public static final String MODID = "GrygrFlzr_GlowstoneWire";
    public static final String NAME = "Glowstone Wire Migration";
    public static final String VERSION = GlowstoneWireMod.VERSION;
    
    public static final int MIGRATEVERSION = 1;
    //public static HashSet dirtyChunks = new HashSet();
    public static Map<World, Map<ChunkCoordIntPair, Boolean>> chunks;
    public static byte blockIDs;
    
    public static Block glowstoneWire;

    @Instance("GrygrFlzr_GlowstoneWire")
    public static MigrateMod instance;
    @EventHandler
    public void preload(FMLPreInitializationEvent event) {
        glowstoneWire = new BlockMigration().setHardness(0.0F).setLightLevel(0.625F).setStepSound(Block.soundTypeStone).setBlockName("glowstoneDust");
        GameRegistry.registerBlock(glowstoneWire, "glowstone_wire");
        MinecraftForge.EVENT_BUS.register(new MigrateEventHook());
    }
    
    //@EventHandler
    public void mappingError(FMLMissingMappingsEvent event) {
        //cpw.mods.fml.common.event.
        FMLLog.info("BEGIN MAPPING OVERRIDE");
        List<MissingMapping> missingList = event.get();
        //FMLLog.info("Missing Mappings: %s", missing);
        for(int i=0; i<missingList.size(); i++) {
            MissingMapping missing = missingList.get(i);
            //FMLLog.info("Missing Mapping for %s of type %s",missing.get(i).name,missing.get(i).type);
            if(missing.name == MODID + ":glowstone_wire") {
                
            }
        }
    }
}
