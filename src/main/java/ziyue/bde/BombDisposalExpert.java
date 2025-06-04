package ziyue.bde;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author ZiYueCommentary
 * @since 1.0.0
 */

@Mod(BombDisposalExpert.MOD_ID)
@ParametersAreNonnullByDefault
public class BombDisposalExpert
{
    public static final String MOD_ID = "bde";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredHolder<Block, Block> TNT_NO_GUNPOWDER = BLOCKS.register("tnt_no_gunpowder", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.TNT))
    {
        @Override
        public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return true;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 10;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
        }
    });
    public static final DeferredHolder<Item, BlockItem> TNT_NO_GUNPOWDER_ITEM = ITEMS.register("tnt_no_gunpowder", () -> new BlockItem(TNT_NO_GUNPOWDER.get(), new Item.Properties()));

    public static final TagKey<Item> DEFUSER = TagKey.create(Registries.ITEM,
            new ResourceLocation(MOD_ID,"defuser"));

    public BombDisposalExpert(IEventBus bus)
    {
        LOGGER.info("Bomb Disposal Expert! Made by ZiYueCommentary.");

        BLOCKS.register(bus);
        ITEMS.register(bus);

        bus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS)
            event.accept(TNT_NO_GUNPOWDER_ITEM.get());
    }
}
