package grygrflzr.mods.gswiremigrate;

import grygrflzr.mods.glowstonewire.GlowstoneWireMod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.ChunkEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MigrateEventHook {
    @SubscribeEvent
    public void chunkLoad(ChunkEvent.Load event) {
        int chunkX = event.getChunk().xPosition;
        int chunkZ = event.getChunk().zPosition;
        World world = event.world;
        
        if(!world.isRemote) {
            FMLLog.fine("Loading Chunk %d, %d", chunkX, chunkZ);
            long startTime = System.currentTimeMillis();
            
            for(int x=0; x<16; x++) {
                for(int z=0; z<16; z++) {
                    for(int y=0; y<world.getActualHeight(); y++) {
                        Block block = event.getChunk().getBlock(x, y, z);
                        if(block == MigrateMod.glowstoneWire) {
                            FMLLog.fine(
                                    "Replacing glowstone wire block at (%d,%d,%d)",
                                    chunkX*16+x, y, chunkZ*16+z
                                    );
                            world.setBlock(chunkX*16+x, y, chunkZ*16+z, GlowstoneWireMod.glowstoneWire);
                        }
                    }
                }
            }
            
            long endTime = System.currentTimeMillis();
            FMLLog.fine("Iterated through chunk in %d milliseconds", (endTime - startTime));
        }
    }
    
}
