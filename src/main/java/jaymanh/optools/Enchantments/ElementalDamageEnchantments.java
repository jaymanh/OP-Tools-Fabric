package jaymanh.optools.Enchantments;

import jaymanh.optools.OpTools;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElementalDamageEnchantments {
    private static final int LIGHTNING_COOLDOWN_TICKS = 20;
    private static final Map<UUID, Long> NEXT_LIGHTNING_TICK = new HashMap<>();

    private static void createFireRadius(BlockPos center, World world, int radius) {
        if (radius < 0) {
            return;
        }
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos posToCheck = center.add(x, y, z);
                    if (x * x + y * y + z * z <= radius * radius) {
                        if (world.isAir(posToCheck) || world.getBlockState(posToCheck).getBlock() == Blocks.TALL_GRASS || world.getBlockState(posToCheck).getBlock() == Blocks.SHORT_GRASS) {
                            world.setBlockState(posToCheck, Blocks.FIRE.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    private static boolean resistsFire(ServerWorld world, Entity user) {
        if (user.isFireImmune()) {
            return true;
        }
        if (!(user instanceof LivingEntity living)) {
            return false;
        }
        if (living.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            return true;
        }
        return ModEnchantments.entityLevel(world, living, Enchantments.FIRE_PROTECTION) > 0;
    }

    public static void initialise() {
        OpTools.register(Identifier.of("elemental_fire"), (world, level, context, user, pos) -> {
            if (resistsFire(world, user)) {
                return;
            }
            int radius = level - 1;
            createFireRadius(user.getBlockPos(), world, radius);
        });

        OpTools.register(Identifier.of("elemental_water"), (world, level, context, user, pos) -> {
            user.setVelocity(user.getVelocity().x, 0.02, user.getVelocity().z);
            EnchantTick.startWaterFloat(user, world);
            world.spawnParticles(ParticleTypes.BUBBLE, user.getX(), user.getY(), user.getZ(), 100, 0.5, 1, 0.5, 10);
        });

        OpTools.register(Identifier.of("elemental_earth"), (world, level, context, user, pos) -> {
            user.setVelocity(Vec3d.ZERO);
            user.move(MovementType.SELF, new Vec3d(0, -0.8 * level, 0));
            user.setVelocity(Vec3d.ZERO);
            user.fallDistance = 0;
            if (user instanceof MobEntity mob) {
                mob.getNavigation().stop();
            }
        });

        OpTools.register(Identifier.of("elemental_lightning"), (world, level, context, user, pos) -> {
            Entity source = context.owner() != null ? context.owner() : user;
            long now = world.getTime();
            if (NEXT_LIGHTNING_TICK.size() > 128) {
                NEXT_LIGHTNING_TICK.entrySet().removeIf(entry -> entry.getValue() <= now);
            }
            Long nextAllowed = NEXT_LIGHTNING_TICK.get(source.getUuid());
            if (nextAllowed != null && now < nextAllowed) {
                return;
            }
            NEXT_LIGHTNING_TICK.put(source.getUuid(), now + LIGHTNING_COOLDOWN_TICKS);

            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.setPosition(new Vec3d(user.getX(), user.getY(), user.getZ()));
            world.spawnEntity(lightning);
        });

        OpTools.register(Identifier.of("elemental_ice"), (world, level, context, user, pos) -> {
            if (user instanceof LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, Math.max(0, level - 1)));
            }
        });
    }
}
