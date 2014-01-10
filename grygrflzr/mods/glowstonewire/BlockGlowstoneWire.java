package grygrflzr.mods.glowstonewire;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGlowstoneWire extends Block
{
    /**
     * When false, power transmission methods do not look at other redstone wires. Used internally during
     * updateCurrentStrength.
     */
    private boolean wiresProvidePower = false;
    private Set blocksNeedingUpdate = new HashSet();
    @SideOnly(Side.CLIENT)
    private static IIcon field_94413_c;
    @SideOnly(Side.CLIENT)
    private static IIcon field_94410_cO;
    @SideOnly(Side.CLIENT)
    private static IIcon field_94411_cP;
    @SideOnly(Side.CLIENT)
    private static IIcon field_94412_cQ;
    private int wireColor = 0xFFFF00;
    private int wireColorR = 255;
    private int wireColorG = 255;
    private int wireColorB = 0;

    public BlockGlowstoneWire()
    {
        this(255, 255, 0);
    }
    
    public BlockGlowstoneWire(int r, int g, int b)
    {
        super(Material.field_151594_q/*circuits*/);
        this.func_149676_a/*setBlockBounds*/(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        this.func_149649_H()/*disableStats*/;
        this.func_149658_d/*setTextureName*/("redstone_dust");
        wireColorR = r;
        wireColorG = g;
        wireColorB = b;
        wireColor = (r*65536)+(g*256)+b;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB func_149668_a/*getCollisionBoundingBoxFromPool*/(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean func_149662_c/*isOpaqueCube*/()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean func_149686_d/*renderAsNormalBlock*/()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int func_149645_b/*getRenderType*/()
    {
        return ClientProxy.glowstoneWireRenderID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int func_149720_d/*colorMultiplier*/(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return wireColor;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean func_149742_c/*canPlaceBlockAt*/(World par1World, int x, int y, int z)
    {
    	return true;
        //return World.func_147466_a/*doesBlockHaveSolidTopSurface*/(par1World, x, y - 1, z) || par1World.func_147439_a/*getBlock*/(x, y - 1, z) == Blocks.glowstone;
    }

    /**
     * Sets the strength of the wire current (0-15) for this block based on neighboring blocks and propagates to
     * neighboring redstone wires
     */
    private void func_150177_e/*updateAndPropagateCurrentStrength*/(World par1World, int par2, int par3, int par4)
    {
        //this.func_150175_a/*calculateCurrentChanges*/(par1World, par2, par3, par4, par2, par3, par4);
        ArrayList arraylist = new ArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for (int l = 0; l < arraylist.size(); ++l)
        {
            ChunkPosition chunkposition = (ChunkPosition)arraylist.get(l);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(chunkposition.field_151329_a/*x*/, chunkposition.field_151327_b/*y*/, chunkposition.field_151328_c/*z*/, this);
        }
    }

    private void func_150175_a/*calculateCurrentChanges*/(World par1World, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        
    }

    /**
     * Calls World.notifyBlocksOfNeighborChange() for all neighboring blocks, but only if the given block is a redstone
     * wire.
     */
    private void func_150172_m/*notifyWireNeighborsOfNeighborChange*/(World par1World, int par2, int par3, int par4)
    {
        if (par1World.func_147439_a/*getBlock*/(par2, par3, par4) == this)
        {
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3, par4, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2 - 1, par3, par4, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2 + 1, par3, par4, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3, par4 - 1, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3, par4 + 1, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3 - 1, par4, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3 + 1, par4, this);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void func_149726_b/*onBlockAdded*/(World par1World, int par2, int par3, int par4)
    {
        super.func_149726_b/*onBlockAdded*/(par1World, par2, par3, par4);

        if (!par1World.isRemote)
        {
            this.func_150177_e/*updateAndPropagateCurrentStrength*/(par1World, par2, par3, par4);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3 + 1, par4, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3 - 1, par4, this);
            this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 - 1, par3, par4);
            this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 + 1, par3, par4);
            this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3, par4 - 1);
            this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3, par4 + 1);

            if (par1World.func_147439_a/*getBlock*/(par2 - 1, par3, par4).func_149721_r()/*isBlockNormalCube*/)
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 - 1, par3 + 1, par4);
            }
            else
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 - 1, par3 - 1, par4);
            }

            if (par1World.func_147439_a/*getBlock*/(par2 + 1, par3, par4).func_149721_r()/*isBlockNormalCube*/)
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 + 1, par3 + 1, par4);
            }
            else
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 + 1, par3 - 1, par4);
            }

            if (par1World.func_147439_a/*getBlock*/(par2, par3, par4 - 1).func_149721_r()/*isBlockNormalCube*/)
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3 + 1, par4 - 1);
            }
            else
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3 - 1, par4 - 1);
            }

            if (par1World.func_147439_a/*getBlock*/(par2, par3, par4 + 1).func_149721_r()/*isBlockNormalCube*/)
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3 + 1, par4 + 1);
            }
            else
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3 - 1, par4 + 1);
            }
        }
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void func_149749_a/*breakBlock*/(World par1World, int par2, int par3, int par4, Block par5, int par6)
    {
        super.func_149749_a/*breakBlock*/(par1World, par2, par3, par4, par5, par6);

        if (!par1World.isRemote)
        {
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3 + 1, par4, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3 - 1, par4, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2 + 1, par3, par4, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2 - 1, par3, par4, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3, par4 + 1, this);
            par1World.func_147459_d/*notifyBlocksOfNeighborChange*/(par2, par3, par4 - 1, this);
            this.func_150177_e/*updateAndPropagateCurrentStrength*/(par1World, par2, par3, par4);
            this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 - 1, par3, par4);
            this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 + 1, par3, par4);
            this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3, par4 - 1);
            this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3, par4 + 1);

            if (par1World.func_147439_a/*getBlock*/(par2 - 1, par3, par4).func_149721_r()/*isBlockNormalCube*/)
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 - 1, par3 + 1, par4);
            }
            else
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 - 1, par3 - 1, par4);
            }

            if (par1World.func_147439_a/*getBlock*/(par2 + 1, par3, par4).func_149721_r()/*isBlockNormalCube*/)
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 + 1, par3 + 1, par4);
            }
            else
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2 + 1, par3 - 1, par4);
            }

            if (par1World.func_147439_a/*getBlock*/(par2, par3, par4 - 1).func_149721_r()/*isBlockNormalCube*/)
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3 + 1, par4 - 1);
            }
            else
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3 - 1, par4 - 1);
            }

            if (par1World.func_147439_a/*getBlock*/(par2, par3, par4 + 1).func_149721_r()/*isBlockNormalCube*/)
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3 + 1, par4 + 1);
            }
            else
            {
                this.func_150172_m/*notifyWireNeighborsOfNeighborChange*/(par1World, par2, par3 - 1, par4 + 1);
            }
        }
    }

    /**
     * Returns the current strength at the specified block if it is greater than the passed value, or the passed value
     * otherwise. Signature: (world, x, y, z, strength)
     */
    private int func_150178_a/*getMaxCurrentStrength*/(World par1World, int par2, int par3, int par4, int par5)
    {
        if (par1World.func_147439_a/*getBlock*/(par2, par3, par4) != this)
        {
            return par5;
        }
        else
        {
            int i1 = par1World.getBlockMetadata(par2, par3, par4);
            return i1 > par5 ? i1 : par5;
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void func_149695_a/*onNeighborBlockChange*/(World par1World, int par2, int par3, int par4, Block par5)
    {
        if (!par1World.isRemote)
        {
            boolean flag = this.func_149742_c/*canPlaceBlockAt*/(par1World, par2, par3, par4);

            if (flag)
            {
                this.func_150177_e/*updateAndPropagateCurrentStrength*/(par1World, par2, par3, par4);
            }
            else
            {
                this.func_149697_b/*dropBlockAsItem*/(par1World, par2, par3, par4, 0, 0);
                par1World.func_147468_f/*setBlockToAir*/(par2, par3, par4);
            }

            super.func_149695_a/*onNeighborBlockChange*/(par1World, par2, par3, par4, par5);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public Item func_149650_a/*itemDropped*/(int par1, Random par2Random, int par3)
    {
        return Items.glowstone_dust;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int func_149748_c/*isProvidingStrongPower*/(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return 0;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int func_149709_b/*isProvidingWeakPower*/(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (par1IBlockAccess.func_147439_a/*getBlock*/(par2, par3, par4) != GlowstoneWireMod.glowstoneWire)
        {
            return 0;
        }
        if (!this.wiresProvidePower)
        {
            return 0;
        }
        else
        {
            int i1 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);

            if (i1 == 0)
            {
                return 0;
            }
            else if (par5 == 1)
            {
                return i1;
            }
            else
            {
                boolean flag = func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2 - 1, par3, par4, 1) || !par1IBlockAccess.func_147439_a/*getBlock*/(par2 - 1, par3, par4).func_149721_r()/*isBlockNormalCube*/ && func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2 - 1, par3 - 1, par4, -1);
                boolean flag1 = func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2 + 1, par3, par4, 3) || !par1IBlockAccess.func_147439_a/*getBlock*/(par2 + 1, par3, par4).func_149721_r()/*isBlockNormalCube*/ && func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2 + 1, par3 - 1, par4, -1);
                boolean flag2 = func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2, par3, par4 - 1, 2) || !par1IBlockAccess.func_147439_a/*getBlock*/(par2, par3, par4 - 1).func_149721_r()/*isBlockNormalCube*/ && func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2, par3 - 1, par4 - 1, -1);
                boolean flag3 = func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2, par3, par4 + 1, 0) || !par1IBlockAccess.func_147439_a/*getBlock*/(par2, par3, par4 + 1).func_149721_r()/*isBlockNormalCube*/ && func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2, par3 - 1, par4 + 1, -1);

                if (!par1IBlockAccess.func_147439_a/*getBlock*/(par2, par3 + 1, par4).func_149721_r()/*isBlockNormalCube*/)
                {
                    if (par1IBlockAccess.func_147439_a/*getBlock*/(par2 - 1, par3, par4).func_149721_r()/*isBlockNormalCube*/ && func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2 - 1, par3 + 1, par4, -1))
                    {
                        flag = true;
                    }

                    if (par1IBlockAccess.func_147439_a/*getBlock*/(par2 + 1, par3, par4).func_149721_r()/*isBlockNormalCube*/ && func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2 + 1, par3 + 1, par4, -1))
                    {
                        flag1 = true;
                    }

                    if (par1IBlockAccess.func_147439_a/*getBlock*/(par2, par3, par4 - 1).func_149721_r()/*isBlockNormalCube*/ && func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2, par3 + 1, par4 - 1, -1))
                    {
                        flag2 = true;
                    }

                    if (par1IBlockAccess.func_147439_a/*getBlock*/(par2, par3, par4 + 1).func_149721_r()/*isBlockNormalCube*/ && func_150176_g/*isPoweredOrRepeater*/(par1IBlockAccess, par2, par3 + 1, par4 + 1, -1))
                    {
                        flag3 = true;
                    }
                }

                return !flag2 && !flag1 && !flag && !flag3 && par5 >= 2 && par5 <= 5 ? i1 : (par5 == 2 && flag2 && !flag && !flag1 ? i1 : (par5 == 3 && flag3 && !flag && !flag1 ? i1 : (par5 == 4 && flag && !flag2 && !flag3 ? i1 : (par5 == 5 && flag1 && !flag2 && !flag3 ? i1 : 0))));
            }
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean func_149744_f/*canProvidePower*/()
    {
        return false;
    }

    /**
     * Returns true if redstone wire can connect to the specified block. Params: World, X, Y, Z, side (not a normal
     * notch-side, this can be 0, 1, 2, 3 or -1)
     */
    public static boolean func_150174_f/*isPowerProviderOrWire*/(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, int par4)
    {
        return par0IBlockAccess.func_147439_a/*getBlock*/(par1, par2, par3) == GlowstoneWireMod.glowstoneWire;
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void func_149734_b/*randomDisplayTick*/(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        double d0 = (double)par2 + 0.5D + ((double)par5Random.nextFloat() - 0.5D) * 0.2D;
        double d1 = (double)((float)par3 + 0.0625F);
        double d2 = (double)par4 + 0.5D + ((double)par5Random.nextFloat() - 0.5D) * 0.2D;

        if(wireColorR > 0) par1World.spawnParticle("reddust", d0, d1, d2, (double)(((float)wireColorR)/255), (double)(((float)wireColorG)/255), (double)(((float)wireColorB)/255));
        else par1World.spawnParticle("reddust", d0, d1, d2, (double)0.001F, (double)(((float)wireColorG)/255), (double)(((float)wireColorB)/255));
    }

    /**
     * Returns true if the block coordinate passed can provide power, or is a redstone wire, or if its a repeater that
     * is powered.
     */
    public static boolean func_150176_g/*isPoweredOrRepeater*/(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, int par4)
    {
        return par0IBlockAccess.func_147439_a/*getBlock*/(par1, par2, par3) == GlowstoneWireMod.glowstoneWire;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public Item func_149694_d/*itemPicked*/(World par1World, int par2, int par3, int par4)
    {
        return Items.glowstone_dust;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void func_149651_a/*registerIcons*/(IIconRegister par1IconRegister)
    {
        BlockGlowstoneWire.field_94413_c = par1IconRegister.registerIcon(this.func_149641_N/*getTextureName*/() + "_" + "cross");
        BlockGlowstoneWire.field_94410_cO = par1IconRegister.registerIcon(this.func_149641_N/*getTextureName*/() + "_" + "line");
        BlockGlowstoneWire.field_94411_cP = par1IconRegister.registerIcon(this.func_149641_N/*getTextureName*/() + "_" + "cross_overlay");
        BlockGlowstoneWire.field_94412_cQ = par1IconRegister.registerIcon(this.func_149641_N/*getTextureName*/() + "_" + "line_overlay");
        this.field_149761_L/*blockIcon*/ = BlockGlowstoneWire.field_94413_c;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon func_150173_e(String par0Str)
    {
        return par0Str.equals("cross") ? field_94413_c : (par0Str.equals("line") ? field_94410_cO : (par0Str.equals("cross_overlay") ? field_94411_cP : (par0Str.equals("line_overlay") ? field_94412_cQ : null)));
    }
}
