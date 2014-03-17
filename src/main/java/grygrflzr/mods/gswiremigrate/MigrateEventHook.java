package grygrflzr.mods.gswiremigrate;

import grygrflzr.mods.glowstonewire.GlowstoneWireMod;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
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
     * Resets chunk cache
     */
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        if(!event.world.isRemote) {
            //Reset chunk cache
            MigrateMod.clearChunks();
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
            //Assume chunk is clean first
            MigrateMod.registerChunk(chunk, false);
            
            int chunkMigrateVersion = event.getData().getCompoundTag("Level").getInteger(GlowstoneWireMod.MODID + ":MigrateVersion");
            //Compare Migrate Version - Future Proofing for possible migrations
            if(chunkMigrateVersion != MigrateMod.MIGRATEVERSION) {
                chunkIterate:
                    for(int x=0; x<16; x++) {
                        for(int z=0; z<16; z++) {
                            //Limit iteration to highest block
                            for(int y=0; y<chunk.getHeightValue(x, z); y++) {
                                //Find and replace block
                                if(MigrateMod.canRemap(chunk.getBlock(x, y, z))) {
                                    //Mark chunk in memory
                                    MigrateMod.registerChunk(chunk, true);
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
    @SubscribeEvent
    public void chunkLoad(ChunkEvent.Load event) {
        World world = event.world;
        Chunk chunk = event.getChunk();
        
        if(!event.world.isRemote) {
            //Check if world is not newly generated
            if(!MigrateMod.chunks.get(world).isEmpty()) {
                //Check if chunk is marked
                if(MigrateMod.isChunkFlagged(chunk)) {
                    for(int x=0; x<16; x++) {
                        for(int z=0; z<16; z++) {
                            //Limit iteration to highest block
                            for(int y=0; y<chunk.getHeightValue(x, z); y++) {
                                Block oldBlock = chunk.getBlock(x, y, z);
                                //Find and replace block
                                if(MigrateMod.canRemap(oldBlock)) {
                                    world.setBlock(chunk.xPosition*16+x, y, chunk.zPosition*16+z, MigrateMod.getRemap(oldBlock));
                                    FMLLog.info(
                                            "(%d,%d,%d) Replaced %s with %s", chunk.xPosition*16+x, y, chunk.zPosition*16+z,
                                            oldBlock.getUnlocalizedName(), MigrateMod.getRemap(oldBlock).getUnlocalizedName());
                                }
                            }
                        }
                    }
                }
                
                //Mark as clean
                MigrateMod.registerChunk(chunk, false);
            }
        }
    }
    
    /**
     * Handles Chunk Data Saving
     * @param event
     */
    @SubscribeEvent
    public void chunkDataSave(ChunkDataEvent.Save event) {
        World world = event.world;
        Chunk chunk = event.getChunk();
        
        if(!world.isRemote) {
            //Assume chunk is clean first
            event.getData().getCompoundTag("Level").setInteger(GlowstoneWireMod.MODID + ":MigrateVersion",MigrateMod.MIGRATEVERSION);
            
            //Check if chunk is marked as dirty
            if(MigrateMod.isChunkFlagged(chunk)) {
                //Remove flag
                event.getData().getCompoundTag("Level").removeTag(GlowstoneWireMod.MODID + ":MigrateVersion");
            }
        }
    }
}
