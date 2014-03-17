package grygrflzr.mods.gswiremigrate;

import grygrflzr.mods.glowstonewire.GlowstoneWireMod;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Placeholder mod to handle old blocks
 * @author GrygrFlzr
 */
@Mod(modid = MigrateMod.MODID, name = MigrateMod.NAME, version = MigrateMod.VERSION)
public class MigrateMod {
    public static final String MODID = "GrygrFlzr_GlowstoneWire";
    public static final String NAME = "Glowstone Wire Migration";
    public static final String VERSION = GlowstoneWireMod.VERSION;
    
    /**
     * The migration version to compare to
     */
    public static final int MIGRATEVERSION = 1;
    /**
     * A map of chunks mapped by world<br>
     * Separate dimensions can have identical chunk coordinates
     */
    public static Map<World, Map<ChunkCoordIntPair, Boolean>> chunks = new HashMap<World, Map<ChunkCoordIntPair, Boolean>>();
    /**
     * A map to replace one block with another
     */
    public static Map<Block, Block> remap = new HashMap<Block, Block>();
    
    public static Block dummyBlock;

    @Instance(MODID)
    public static MigrateMod instance;
    
    @EventHandler
    public void preload(FMLPreInitializationEvent event) {
        dummyBlock = new DummyBlock().setBlockName("glowstoneDust");
        GameRegistry.registerBlock(dummyBlock, "glowstone_wire");
        registerRemap(dummyBlock, GlowstoneWireMod.glowstoneWire);
    }
    @EventHandler
    public void load(FMLInitializationEvent event) {
        //Defer event hook to init to allow adding to remap on preinit
        MinecraftForge.EVENT_BUS.register(new MigrateEventHook());
    }
    
    //--------- Remap Handler --------//
    
    /**
     * Remaps a block to another block
     * @param oldBlock The block to replace
     * @param newBlock The block that will replace oldBlock
     */
    public static void registerRemap(Block oldBlock, Block newBlock) {
        remap.put(oldBlock, newBlock);
    }
    
    /**
     * Checks if the block can be remapped
     * @param block The block to test
     * @return <b>TRUE</b> if block can be remapped, <b>FALSE</b> otherwise
     */
    public static boolean canRemap(Block block) {
        return remap.containsKey(block);
    }
    
    /**
     * Gets the block to remap
     * @param block The new block to replace
     * @return Block
     */
    public static Block getRemap(Block block) {
        return canRemap(block) ? remap.get(block) : null;
    }
    
    //------ Chunk Map Handlers ------//
    
    /**
     * Registers a chunk to the chunk cache
     * @param world The world the chunk resides in
     * @param chunkCoord The chunk coordinates
     * @param flagged
     */
    public static void registerChunk(World world, ChunkCoordIntPair chunkCoord, boolean flagged) {
        if(chunks.isEmpty() || !chunks.containsKey(world)) {
            chunks.put(world, new HashMap<ChunkCoordIntPair, Boolean>());
        }
        chunks.get(world).put(chunkCoord, flagged);
    }
    /**
     * Registers a chunk to the chunk cache
     * @param world The world the chunk resides in
     * @param chunk The chunk to register
     * @param flagged
     */
    public static void registerChunk(World world, Chunk chunk, boolean flagged) {
        registerChunk(world, chunk.getChunkCoordIntPair(), flagged);
    }
    /**
     * Registers a chunk to the chunk cache
     * @param chunk The chunk to register
     * @param flagged
     */
    public static void registerChunk(Chunk chunk, boolean flagged) {
        registerChunk(chunk.worldObj, chunk.getChunkCoordIntPair(), flagged);
    }
    
    /**
     * Checks if the given chunk in the world is flagged
     * @param world
     * @param chunkCoord
     * @return <b>TRUE</b> if chunk is flagged, <b>FALSE</b> otherwise
     */
    public static boolean isChunkFlagged(World world, ChunkCoordIntPair chunkCoord) {
        if(chunks.isEmpty()) {
            return false;
        }
        if(!chunks.containsKey(world)) {
            return false;
        }
        if(chunks.get(world).isEmpty()) {
            return false;
        }
        if(!chunks.get(world).containsKey(chunkCoord)) {
            return false;
        }
        if(chunks.get(world).get(chunkCoord) == true) {
            return true;
        }
        return false;
    }
    /**
     * Checks if the given chunk in the world is flagged
     * @param world The world the chunk resides in
     * @param chunk The chunk to check
     * @return <b>TRUE</b> if chunk is flagged, <b>FALSE</b> otherwise
     */
    public static boolean isChunkFlagged(World world, Chunk chunk) {
        return isChunkFlagged(world, chunk.getChunkCoordIntPair());
    }
    /**
     * Checks if the given chunk in the world is flagged
     * @param chunk The chunk to check
     * @return <b>TRUE</b> if chunk is flagged, <b>FALSE</b> otherwise
     */
    public static boolean isChunkFlagged(Chunk chunk) {
        return isChunkFlagged(chunk.worldObj, chunk.getChunkCoordIntPair());
    }
    
    /**
     * Clears the chunk mapping
     */
    public static void clearChunks() {
        chunks.clear();
    }
}
