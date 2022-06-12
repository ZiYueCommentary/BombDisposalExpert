package ziyue.bde;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ziyue.bde.blocks.BlockTntNoGunpowder;
import ziyue.bde.registry.RegistryObject;

public class Main
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.NAME);
    public static final RegistryObject<Block> TNT = new RegistryObject<>(BlockTntNoGunpowder::new);

    public static void init(RegisterBlockItem registerBlockItem) {
        registerBlockItem.accept("tnt_no_gunpowder", TNT, CreativeModeTab.TAB_REDSTONE);
    }

    @FunctionalInterface
    public interface RegisterBlockItem {
        void accept(String string, RegistryObject<Block> block, CreativeModeTab tab);
    }
}
