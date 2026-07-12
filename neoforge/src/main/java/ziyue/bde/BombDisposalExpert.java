package ziyue.bde;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Supplier;

import static ziyue.bde.ModBlocks.*;

@Mod(BombDisposalExpert.MODID)
public class BombDisposalExpert
{
    public static final String MODID = "bde";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final Supplier<AttachmentType<Boolean>> CREEPER_IS_NEUTRALIZED = ATTACHMENT_TYPES.register(
            "creeper_is_neutralized", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL.fieldOf("creeper_is_neutralized")).build()
    );


    public BombDisposalExpert(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Bomb Disposal Expert! Made by ZiYueCommentary.");

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onUseTntBlock);
        NeoForge.EVENT_BUS.addListener(this::onUsePrimedTntBlock);
        modEventBus.addListener(this::addCreative);

        ModBlocks.initialize();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(TNT_NO_GUNPOWDER_ITEM);
        }
    }

    private void onUseTntBlock(UseItemOnBlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) ||
                !event.getPlayer().getItemInHand(event.getHand()).is(DEFUSER) ||
                !level.getBlockState(event.getPos()).is(Blocks.TNT)) {
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }

        final BlockPos blockPos = event.getPos();
        level.setBlockAndUpdate(blockPos, TNT_NO_GUNPOWDER.get().defaultBlockState());
        event.getPlayer().getItemInHand(event.getHand()).hurtAndBreak(1, event.getPlayer(), event.getHand().asEquipmentSlot());
        level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS);
        level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY() + 2, blockPos.getZ(), new ItemStack(Items.GUNPOWDER)));
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private void onUsePrimedTntBlock(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getLevel() instanceof ServerLevel level) ||
                !event.getEntity().getItemInHand(event.getHand()).is(DEFUSER) ||
                !(event.getTarget() instanceof PrimedTnt tnt)) {
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }

        event.getEntity().getItemInHand(event.getHand()).hurtAndBreak(1, event.getEntity(), event.getHand().asEquipmentSlot());
        tnt.spawnAtLocation(level, new ItemStack(TNT_NO_GUNPOWDER));
        tnt.playSound(SoundEvents.FIRE_EXTINGUISH);
        tnt.kill(level);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
