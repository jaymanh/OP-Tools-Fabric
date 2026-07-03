package jaymanh.optools.TagGenerator;

import jaymanh.optools.Armor.ModArmor;
import jaymanh.optools.Foods.ModFoodItems;
import jaymanh.optools.Items.ModItems;
import jaymanh.optools.Tools.ModTools;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import java.util.concurrent.CompletableFuture;

import static jaymanh.optools.OpTools.MOD_ID;

public class OpToolsModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public OpToolsModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateRawBuilder(ItemTags.SWORDS)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDILLIUM_SWORD.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDIUM_SWORD.toString()));

        getOrCreateRawBuilder(ItemTags.PICKAXES)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDILLIUM_PICKAXE.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDIUM_PICKAXE.toString()));

        getOrCreateRawBuilder(ItemTags.AXES)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDILLIUM_AXE.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDIUM_AXE.toString()));

        getOrCreateRawBuilder(ItemTags.HOES)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDILLIUM_HOE.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDIUM_HOE.toString()));

        getOrCreateRawBuilder(ItemTags.SHOVELS)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDILLIUM_SHOVEL.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModTools.DIAMONDIUM_SHOVEL.toString()));

        getOrCreateRawBuilder(ItemTags.HEAD_ARMOR)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModArmor.DIAMONDILLIUM_HELMET.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModArmor.DIAMONDIUM_HELMET.toString()));

        getOrCreateRawBuilder(ItemTags.CHEST_ARMOR)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModArmor.DIAMONDILLIUM_CHESTPLATE.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModArmor.DIAMONDIUM_CHESTPLATE.toString()));

        getOrCreateRawBuilder(ItemTags.LEG_ARMOR)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModArmor.DIAMONDILLIUM_LEGGINGS.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModArmor.DIAMONDIUM_LEGGINGS.toString()));

        getOrCreateRawBuilder(ItemTags.FOOT_ARMOR)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModArmor.DIAMONDILLIUM_BOOTS.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModArmor.DIAMONDIUM_BOOTS.toString()));

        getOrCreateRawBuilder(ItemTags.BEACON_PAYMENT_ITEMS)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModItems.DIAMONDILLIUM_INGOT.toString()))
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModItems.DIAMONDIUM_INGOT.toString()));

        getOrCreateRawBuilder(ItemTags.MEAT)
                .addOptionalTag(Identifier.fromNamespaceAndPath(MOD_ID, ModFoodItems.SUPER_MEAT.toString()));
    }
}
