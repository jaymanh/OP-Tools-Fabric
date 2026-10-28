package jaymanh.optools.Enchantments;

import jaymanh.optools.OpTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElementalDamageEnchantments {
    private static final int LIGHTNING_COOLDOWN_TICKS = 20;
    private static final Map<UUID, Long> NEXT_LIGHTNING_TICK = new HashMap<>();

    private static void createFireRadius(BlockPos center, Level world, int radius) {
        if (radius < 0) {
            return;
        }
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos posToCheck = center.offset(x, y, z);
                    if (x * x + y * y + z * z <= radius * radius) {
                        if (world.isEmptyBlock(posToCheck) || world.getBlockState(posToCheck).getBlock() == Blocks.TALL_GRASS || world.getBlockState(posToCheck).getBlock() == Blocks.SHORT_GRASS) {
                            world.setBlockAndUpdate(posToCheck, Blocks.FIRE.defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    private static boolean resistsFire(ServerLevel world, Entity user) {
        if (user.fireImmune()) {
            return true;
        }
        if (!(user instanceof LivingEntity living)) {
            return false;
        }
        if (living.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return true;
        }
        return ModEnchantments.entityLevel(world, living, Enchantments.FIRE_PROTECTION) > 0;
    }

    public static void initialise() {
        OpTools.register(Identifier.parse("elemental_fire"), (world, level, context, user, pos) -> {
            if (resistsFire(world, user)) {
                return;
            }
            int radius = level - 1;
            createFireRadius(user.blockPosition(), world, radius);
        });

        OpTools.register(Identifier.parse("elemental_water"), (world, level, context, user, pos) -> {
            user.setDeltaMovement(user.getDeltaMovement().x, 0.02, user.getDeltaMovement().z);
            EnchantTick.startWaterFloat(user, world);
            world.sendParticles(ParticleTypes.BUBBLE, user.getX(), user.getY(), user.getZ(), 100, 0.5, 1, 0.5, 10);
        });

        OpTools.register(Identifier.parse("elemental_earth"), (world, level, context, user, pos) -> {
            user.setDeltaMovement(Vec3.ZERO);
            user.move(MoverType.SELF, new Vec3(0, -0.8 * level, 0));
            user.setDeltaMovement(Vec3.ZERO);
            user.resetFallDistance();
            if (user instanceof Mob mob) {
                mob.getNavigation().stop();
            }
        });

        OpTools.register(Identifier.parse("elemental_lightning"), (world, level, context, user, pos) -> {
            Entity source = context.owner() != null ? context.owner() : user;
            long now = world.getGameTime();
            if (NEXT_LIGHTNING_TICK.size() > 128) {
                NEXT_LIGHTNING_TICK.entrySet().removeIf(entry -> entry.getValue() <= now);
            }
            Long nextAllowed = NEXT_LIGHTNING_TICK.get(source.getUUID());
            if (nextAllowed != null && now < nextAllowed) {
                return;
            }
            NEXT_LIGHTNING_TICK.put(source.getUUID(), now + LIGHTNING_COOLDOWN_TICKS);

            LightningBolt lightning = new LightningBolt(EntityTypes.LIGHTNING_BOLT, world);
            lightning.setPos(user.getX(), user.getY(), user.getZ());
            lightning.setVisualOnly(false);
            world.addFreshEntity(lightning);
        });

        OpTools.register(Identifier.parse("elemental_ice"), (world, level, context, user, pos) -> {
            if (user instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, Math.max(0, level - 1)));
            }
        });
    }
}
