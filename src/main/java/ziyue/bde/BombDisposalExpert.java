package ziyue.bde;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author ZiYueCommentary
 * @since 1.0.0
 */

public class BombDisposalExpert implements ModInitializer
{
    public static final String MOD_ID = "bde";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Block TNT_NO_GUNPOWDER = new Block(AbstractBlock.Settings.copy(Blocks.TNT));

    @Override
    public void onInitialize() {
        LOGGER.info("Bomb Disposal Expert! Made by ZiYueCommentary.");

        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "tnt_no_gunpowder"), TNT_NO_GUNPOWDER);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "tnt_no_gunpowder"), new BlockItem(TNT_NO_GUNPOWDER, new Item.Settings().group(ItemGroup.REDSTONE)));
        FlammableBlockRegistry.getDefaultInstance().add(TNT_NO_GUNPOWDER, 10, 5);
    }
}