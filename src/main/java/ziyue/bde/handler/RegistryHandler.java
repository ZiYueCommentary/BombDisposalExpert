package ziyue.bde.handler;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static ziyue.bde.BombDisposalExpert.TNT_NO_GUNPOWDER;
import static ziyue.bde.BombDisposalExpert.TNT_NO_GUNPOWDER_ITEM;

@EventBusSubscriber
public class RegistryHandler
{
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(TNT_NO_GUNPOWDER_ITEM);
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(TNT_NO_GUNPOWDER_ITEM, 0, new ModelResourceLocation("bde:tnt_no_gunpowder", "inventory"));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(TNT_NO_GUNPOWDER);
    }
}