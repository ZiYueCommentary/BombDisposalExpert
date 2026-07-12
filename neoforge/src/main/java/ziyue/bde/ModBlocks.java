package ziyue.bde;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import static ziyue.bde.BombDisposalExpert.*;

public class ModBlocks
{
    public static final TagKey<Item> DEFUSER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, "defuser"));
    public static final DeferredBlock<Block> TNT_NO_GUNPOWDER = BLOCKS.registerBlock("tnt_no_gunpowder", properties -> new Block(properties)
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
    }, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TNT));
    public static final DeferredItem<BlockItem> TNT_NO_GUNPOWDER_ITEM = ITEMS.registerSimpleBlockItem("tnt_no_gunpowder", TNT_NO_GUNPOWDER);

    public static void initialize() {

    }
}
