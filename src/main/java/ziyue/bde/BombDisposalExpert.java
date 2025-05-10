package ziyue.bde;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author ZiYueCommentary
 * @since 1.0.0
 */

@Mod(BombDisposalExpert.MOD_ID)
public class BombDisposalExpert
{
    public static final String MOD_ID = "bde";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Block> TNT_NO_GUNPOWDER = BLOCKS.register("tnt_no_gunpowder", () -> new Block(Block.Properties.copy(Blocks.TNT))
    {
        @Override
        public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
            return 10;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
            return 5;
        }
    });
    public static final RegistryObject<Item> TNT_NO_GUNPOWDER_ITEM = ITEMS.register("tnt_no_gunpowder", () -> new BlockItem(TNT_NO_GUNPOWDER.get(), new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));

    public BombDisposalExpert() {
        LOGGER.info("Bomb Disposal Expert! Made by ZiYueCommentary.");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
