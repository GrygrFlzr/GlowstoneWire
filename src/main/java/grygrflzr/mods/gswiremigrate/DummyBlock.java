package grygrflzr.mods.gswiremigrate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class DummyBlock extends Block {

    protected DummyBlock() {
        super(Material.circuits);
    }
    
    public void onBlockAdded(World world, int x, int y, int z) {
        //world.setBlock(x, y, z, GlowstoneWireMod.glowstoneWire);
        //FMLLog.info("Replaced block (%d,%d,%d)", x, y, z);
        
        //TODO: remove code
        //Mark chunk unclean
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        MigrateMod.chunks.get(world).put(chunk.getChunkCoordIntPair(), true);
    }

}
