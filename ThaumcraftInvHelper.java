package thaumcraft.api;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class ThaumcraftInvHelper {

	public static class InvFilter {
		/** ignore the damage component */
		public boolean igDmg;
		/** ignore all other data components, including custom data */
		public boolean igNBT;
		/** treat stacks as equal if they share a common (<code>c:</code> namespace) item tag. Replaces ore dictionary matching. */
		public boolean useOre;
		/** treat stacks as equal if their items come from the same mod */
		public boolean useMod;
		/** only require that the custom data of the first stack is present and equal in the second */
		public boolean relaxedNBT = false;

		public InvFilter(boolean ignoreDamage, boolean ignoreNBT, boolean useOre, boolean useMod) {
			this.igDmg = ignoreDamage;
			this.igNBT = ignoreNBT;
			this.useOre = useOre;
			this.useMod = useMod;
		}

		public InvFilter setRelaxedNBT() {
			relaxedNBT = true;
			return this;
		}

		public final static InvFilter STRICT = new InvFilter(false,false,false,false);
		public final static InvFilter BASEORE = new InvFilter(false,false,true,false);
	}

	public static IItemHandler getItemHandlerAt(Level world, BlockPos pos, Direction side) {
		Optional<IItemHandler> dest = VanillaInventoryCodeHooks.getItemHandler(world, pos, side);
		if (dest.isPresent()) {
			return dest.get();
		} else {
			BlockEntity tileentity = world.getBlockEntity(pos);
	        if (tileentity instanceof Container) {
	        	return wrapInventory ((Container) tileentity, side);
	        }
		}
		return null;
	}

	public static IItemHandler wrapInventory(Container inventory, Direction side) {
		return inventory instanceof WorldlyContainer? new SidedInvWrapper((WorldlyContainer) inventory, side) : new InvWrapper(inventory);
	}

	/**
	 * Unlike the normal component comparison used by itemstacks, this method only checks if all the custom data tags in stackA are present and equal in stackB. Any extra tags in stackB are ignored.
	 * Some mods love adding their own data to itemstacks which ends up breaking a lot of crafting recipes or similar checks.
	 * Only the custom data component is compared; other components are ignored.
	 * @param prime
	 * @param other
	 * @return
	 */
	public static boolean areItemStackTagsEqualRelaxed(ItemStack prime, ItemStack other) {
		if (prime.isEmpty() && other.isEmpty()) return true;
		if (prime.isEmpty() || other.isEmpty()) return false;
		return !ThaumcraftApiHelper.hasCustomData(prime) ||
				compareTagsRelaxed(ThaumcraftApiHelper.getCustomData(prime), ThaumcraftApiHelper.getCustomData(other));
	}

	public static boolean compareTagsRelaxed(CompoundTag prime, CompoundTag other) {
		for (String key : prime.keySet()) {
			if (!other.contains(key) || !prime.get(key).equals(other.get(key))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param stack0 the stack to test
	 * @param in an ItemStack, Ingredient, item TagKey, or a String item tag id (with or without a leading #)
	 */
	public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in)
	{
		if (stack0==null && in!=null) return false;
		if (stack0!=null && in==null) return false;
		if (stack0==null && in==null) return true;

		if (in instanceof Object[]) return true;

		if (in instanceof Ingredient) return ((Ingredient) in).test(stack0);

		if (in instanceof TagKey || in instanceof String) {
			Ingredient ing = ThaumcraftApiHelper.getIngredient(in instanceof String && !((String) in).startsWith("#") ? "#"+in : in);
			return ing != null && ing.test(stack0);
		}

		if (in instanceof ItemStack) {
			boolean t1= !ThaumcraftApiHelper.hasCustomData(stack0) || areItemStackTagsEqualForCrafting(stack0, (ItemStack) in);
			if (!t1) return false;
	        return ItemStack.isSameItem((ItemStack) in, stack0);
		}

		return false;
	}

	public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets)
	{
	    for (ItemStack input : inputs)
	    {
	        for (ItemStack target : targets)
	        {
	        	boolean match = strict ? ItemStack.isSameItemSameComponents(target, input) :
	        		ItemStack.isSameItem(target, input) && ThaumcraftApiHelper.getCustomData(target).equals(ThaumcraftApiHelper.getCustomData(input));
	            if (match) return true;
	        }
	    }
	    return false;
	}

	/**
	 * Item metadata no longer exists; damage is a component. Two stacks are equal here if they hold the same item,
	 * and, for non-damageable items, the same damage value.
	 */
	public static boolean areItemsEqual(ItemStack s1,ItemStack s2)
	{
		if (s1.isDamageableItem() && s2.isDamageableItem())
		{
			return s1.getItem() == s2.getItem();
		} else
			return s1.getItem() == s2.getItem() && s1.getDamageValue() == s2.getDamageValue();
	}

	public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem,ItemStack recipeItem)
	{
		if (recipeItem == null || slotItem == null) return false;
		if (ThaumcraftApiHelper.hasCustomData(recipeItem) && !ThaumcraftApiHelper.hasCustomData(slotItem)) return false;
		if (!ThaumcraftApiHelper.hasCustomData(recipeItem)) return true;

		CompoundTag slotTag = ThaumcraftApiHelper.getCustomData(slotItem);
		CompoundTag recipeTag = ThaumcraftApiHelper.getCustomData(recipeItem);
	    for (String s : recipeTag.keySet())
	    {
	        if (slotTag.contains(s)) {
	        	if (!slotTag.get(s).toString().equals(recipeTag.get(s).toString())) {
	        		return false;
	        	}
	        } else {
	    		return false;
	        }
	    }
	    return true;
	}

	/**
	 * Compares two stacks using the given filter.
	 * @param stack0
	 * @param stack1
	 * @param filter
	 * @return
	 */
	public static boolean areItemStacksEqual(ItemStack stack0, ItemStack stack1, InvFilter filter) {
		if (stack0 == null || stack1 == null) return stack0 == stack1;
		if (stack0.isEmpty() && stack1.isEmpty()) return true;
		if (stack0.isEmpty() || stack1.isEmpty()) return false;

		if (filter.useMod) {
			return BuiltInRegistries.ITEM.getKey(stack0.getItem()).getNamespace().equals(
					BuiltInRegistries.ITEM.getKey(stack1.getItem()).getNamespace());
		}

		if (filter.useOre && shareCommonTag(stack0, stack1)) return true;

		if (stack0.getItem() != stack1.getItem()) return false;

		if (!filter.igDmg && stack0.getDamageValue() != stack1.getDamageValue()) return false;

		if (filter.igNBT) return true;

		if (filter.relaxedNBT) return areItemStackTagsEqualRelaxed(stack0, stack1);

		return ItemStack.matchesIgnoringComponents(stack0, stack1, type -> type == DataComponents.DAMAGE);
	}

	private static boolean shareCommonTag(ItemStack stack0, ItemStack stack1) {
		return stack0.typeHolder().tags()
				.filter(t -> "c".equals(t.location().getNamespace()))
				.anyMatch(t -> stack1.typeHolder().is(t));
	}

	public static ItemStack insertStackAt(Level world, BlockPos pos, Direction side, ItemStack stack, boolean simulate)
	{
		IItemHandler inventory = getItemHandlerAt(world,pos,side);
		if (inventory!=null) {
			return ItemHandlerHelper.insertItemStacked(inventory, stack, simulate);
		}
		return stack;
	}

	public static ItemStack hasRoomFor(Level world, BlockPos pos, Direction side, ItemStack stack) {
		ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
		if (testStack.isEmpty()) {
			return stack.copy();
		}
		testStack.setCount(stack.getCount() - testStack.getCount());
		return testStack;
	}

	public static boolean hasRoomForSome(Level world, BlockPos pos, Direction side, ItemStack stack) {
		ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
		return stack.getCount()==0 || testStack.getCount()!=stack.getCount();
	}

	public static boolean hasRoomForAll(Level world, BlockPos pos, Direction side, ItemStack stack) {
		return insertStackAt(world, pos, side, stack.copy(), true).isEmpty();
	}

	public static int countTotalItemsIn(IItemHandler inventory, ItemStack stack, ThaumcraftInvHelper.InvFilter filter) {
		int count = 0;
		if (inventory!=null) {
			for (int a=0;a<inventory.getSlots();a++) {
	    		if (areItemStacksEqual(stack,inventory.getStackInSlot(a),filter)) {
	    			count+=inventory.getStackInSlot(a).getCount();
	    		}
	    	}
		}
		return count;
	}

	public static int countTotalItemsIn(Level world, BlockPos pos, Direction side, ItemStack stack, ThaumcraftInvHelper.InvFilter filter) {
		return countTotalItemsIn(getItemHandlerAt(world,pos,side),stack,filter);
	}

}
