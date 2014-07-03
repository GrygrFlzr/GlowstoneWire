package grygrflzr.mods.glowstonewire;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.IModGuiFactory;

public class GlowstoneWireGUIFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
        
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return GlowstoneWireConfigGUI.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(
            RuntimeOptionCategoryElement element) {
        return null;
    }

}
