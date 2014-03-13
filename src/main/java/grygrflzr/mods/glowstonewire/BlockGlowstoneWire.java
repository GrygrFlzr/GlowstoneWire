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
        super(Material.circuits);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        this.disableStats();
        this.setBlockTextureName("redstone_dust");
        wireColorR = r;
        wireColorG = g;
        wireColorB = b;
        wireColor = (r*65536)+(g*256)+b;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return ClientProxy.glowstoneWireRenderID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return wireColor;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int x, int y, int z)
    {
        return World.doesBlockHaveSolidTopSurface(par1World, x, y - 1, z) || par1World.getBlock(x, y - 1, z) == Blocks.glowstone;
    }

    /**
     * Sets the strength of the wire current (0-15) for this block based on neighboring blocks and propagates to
     * neighboring redstone wires
     */
    private void updateAndPropagateCurrentStrength(World par1World, int par2, int par3, int par4)
    {
        //this.calculateCurrentChanges(par1World, par2, par3, par4, par2, par3, par4);
        ArrayList arraylist = new ArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for (int l = 0; l < arraylist.size(); ++l)
        {
            ChunkPosition chunkposition = (ChunkPosition)arraylist.get(l);
            par1World.notifyBlocksOfNeighborChange(chunkposition.chunkPosX, chunkposition.chunkPosY, chunkposition.chunkPosZ, this);
        }
    }

    private void calculateCurrentChanges(World par1World, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        
    }

    /**
     * Calls World.notifyBlocksOfNeighborChange() for all neighboring blocks, but only if the given block is a redstone
     * wire.
     */
    private void notifyWireNeighborsOfNeighborChange(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlock(par2, par3, par4) == this)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this);
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this);
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this);
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this);
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);

        if (!par1World.isRemote)
        {
            this.updateAndPropagateCurrentStrength(par1World, par2, par3, par4);
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3, par4 - 1);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3, par4 + 1);

            if (par1World.getBlock(par2 - 1, par3, par4).isNormalCube())
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3 + 1, par4);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3 - 1, par4);
            }

            if (par1World.getBlock(par2 + 1, par3, par4).isNormalCube())
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3 + 1, par4);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3 - 1, par4);
            }

            if (par1World.getBlock(par2, par3, par4 - 1).isNormalCube())
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 + 1, par4 - 1);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 - 1, par4 - 1);
            }

            if (par1World.getBlock(par2, par3, par4 + 1).isNormalCube())
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 + 1, par4 + 1);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 - 1, par4 + 1);
            }
        }
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6)
    {
        super.breakBlock(par1World, par2, par3, par4, par5, par6);

        if (!par1World.isRemote)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this);
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this);
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this);
            this.updateAndPropagateCurrentStrength(par1World, par2, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3, par4);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3, par4 - 1);
            this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3, par4 + 1);

            if (par1World.getBlock(par2 - 1, par3, par4).isNormalCube())
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3 + 1, par4);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 - 1, par3 - 1, par4);
            }

            if (par1World.getBlock(par2 + 1, par3, par4).isNormalCube())
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3 + 1, par4);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2 + 1, par3 - 1, par4);
            }

            if (par1World.getBlock(par2, par3, par4 - 1).isNormalCube())
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 + 1, par4 - 1);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 - 1, par4 - 1);
            }

            if (par1World.getBlock(par2, par3, par4 + 1).isNormalCube())
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 + 1, par4 + 1);
            }
            else
            {
                this.notifyWireNeighborsOfNeighborChange(par1World, par2, par3 - 1, par4 + 1);
            }
        }
    }

    /**
     * Returns the current strength at the specified block if it is greater than the passed value, or the passed value
     * otherwise. Signature: (world, x, y, z, strength)
     */
    private int getMaxCurrentStrength(World par1World, int par2, int par3, int par4, int par5)
    {
        if (par1World.getBlock(par2, par3, par4) != this)
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
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
    {
        if (!par1World.isRemote)
        {
            boolean flag = this.canPlaceBlockAt(par1World, par2, par3, par4);

            if (flag)
            {
                this.updateAndPropagateCurrentStrength(par1World, par2, par3, par4);
            }
            else
            {
                this.dropBlockAsItem(par1World, par2, par3, par4, 0, 0);
                par1World.setBlockToAir(par2, par3, par4);
            }

            super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public Item itemDropped(int par1, Random par2Random, int par3)
    {
        return Items.glowstone_dust;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return 0;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (par1IBlockAccess.getBlock(par2, par3, par4) != GlowstoneWireMod.glowstoneWire)
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
                boolean flag = isPoweredOrRepeater(par1IBlockAccess, par2 - 1, par3, par4, 1) || !par1IBlockAccess.getBlock(par2 - 1, par3, par4).isNormalCube() && isPoweredOrRepeater(par1IBlockAccess, par2 - 1, par3 - 1, par4, -1);
                boolean flag1 = isPoweredOrRepeater(par1IBlockAccess, par2 + 1, par3, par4, 3) || !par1IBlockAccess.getBlock(par2 + 1, par3, par4).isNormalCube() && isPoweredOrRepeater(par1IBlockAccess, par2 + 1, par3 - 1, par4, -1);
                boolean flag2 = isPoweredOrRepeater(par1IBlockAccess, par2, par3, par4 - 1, 2) || !par1IBlockAccess.getBlock(par2, par3, par4 - 1).isNormalCube() && isPoweredOrRepeater(par1IBlockAccess, par2, par3 - 1, par4 - 1, -1);
                boolean flag3 = isPoweredOrRepeater(par1IBlockAccess, par2, par3, par4 + 1, 0) || !par1IBlockAccess.getBlock(par2, par3, par4 + 1).isNormalCube() && isPoweredOrRepeater(par1IBlockAccess, par2, par3 - 1, par4 + 1, -1);

                if (!par1IBlockAccess.getBlock(par2, par3 + 1, par4).isNormalCube())
                {
                    if (par1IBlockAccess.getBlock(par2 - 1, par3, par4).isNormalCube() && isPoweredOrRepeater(par1IBlockAccess, par2 - 1, par3 + 1, par4, -1))
                    {
                        flag = true;
                    }

                    if (par1IBlockAccess.getBlock(par2 + 1, par3, par4).isNormalCube() && isPoweredOrRepeater(par1IBlockAccess, par2 + 1, par3 + 1, par4, -1))
                    {
                        flag1 = true;
                    }

                    if (par1IBlockAccess.getBlock(par2, par3, par4 - 1).isNormalCube() && isPoweredOrRepeater(par1IBlockAccess, par2, par3 + 1, par4 - 1, -1))
                    {
                        flag2 = true;
                    }

                    if (par1IBlockAccess.getBlock(par2, par3, par4 + 1).isNormalCube() && isPoweredOrRepeater(par1IBlockAccess, par2, par3 + 1, par4 + 1, -1))
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
    public boolean canProvidePower()
    {
        return false;
    }

    /**
     * Returns true if redstone wire can connect to the specified block. Params: World, X, Y, Z, side (not a normal
     * notch-side, this can be 0, 1, 2, 3 or -1)
     */
    public static boolean isPowerProviderOrWire(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, int par4)
    {
        return par0IBlockAccess.getBlock(par1, par2, par3) == GlowstoneWireMod.glowstoneWire;
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
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
    public static boolean isPoweredOrRepeater(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, int par4)
    {
        return par0IBlockAccess.getBlock(par1, par2, par3) == GlowstoneWireMod.glowstoneWire;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public Item itemPicked(World par1World, int par2, int par3, int par4)
    {
        return Items.glowstone_dust;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IIconRegister par1IconRegister)
    {
        BlockGlowstoneWire.field_94413_c = par1IconRegister.registerIcon(this.getTextureName() + "_" + "cross");
        BlockGlowstoneWire.field_94410_cO = par1IconRegister.registerIcon(this.getTextureName() + "_" + "line");
        BlockGlowstoneWire.field_94411_cP = par1IconRegister.registerIcon(this.getTextureName() + "_" + "cross_overlay");
        BlockGlowstoneWire.field_94412_cQ = par1IconRegister.registerIcon(this.getTextureName() + "_" + "line_overlay");
        this.blockIcon = BlockGlowstoneWire.field_94413_c;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getRedstoneWireIcon(String par0Str)
    {
        return par0Str.equals("cross") ? field_94413_c : (par0Str.equals("line") ? field_94410_cO : (par0Str.equals("cross_overlay") ? field_94411_cP : (par0Str.equals("line_overlay") ? field_94412_cQ : null)));
    }
}
