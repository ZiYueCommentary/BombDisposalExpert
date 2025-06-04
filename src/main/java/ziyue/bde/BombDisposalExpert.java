package ziyue.bde;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

/**
 * @author ZiYueCommentary
 * @since 1.0.0
 */

public class BombDisposalExpert implements ModInitializer
{
    public static final String MOD_ID = "bde";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Block TNT_NO_GUNPOWDER = register("tnt_no_gunpowder", Block::new, Block.Settings.copy(Blocks.TNT));
    public static final TagKey<Item> DEFUSER = TagKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "defuser"));
    @Override
    public void onInitialize() {
        LOGGER.info("Bomb Disposal Expert! Made by ZiYueCommentary.");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.add(TNT_NO_GUNPOWDER));
        FlammableBlockRegistry.getDefaultInstance().add(TNT_NO_GUNPOWDER, 10, 5);
    }

    private static Block register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = Identifier.of(MOD_ID, path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);
        return block;
    }
}