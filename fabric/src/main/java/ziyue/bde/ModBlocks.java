package ziyue.bde;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

import static ziyue.bde.BombDisposalExpert.MOD_ID;

public class ModBlocks
{
    public static final Block TNT_NO_GUNPOWDER = register("tnt_no_gunpowder", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.TNT).ignitedByLava());
    public static final TagKey<Item> DEFUSER = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "defuser"));

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS)
                .register((creativeTab) -> creativeTab.accept(TNT_NO_GUNPOWDER));
        FlammableBlockRegistry.getDefaultInstance().add(TNT_NO_GUNPOWDER, 10, 5);
        UseBlockCallback.EVENT.register((player, level, interactionHand, blockHitResult) -> {
            final BlockPos blockPos = blockHitResult.getBlockPos();
            if (!player.getItemInHand(interactionHand).is(DEFUSER) || !level.getBlockState(blockPos).is(Blocks.TNT))
                return InteractionResult.PASS;
            player.getItemInHand(interactionHand).hurtAndBreak(1, player, interactionHand.asEquipmentSlot());
            level.setBlockAndUpdate(blockPos, TNT_NO_GUNPOWDER.defaultBlockState());
            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS);
            level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), new ItemStack(Items.GUNPOWDER)));
            return InteractionResult.SUCCESS;
        });
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof PrimedTnt tnt) || player.getItemInHand(hand).is(DEFUSER))
                return InteractionResult.PASS;
            player.getItemInHand(hand).hurtAndBreak(1, player, hand.asEquipmentSlot());
            tnt.spawnAtLocation(serverLevel, new ItemStack(TNT_NO_GUNPOWDER));
            tnt.playSound(SoundEvents.FIRE_EXTINGUISH);
            tnt.kill(serverLevel);
            return InteractionResult.SUCCESS;
        });
    }

    private static Block register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        final Identifier identifier = Identifier.fromNamespaceAndPath(MOD_ID, path);
        final ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, identifier);
        final ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, identifier);
        final Block block = factory.apply(properties.setId(blockKey));
        BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }
}
