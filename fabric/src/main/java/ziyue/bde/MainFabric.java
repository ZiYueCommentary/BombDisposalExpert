package ziyue.bde;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import ziyue.bde.registry.RegistryObject;

public class MainFabric implements ModInitializer
{
	@Override
	public void onInitialize() {
		Main.init(MainFabric::registerBlock);
	}

	private static void registerBlock(String path, RegistryObject<Block> block, CreativeModeTab itemGroup) {
		Registry.register(Registry.BLOCK, new ResourceLocation(Reference.MOD_ID, path), block.get());
		Registry.register(Registry.ITEM, new ResourceLocation(Reference.MOD_ID, path), new BlockItem(block.get(), new Item.Properties().tab(itemGroup)));
	}
}
