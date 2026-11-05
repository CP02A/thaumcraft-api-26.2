package thaumcraft.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Item and block tags used by Thaumcraft. These replace the ore dictionary entries of earlier versions.
 * Tag contents are defined by Thaumcraft's data pack; addons can add their own entries to these tags with JSON
 * in their own data packs. Shared material tags use the common <code>c:</code> namespace.
 * <p>
 * Thaumcraft also adds its own logs, planks, slabs, saplings and leaves to the matching vanilla
 * <code>minecraft:</code> tags, and its trapdoors to <code>minecraft:wooden_trapdoors</code>.
 */
public class ThaumcraftTags {

	public static class Items {
		public static final TagKey<Item> ORES_AMBER = common("ores/amber");
		public static final TagKey<Item> ORES_CINNABAR = common("ores/cinnabar");
		public static final TagKey<Item> ORES_QUARTZ = common("ores/quartz");
		
		public static final TagKey<Item> CRYSTALS_AIR = thaumcraft("crystals/air");
		public static final TagKey<Item> CRYSTALS_EARTH = thaumcraft("crystals/earth");
		public static final TagKey<Item> CRYSTALS_WATER = thaumcraft("crystals/water");
		public static final TagKey<Item> CRYSTALS_FIRE = thaumcraft("crystals/fire");
		public static final TagKey<Item> CRYSTALS_ORDER = thaumcraft("crystals/order");
		public static final TagKey<Item> CRYSTALS_ENTROPY = thaumcraft("crystals/entropy");
		public static final TagKey<Item> CRYSTALS_TAINT = thaumcraft("crystals/taint");
		
		public static final TagKey<Item> NITOR = thaumcraft("nitor");
		
		public static final TagKey<Item> GEMS_AMBER = common("gems/amber");
		public static final TagKey<Item> QUICKSILVER = common("quicksilver");
		
		public static final TagKey<Item> NUGGETS_QUICKSILVER = common("nuggets/quicksilver");
		public static final TagKey<Item> NUGGETS_THAUMIUM = common("nuggets/thaumium");
		public static final TagKey<Item> NUGGETS_VOID = common("nuggets/void");
		public static final TagKey<Item> NUGGETS_BRASS = common("nuggets/brass");
		public static final TagKey<Item> NUGGETS_QUARTZ = common("nuggets/quartz");
		public static final TagKey<Item> NUGGETS_MEAT = thaumcraft("nuggets/meat");
		
		public static final TagKey<Item> INGOTS_THAUMIUM = common("ingots/thaumium");
		public static final TagKey<Item> INGOTS_VOID = common("ingots/void");
		public static final TagKey<Item> INGOTS_BRASS = common("ingots/brass");
		
		public static final TagKey<Item> STORAGE_BLOCKS_THAUMIUM = common("storage_blocks/thaumium");
		public static final TagKey<Item> STORAGE_BLOCKS_VOID = common("storage_blocks/void");
		public static final TagKey<Item> STORAGE_BLOCKS_BRASS = common("storage_blocks/brass");
		
		public static final TagKey<Item> PLATES_IRON = common("plates/iron");
		public static final TagKey<Item> PLATES_BRASS = common("plates/brass");
		public static final TagKey<Item> PLATES_THAUMIUM = common("plates/thaumium");
		public static final TagKey<Item> PLATES_VOID = common("plates/void");
		
		/** Native ore clusters, with one sub-tag per ore, e.g. thaumcraft:clusters/iron */
		public static final TagKey<Item> CLUSTERS = thaumcraft("clusters");
		
		private static TagKey<Item> common(String path) {
			return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
		}
		
		private static TagKey<Item> thaumcraft(String path) {
			return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("thaumcraft", path));
		}
	}
	
	public static class Blocks {
		public static final TagKey<Block> ORES_AMBER = common("ores/amber");
		public static final TagKey<Block> ORES_CINNABAR = common("ores/cinnabar");
		public static final TagKey<Block> ORES_QUARTZ = common("ores/quartz");
		
		public static final TagKey<Block> CRYSTALS = thaumcraft("crystals");
		public static final TagKey<Block> NITOR = thaumcraft("nitor");
		
		public static final TagKey<Block> STORAGE_BLOCKS_THAUMIUM = common("storage_blocks/thaumium");
		public static final TagKey<Block> STORAGE_BLOCKS_VOID = common("storage_blocks/void");
		public static final TagKey<Block> STORAGE_BLOCKS_BRASS = common("storage_blocks/brass");
		
		private static TagKey<Block> common(String path) {
			return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", path));
		}
		
		private static TagKey<Block> thaumcraft(String path) {
			return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("thaumcraft", path));
		}
	}
	
}
