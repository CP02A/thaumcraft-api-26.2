package thaumcraft.api;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.crafting.ingredients.AbstractIngredient;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.crafting.IngredientNBTTC;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;

public class ThaumcraftApiHelper {
	
	/**
	 * Champion mob modifier attribute. Thaumcraft registers this in the attribute registry;
	 * look up its {@link Holder} through the registry when applying it to entities.
	 */
	public static final Attribute CHAMPION_MOD = new RangedAttribute("attribute.thaumcraft.mobmod", -2D, -2D, 100D).setSyncable(true);
	
	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#areItemsEqual(ItemStack,ItemStack)} instead
	 */
	@Deprecated
	public static boolean areItemsEqual(ItemStack s1,ItemStack s2)
	{
		return ThaumcraftInvHelper.areItemsEqual(s1, s2);
	}
		
	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#containsMatch(boolean,ItemStack[],List)} instead
	 */
	@Deprecated
	public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets)
	{
		return ThaumcraftInvHelper.containsMatch(strict, inputs, targets);
	}
	
	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#areItemStacksEqualForCrafting(ItemStack,Object)} instead
	 */
	@Deprecated
	public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in)
	{
		return ThaumcraftInvHelper.areItemStacksEqualForCrafting(stack0, in);
	}
	
	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#areItemStackTagsEqualForCrafting(ItemStack,ItemStack)} instead
	 */
	@Deprecated
	public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem,ItemStack recipeItem)
	{
		return ThaumcraftInvHelper.areItemStackTagsEqualForCrafting(slotItem, recipeItem);
	}
   
    public static BlockEntity getConnectableTile(BlockGetter world, BlockPos pos, Direction face) {
		BlockEntity te = world.getBlockEntity(pos.relative(face));
		if (te instanceof IEssentiaTransport && ((IEssentiaTransport)te).isConnectable(face.getOpposite())) 
			return te;
		else
			return null;
	}  
    
    /**
     * Ray traces from v1 to v2, ignoring the block that v1 starts in.
     * @param world
     * @param v1 start of the trace
     * @param v2 end of the trace
     * @param stopOnLiquid if fluids should stop the trace
     * @param ignoreBlockWithoutBoundingBox if true, uses collision shapes, so blocks without collision are passed through
     * @param returnLastUncollidableBlock if true and nothing was hit, returns a miss result for the last block passed through
     * @return the hit result, or null
     */
	public static BlockHitResult rayTraceIgnoringSource(Level world, Vec3 v1, Vec3 v2, 
			boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
	{
		if (Double.isNaN(v1.x) || Double.isNaN(v1.y) || Double.isNaN(v1.z)) return null;
		if (Double.isNaN(v2.x) || Double.isNaN(v2.y) || Double.isNaN(v2.z)) return null;
		
		final BlockPos source = BlockPos.containing(v1);
		final Direction travel = Direction.getApproximateNearest(v1.subtract(v2));
		final BlockHitResult[] lastMiss = new BlockHitResult[1];
		ClipContext context = new ClipContext(v1, v2, 
				ignoreBlockWithoutBoundingBox ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE, 
				stopOnLiquid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, 
				CollisionContext.empty());
		
		BlockHitResult hit = BlockGetter.traverseBlocks(v1, v2, context, (ctx, pos) -> {
			if (pos.equals(source)) return null;
			BlockState state = world.getBlockState(pos);
			VoxelShape shape = ctx.getBlockShape(state, world, pos);
			BlockHitResult blockHit = world.clipWithInteractionOverride(v1, v2, pos, shape, state);
			if (blockHit != null) return blockHit;
			if (stopOnLiquid) {
				FluidState fluid = world.getFluidState(pos);
				BlockHitResult fluidHit = ctx.getFluidShape(fluid, world, pos).clip(v1, v2, pos);
				if (fluidHit != null) return fluidHit;
			}
			lastMiss[0] = BlockHitResult.miss(Vec3.atCenterOf(pos), travel, pos.immutable());
			return null;
		}, ctx -> null);
		
		if (hit != null) return hit;
		return returnLastUncollidableBlock ? lastMiss[0] : null;
	}
	
	public static Object getNBTDataFromId(CompoundTag nbt, byte id, String key) {
		switch (id) {
		case 1: return nbt.getByte(key).orElse(null);
		case 2: return nbt.getShort(key).orElse(null);
		case 3: return nbt.getInt(key).orElse(null);
		case 4: return nbt.getLong(key).orElse(null);
		case 5: return nbt.getFloat(key).orElse(null);
		case 6: return nbt.getDouble(key).orElse(null);
		case 7: return nbt.getByteArray(key).orElse(null);
		case 8: return nbt.getString(key).orElse(null);
		case 9: return nbt.getList(key).orElse(null);
		case 10: return nbt.get(key);
		case 11: return nbt.getIntArray(key).orElse(null);
		default: return null;
		}
	}
	
	public static int setByteInInt(int data, byte b, int index)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(0,data);
		bb.put(index, b);
	    return bb.getInt(0);
	}
	
	public static byte getByteInInt(int data, int index) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(0,data);
		return bb.get(index);
	}	
	
	public static long setByteInLong(long data, byte b, int index)
	{
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(0,data);
		bb.put(index, b);
	    return bb.getLong(0);
	}
	
	public static byte getByteInLong(long data, int index) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(0,data);
		return bb.get(index);
	}	
	
	public static int setNibbleInInt(int data, int nibble, int nibbleIndex)
	{
	    int shift = nibbleIndex * 4;
	    return (data & ~(0xf << shift)) | (nibble << shift);
	}
	
	public static int getNibbleInInt(int data, int nibbleIndex) {
		return (data >> (nibbleIndex << 2)) & 0xF;
	}

	/**
	 * Create a crystal itemstack from a sent aspect. 
	 * @param aspect
	 * @param stackSize stack size
	 * @return
	 */
	public static ItemStack makeCrystal(Aspect aspect, int stackSize) {
		if (aspect==null) return null;
		ItemStack is = new ItemStack(ItemsTC.crystalEssence,stackSize);
		((ItemGenericEssentiaContainer)ItemsTC.crystalEssence).setAspects(is, new AspectList().add(aspect, 1));
		return is;
	}

	/**
	 * Create a crystal itemstack from a sent aspect. Sending a null will result in a balanced shard (one of each primal).
	 * @param aspect
	 * @return
	 */
	public static ItemStack makeCrystal(Aspect aspect) {
		return makeCrystal(aspect,1);
	}
	
	//CUSTOM DATA///////////////////////////////////////
	// Item stacks no longer carry an NBT compound. Thaumcraft keeps its per-stack data in the 
	// minecraft:custom_data component; these helpers read and write it.
	
	/**
	 * @return true if the stack has a non-empty custom data component
	 */
	public static boolean hasCustomData(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && !data.isEmpty();
	}
	
	/**
	 * @return a copy of the stack's custom data compound. Never null; empty if the stack has no custom data.
	 * Changes to the returned tag are not written back - use {@link #updateCustomData} or {@link #setCustomData}.
	 */
	public static CompoundTag getCustomData(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? new CompoundTag() : data.copyTag();
	}
	
	/**
	 * Replaces the stack's custom data with the given compound. Passing an empty compound removes the component.
	 */
	public static void setCustomData(ItemStack stack, CompoundTag tag) {
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
	}
	
	/**
	 * Edits the stack's custom data in place.
	 */
	public static void updateCustomData(ItemStack stack, Consumer<CompoundTag> updater) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
	}
	
	//TAGS///////////////////////////////////////
	
	/**
	 * @param tag an item tag id, for example <code>"c:ingots/iron"</code>
	 * @return the item tag key
	 */
	public static TagKey<Item> itemTag(String tag) {
		return TagKey.create(Registries.ITEM, Identifier.parse(tag.startsWith("#") ? tag.substring(1) : tag));
	}
	
	/**
	 * Replacement for the ore dictionary lookup. Returns one stack of every item in the given item tag. 
	 * If the tag id ends with <code>*</code> every item tag whose id starts with the given prefix is included,
	 * for example <code>"c:ores/*"</code>.
	 * Tags are only populated once data packs have loaded.
	 * @param tag item tag id, optionally prefixed with #
	 * @return the matching items
	 */
	public static List<ItemStack> getOresWithWildCards(String tag) {
		String t = tag.trim();
		if (t.startsWith("#")) t = t.substring(1);
		ArrayList<ItemStack> ores = new ArrayList<>(); 
		if (t.endsWith("*")) {
			String m = t.substring(0, t.length()-1);
			BuiltInRegistries.ITEM.getTags().forEach(named -> {
				if (named.key().location().toString().startsWith(m)) {
					for (Holder<Item> h : named) ores.add(new ItemStack(h));
				}
			});
		} else {
			for (Holder<Item> h : BuiltInRegistries.ITEM.getTagOrEmpty(itemTag(t))) ores.add(new ItemStack(h));
		}
		return ores;
	}

	/**
	 * Converts a recipe input into an {@link Ingredient}. Accepts:
	 * <ul>
	 * <li>an {@link Ingredient}</li>
	 * <li>an {@link ItemStack} - if the stack has custom data the ingredient also requires that data (relaxed match)</li>
	 * <li>an {@link ItemLike} (item or block)</li>
	 * <li>a {@link TagKey} of items</li>
	 * <li>a String - <code>"#namespace:path"</code> for an item tag, otherwise an item id</li>
	 * </ul>
	 * @return the ingredient, or null if the object could not be converted
	 */
	@SuppressWarnings("unchecked")
	public static Ingredient getIngredient(Object obj)
    {
		if (obj == null) return null;
		if (obj instanceof Ingredient) return (Ingredient) obj;
        if (obj instanceof ItemStack) {
        	ItemStack stack = (ItemStack) obj;
        	if (stack.isEmpty()) return null;
        	if (hasCustomData(stack)) 
        		return new IngredientNBTTC(stack);
        	return Ingredient.of(stack.getItem());
        }
        if (obj instanceof ItemLike) return Ingredient.of((ItemLike) obj);
        if (obj instanceof TagKey) return AbstractIngredient.of((TagKey<Item>) obj);
        if (obj instanceof String) {
        	String s = ((String) obj).trim();
        	if (s.startsWith("#")) return AbstractIngredient.of(itemTag(s));
        	Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(s));
        	return item == null || item == Items.AIR ? null : Ingredient.of(item);
        }
        return null;
    }

	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#getItemHandlerAt(Level,BlockPos,Direction)} instead
	 */
	@Deprecated
	public static IItemHandler getItemHandlerAt(Level world, BlockPos pos, Direction side) {
		return ThaumcraftInvHelper.getItemHandlerAt(world, pos, side);
	}

	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#wrapInventory(Container,Direction)} instead
	 */
	@Deprecated
	public static IItemHandler wrapInventory(Container inventory, Direction side) {
		return ThaumcraftInvHelper.wrapInventory(inventory, side);
	}

	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#areItemStackTagsEqualRelaxed(ItemStack,ItemStack)} instead
	 */	
	@Deprecated
	public static boolean areItemStackTagsEqualRelaxed(ItemStack prime, ItemStack other) {
		return ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(prime, other);
	}
	
	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#compareTagsRelaxed(CompoundTag,CompoundTag)} instead
	 */
	@Deprecated
	public static boolean compareTagsRelaxed(CompoundTag prime, CompoundTag other) {
		return ThaumcraftInvHelper.compareTagsRelaxed(prime, other);
	}
		
}
