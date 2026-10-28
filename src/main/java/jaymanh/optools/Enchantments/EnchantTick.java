package jaymanh.optools.Enchantments;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static jaymanh.optools.OpTools.MOD_ID;

public class EnchantTick implements ServerTickEvents.EndWorldTick {
    static final int WATER_FLOAT_TICKS = 100;
    private static final String LEGACY_GRAVITY_TAG = "Gravity";

    static final AttachmentType<Long> WATER_FLOAT_UNTIL = AttachmentRegistry.createPersistent(
            Identifier.of(MOD_ID, "water_float_until"),
            Codec.LONG
    );

    private static final Set<Entity> FLOATING = new HashSet<>();

    public static void initialise() {
        ServerTickEvents.END_WORLD_TICK.register(new EnchantTick());
        ServerEntityEvents.ENTITY_LOAD.register(EnchantTick::onEntityLoad);
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> FLOATING.remove(entity));
    }

    public static void startWaterFloat(Entity entity, ServerWorld world) {
        clearLegacyFloatTags(entity);
        entity.setAttached(WATER_FLOAT_UNTIL, world.getTime() + WATER_FLOAT_TICKS);
        entity.setNoGravity(true);
        FLOATING.add(entity);
    }

    private static void onEntityLoad(Entity entity, ServerWorld world) {
        migrateLegacyFloat(entity, world);
        Long until = entity.getAttached(WATER_FLOAT_UNTIL);
        if (until == null) {
            return;
        }
        if (world.getTime() >= until) {
            endWaterFloat(entity);
        } else {
            entity.setNoGravity(true);
            FLOATING.add(entity);
        }
    }

    private static void migrateLegacyFloat(Entity entity, ServerWorld world) {
        if (!entity.getCommandTags().contains(LEGACY_GRAVITY_TAG)) {
            return;
        }
        int counted = 0;
        for (String tag : new ArrayList<>(entity.getCommandTags())) {
            if (!tag.startsWith("Num")) {
                continue;
            }
            try {
                counted = Integer.parseInt(tag.substring(3));
            } catch (NumberFormatException ignored) {
                continue;
            }
            entity.removeCommandTag(tag);
        }
        entity.removeCommandTag(LEGACY_GRAVITY_TAG);
        if (entity.hasAttached(WATER_FLOAT_UNTIL)) {
            return;
        }
        int remaining = Math.max(0, WATER_FLOAT_TICKS - counted);
        if (remaining > 0) {
            entity.setAttached(WATER_FLOAT_UNTIL, world.getTime() + remaining);
            entity.setNoGravity(true);
            FLOATING.add(entity);
        } else {
            entity.setNoGravity(false);
        }
    }

    private static void clearLegacyFloatTags(Entity entity) {
        if (!entity.getCommandTags().contains(LEGACY_GRAVITY_TAG)) {
            return;
        }
        List<String> tags = new ArrayList<>(entity.getCommandTags());
        for (String tag : tags) {
            if (tag.equals(LEGACY_GRAVITY_TAG) || tag.startsWith("Num")) {
                entity.removeCommandTag(tag);
            }
        }
    }

    private static void endWaterFloat(Entity entity) {
        entity.setNoGravity(false);
        entity.removeAttached(WATER_FLOAT_UNTIL);
        FLOATING.remove(entity);
    }

    @Override
    public void onEndTick(ServerWorld world) {
        long now = world.getTime();
        Iterator<Entity> iterator = FLOATING.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (entity.isRemoved()) {
                iterator.remove();
                continue;
            }
            if (world.getEntity(entity.getUuid()) != entity) {
                continue;
            }
            Long until = entity.getAttached(WATER_FLOAT_UNTIL);
            if (until == null || now >= until) {
                entity.setNoGravity(false);
                entity.removeAttached(WATER_FLOAT_UNTIL);
                iterator.remove();
            }
        }
    }
}
