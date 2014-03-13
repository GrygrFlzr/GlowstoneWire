package grygrflzr.mods.glowstonewire;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    public static int glowstoneWireRenderID;
    @Override
    public void registerRenderInformation() {
        glowstoneWireRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new GlowstoneWireBlocksRenderer(glowstoneWireRenderID));
    }
}
