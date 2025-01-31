package aartcraft.togglebabymobs.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgePotionItem extends Item {
    private static final Logger LOGGER = LoggerFactory.getLogger("toggle-baby-mobs");

    public AgePotionItem(Settings settings) {
        super(settings);
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(entity instanceof LivingEntity living)) return ActionResult.PASS;
            
            ItemStack mainHand = player.getMainHandStack();
            ItemStack offHand = player.getOffHandStack();
            boolean holdingItem = mainHand.isOf(this) || offHand.isOf(this);
            
            if (!holdingItem) return ActionResult.PASS;

            ActionResult result = tryToggleBabyState(
                mainHand.isOf(this) ? mainHand : offHand,
                player,
                living
            );

            if (result.isAccepted()) {
                player.swingHand(hand);
                return ActionResult.CONSUME;
            }
            
            return ActionResult.PASS;
        });
    }

    private ActionResult tryToggleBabyState(ItemStack stack, PlayerEntity player, LivingEntity entity) {
        LOGGER.debug("Attempting to toggle baby state on {} (Class: {})", entity, entity.getClass().getName());

        boolean modified = false;
        NbtCompound nbt = new NbtCompound();
        entity.writeCustomDataToNbt(nbt);
        
        // Log full NBT data for inspection
        LOGGER.debug("Entity NBT before modification: {}", nbt);
        
        // Check for byte IsBaby tag first (most common case)
        if (nbt.contains("IsBaby", NbtElement.BYTE_TYPE)) {
            byte isBabyByte = nbt.getByte("IsBaby");
            LOGGER.debug("Found byte IsBaby tag: {}", isBabyByte);
            byte newValue = (byte) (isBabyByte == 0 ? 1 : 0);
            nbt.putByte("IsBaby", newValue);
            modified = true;
        }

        // Check for boolean IsBaby tag as fallback
        else if (nbt.contains("IsBaby")) {
            boolean isBaby = nbt.getBoolean("IsBaby");
            LOGGER.debug("Found boolean IsBaby tag: {}", isBaby);
            nbt.putBoolean("IsBaby", !isBaby);
            modified = true;
        }


        else {
            LOGGER.debug("No IsBaby tag found in NBT");
        }

        if (modified) {
            LOGGER.debug("Modified NBT: {}", nbt);
            entity.readCustomDataFromNbt(nbt);
            spawnEffects(entity);
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            LOGGER.info("Successfully toggled baby state for {}", entity.getType());
            return ActionResult.SUCCESS;
        }

        // Entity-specific handling
        if (entity instanceof PassiveEntity passiveEntity) {
            LOGGER.debug("Passive entity detected - current baby state: {}", passiveEntity.isBaby());
            passiveEntity.setBaby(!passiveEntity.isBaby());
            LOGGER.info("Toggled baby state for passive {} to {}", passiveEntity.getType(), passiveEntity.isBaby());
            spawnEffects(passiveEntity);
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        // Final check for unexpected cases
        LOGGER.warn("Failed to modify entity - Type: {}, NBT: {}", entity.getType(), nbt);
        return ActionResult.PASS;
    }

    private void spawnEffects(LivingEntity entity) {
        World world = entity.getWorld();
        Vec3d pos = entity.getPos();

        // Server side: send particle/sound packet to all clients
        if (!world.isClient) {
            ((ServerWorld) world).spawnParticles(
                    ParticleTypes.WHITE_SMOKE,
                    pos.getX(),
                    pos.getY() + 0.5,
                    pos.getZ(),
                    30,
                    0.5, 0.5, 0.5,
                    0.1
            );

            world.playSound(
                    null,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    SoundEvents.UI_TOAST_OUT,
                    SoundCategory.NEUTRAL,
                    2.0f,
                    2.0f
            );
        }
    }
}
