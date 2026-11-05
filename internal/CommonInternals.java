package thaumcraft.api.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApi.SmeltBonus;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;

/**
 * @author Azanor
 *
 * Internal variables and methods used by Thaumcraft and the api that should normally not be accessed directly by addon mods.
 *
 */
public class CommonInternals {
	
	public static HashMap<String,Identifier> jsonLocs = new  HashMap<>();
	public static ArrayList<ThaumcraftApi.EntityTags> scanEntities = new ArrayList<>();	
	public static HashMap<Identifier,IThaumcraftRecipe> craftingRecipeCatalog = new HashMap<>();
	public static HashMap<Identifier,Object> craftingRecipeCatalogFake = new HashMap<>();
	public static ArrayList<SmeltBonus> smeltingBonus = new ArrayList<SmeltBonus>();
	public static ConcurrentHashMap<Integer,AspectList> objectTags = new ConcurrentHashMap<>();
	public static HashMap<Object,Integer> warpMap = new HashMap<Object,Integer>();
	public static HashMap<String,ItemStack> seedList = new HashMap<String,ItemStack>();
	
	public static IThaumcraftRecipe getCatalogRecipe(Identifier key) {	
		return craftingRecipeCatalog.get(key);
	}
	
	public static Object getCatalogRecipeFake(Identifier key) {	
		return craftingRecipeCatalogFake.get(key);
	}
	
	/**
	 * Obviously the int generated is not truly unique, but it is unique enough for this purpose.
	 * @param stack
	 * @return
	 */
	public static int generateUniqueItemstackId(ItemStack stack) {
    	return ItemStack.hashItemAndComponents(stack);
    }
	
	/**
	 * Obviously the int generated is not truly unique, but it is unique enough for this purpose.
	 * Ignores all data components on the itemstack (only the item counts)
	 * @param stack
	 * @return
	 */
	public static int generateUniqueItemstackIdStripped(ItemStack stack) {
    	return ItemStack.hashItemAndComponents(new ItemStack(stack.getItem()));
    }
}
