package ziyue.bde;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BombDisposalExpert implements ModInitializer
{
    public static final String MOD_ID = "bde";
    public static final Block TNT_NO_GUNPOWDER = new Block(FabricBlockSettings.copyOf(Blocks.TNT));

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "tnt_no_gunpowder"), TNT_NO_GUNPOWDER);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "tnt_no_gunpowder"), new BlockItem(TNT_NO_GUNPOWDER, new FabricItemSettings()));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.add(TNT_NO_GUNPOWDER));

        // TntBlock
        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (!player.isSpectator()) {
                BlockPos pos = blockHitResult.getBlockPos();
                if ((world.getBlockState(pos).getBlock() == Blocks.TNT) && (player.isHolding(Items.SHEARS))) {
                    Entity dummy = new ItemEntity(EntityType.ITEM, world);
                    dummy.setPos(pos.getX(), pos.getY(), pos.getZ());

                    world.setBlockState(pos, TNT_NO_GUNPOWDER.getDefaultState());
                    world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    dummy.dropStack(new ItemStack(Items.GUNPOWDER), 1F);

                    dummy.kill();
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
        // TntEntity
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!player.isSpectator()) {
                if ((entity instanceof TntEntity) && (player.isHolding(Items.SHEARS))) {
                    entity.dropItem(TNT_NO_GUNPOWDER);
                    entity.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.3F, 1.0F);
                    entity.kill();
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
}
