package thaumcraft.api;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class ThaumcraftMaterials {

	public static final ToolMaterial TOOLMAT_THAUMIUM = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 500, 7F, 2.5f, 22, ThaumcraftTags.Items.INGOTS_THAUMIUM);
	public static final ToolMaterial TOOLMAT_VOID = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 150, 8F, 3, 10, ThaumcraftTags.Items.INGOTS_VOID);
	public static final ToolMaterial TOOLMAT_ELEMENTAL = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1500, 9F, 3, 18, ThaumcraftTags.Items.INGOTS_THAUMIUM);
	
	public static final ArmorMaterial ARMORMAT_THAUMIUM = armor("thaumium", 25, 2, 5, 6, 2, 25, SoundEvents.ARMOR_EQUIP_IRON, 1.0F, ThaumcraftTags.Items.INGOTS_THAUMIUM);
	public static final ArmorMaterial ARMORMAT_SPECIAL = armor("special", 25, 1, 2, 3, 1, 25, SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, repairTag("special"));
	public static final ArmorMaterial ARMORMAT_VOID = armor("void", 10, 3, 6, 8, 3, 10, SoundEvents.ARMOR_EQUIP_CHAIN, 1F, ThaumcraftTags.Items.INGOTS_VOID);
	public static final ArmorMaterial ARMORMAT_VOIDROBE = armor("voidrobe", 18, 4, 7, 9, 4, 10, SoundEvents.ARMOR_EQUIP_LEATHER, 2f, ThaumcraftTags.Items.INGOTS_VOID);
	public static final ArmorMaterial ARMORMAT_FORTRESS = armor("fortress", 40, 3, 6, 7, 3, 25, SoundEvents.ARMOR_EQUIP_IRON, 3f, ThaumcraftTags.Items.INGOTS_THAUMIUM);
	public static final ArmorMaterial ARMORMAT_CULTIST_PLATE = armor("cultist_plate", 18, 2, 5, 6, 2, 13, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, repairTag("cultist_plate"));
	public static final ArmorMaterial ARMORMAT_CULTIST_ROBE = armor("cultist_robe", 17, 2, 4, 5, 2, 13, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, repairTag("cultist_robe"));
	public static final ArmorMaterial ARMORMAT_CULTIST_LEADER = armor("cultist_leader", 30, 3, 6, 7, 3, 20, SoundEvents.ARMOR_EQUIP_IRON, 1F, repairTag("cultist_leader"));
	
	/**
	 * Keeps the 1.12 argument order (boots, leggings, chestplate, helmet) so values carry over unchanged.
	 * The equipment asset id is <code>thaumcraft:[name]</code>.
	 */
	private static ArmorMaterial armor(String name, int durability, int boots, int legs, int chest, int helmet, 
			int enchantability, Holder<SoundEvent> sound, float toughness, TagKey<Item> repair) {
		Map<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);
		defense.put(ArmorType.BOOTS, boots);
		defense.put(ArmorType.LEGGINGS, legs);
		defense.put(ArmorType.CHESTPLATE, chest);
		defense.put(ArmorType.HELMET, helmet);
		defense.put(ArmorType.BODY, chest);
		ResourceKey<EquipmentAsset> asset = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("thaumcraft", name));
		return new ArmorMaterial(durability, defense, enchantability, sound, toughness, 0.0F, repair, asset);
	}
	
	/** item tag <code>thaumcraft:repairs_[name]_armor</code>, for armor without a natural repair material */
	private static TagKey<Item> repairTag(String name) {
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("thaumcraft", "repairs_" + name + "_armor"));
	}
	
}
