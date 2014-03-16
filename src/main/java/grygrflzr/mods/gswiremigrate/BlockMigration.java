package grygrflzr.mods.gswiremigrate;

import grygrflzr.mods.glowstonewire.GlowstoneWireMod;

import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockMigration extends Block {

    protected BlockMigration() {
        super(Material.circuits);
    }
    
    public void onBlockAdded(World world, int x, int y, int z) {
        world.setBlock(x, y, z, GlowstoneWireMod.glowstoneWire);
        FMLLog.fine("Forced block migration at %d,%d,%d", x, y, z);
    }

}
