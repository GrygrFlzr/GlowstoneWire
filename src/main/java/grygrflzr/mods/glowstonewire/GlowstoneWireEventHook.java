package grygrflzr.mods.glowstonewire;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GlowstoneWireEventHook {
    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {
        if(event.action == Action.RIGHT_CLICK_BLOCK) {
            if(event.entityPlayer.inventory.getCurrentItem() != null &&
                event.entityPlayer.inventory.getCurrentItem().getItem() == Items.glowstone_dust) {
                int x = event.x;
                int y = event.y;
                int z = event.z;
                int face = event.face;
                if(event.entity.worldObj.getBlock(x, y, z) != Blocks.snow_layer) {
                    if (face == 0)
                        --y;
                    if (face == 1)
                        ++y;
                    if (face == 2)
                        --z;
                    if (face == 3)
                        ++z;
                    if (face == 4)
                        --x;
                    if (face == 5)
                        ++x;
                }
                if(!event.entity.worldObj.isAirBlock(x, y, z))
                    if(event.entity.worldObj.getBlock(x, y, z) != Blocks.snow_layer)
                        return;
                if(!event.entityPlayer.canPlayerEdit(x, y, z, face, event.entityPlayer.inventory.getCurrentItem()))
                    return;
                else
                    if(GlowstoneWireMod.glowstoneWire.canPlaceBlockAt(event.entity.worldObj, x, y, z)) {
                        if(!event.entityPlayer.capabilities.isCreativeMode)
                            --event.entityPlayer.inventory.getCurrentItem().stackSize;
                        event.entity.worldObj.setBlock(x, y, z, GlowstoneWireMod.glowstoneWire);
                    }
            }
        }
    }
}
