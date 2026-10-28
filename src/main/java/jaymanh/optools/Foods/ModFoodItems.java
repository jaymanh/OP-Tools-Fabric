package jaymanh.optools.Foods;

import net.minecraft.entity.player.HungerConstants;
import jaymanh.optools.OpTools;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static jaymanh.optools.Tools.ModTools.*;

public class ModFoodItems {
    public static final Item SUPER_MEAT = register(new Item(new Item.Settings().rarity(Rarity.RARE).food(new FoodComponent.Builder()
                    .nutrition(20)
                    .saturationModifier(HungerConstants.FOOD_SATURATION_SUPERNATURAL)
                    .build())
                    .registryKey(key("super_meat"))),
            "super_meat");

    public static void initialise(){
        ItemGroupEvents.modifyEntriesEvent(OP_TOOLS_ITEM_GROUP_KEY).register((ItemGroup) -> {
            ItemGroup.add(SUPER_MEAT);
        });
    }
}
