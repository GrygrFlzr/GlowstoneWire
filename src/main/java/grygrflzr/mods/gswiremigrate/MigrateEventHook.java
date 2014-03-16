package grygrflzr.mods.gswiremigrate;

import grygrflzr.mods.glowstonewire.GlowstoneWireMod;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MigrateEventHook {
    /**
     * Called on world unload<br>
     * Reset chunk cache
     */
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        if(!event.world.isRemote) {
            //Reset chunk cache
            MigrateMod.chunks.clear();
        }
    }
    
    
    /**
     * Handles Chunk Data Loading<br>
     * Chunk is read only, changes are not saved<br>
     * Flags chunks with MigrateVersion mismatch
     */
    @SubscribeEvent
    public void chunkDataLoad(ChunkDataEvent.Load event) {
        World world = event.world;
        
        if(!world.isRemote) {
            Chunk chunk = event.getChunk();
            if(!MigrateMod.chunks.containsKey(world)) {
                MigrateMod.chunks.put(world, new HashMap<ChunkCoordIntPair, Boolean>());
            }
            //Assume chunk is clean first
            MigrateMod.chunks.get(world).put(chunk.getChunkCoordIntPair(), false);
            
            int chunkMigrateVersion = event.getData().getCompoundTag("Level").getInteger(GlowstoneWireMod.MODID + ":MigrateVersion");
            //Compare Migrate Version - Future Proofing for possible migrations
            if(chunkMigrateVersion != MigrateMod.MIGRATEVERSION) {
                chunkIterate:
                    for(int x=0; x<16; x++) {
                        for(int z=0; z<16; z++) {
                            //Limit iteration to highest block
                            for(int y=0; y<chunk.getHeightValue(x, z); y++) {
                                if(chunk.getBlock(x, y, z) == MigrateMod.glowstoneWire) {
                                    //Mark chunk in memory
                                    MigrateMod.chunks.get(world).put(chunk.getChunkCoordIntPair(), true);
                                    FMLLog.info("Found dirty chunk (%d,%d)", chunk.xPosition, chunk.zPosition);
                                    //Break iteration
                                    break chunkIterate;
                                }
                            }
                        }
                    }
            }
        }
    }
    
    /**
     * Handles Chunk Loading<br>
     * Replaces obsolete blocks in flagged chunks with new blocks
     */
    //@SubscribeEvent
    public void chunkLoad(ChunkEvent.Load event) {
        World world = event.world;
        Chunk chunk = event.getChunk();
        
        if(!event.world.isRemote) {
            //Check if chunk is marked
            if(MigrateMod.chunks.get(world).get(chunk.getChunkCoordIntPair()) == true) {
                for(int x=0; x<16; x++) {
                    for(int z=0; z<16; z++) {
                        //Limit iteration to highest block
                        for(int y=0; y<chunk.getHeightValue(x, z); y++) {
                            if(chunk.getBlock(x, y, z) == MigrateMod.glowstoneWire) {
                                world.setBlock(chunk.xPosition*16+x, y, chunk.zPosition*16+z, GlowstoneWireMod.glowstoneWire);
                                FMLLog.info("Replaced block (%d,%d,%d)", chunk.xPosition*16+x, y, chunk.zPosition*16+z);
                            }
                        }
                    }
                }
            }
            
            //Mark as clean
            MigrateMod.chunks.get(world).put(chunk.getChunkCoordIntPair(), false);
        }
    }
    
    /**
     * Handles Chunk Data Saving
     * @param event
     */
    //TODO: uncomment annotation
    //@SubscribeEvent
    public void chunkDataSave(ChunkDataEvent.Save event) {
        World world = event.world;
        Chunk chunk = event.getChunk();
        
        if(!world.isRemote) {
            if(MigrateMod.chunks.get(world).get(chunk.getChunkCoordIntPair()) == false) {
                //Flag chunk as clean
                event.getData().getCompoundTag("Level").setInteger(GlowstoneWireMod.MODID + ":MigrateVersion",MigrateMod.MIGRATEVERSION);
            }
        }
    }
}
