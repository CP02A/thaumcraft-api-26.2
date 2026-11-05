package thaumcraft.api.items;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

/**
 * An item that holds aspects in its custom data component, like vis crystals.
 */
public class ItemGenericEssentiaContainer extends Item implements IEssentiaContainerItem
{

	public ItemGenericEssentiaContainer(Item.Properties properties, int base)
    {
        super(properties.stacksTo(64));
        this.base = base;
    }

	protected int base = 1;

	/**
	 * Adds one stack per registered aspect to the list.
	 * Creative tabs are filled through events now, so call this from your creative tab contents handler.
	 */
	public void getSubItems(NonNullList<ItemStack> items) {
    	for (Aspect tag:Aspect.aspects.values()) {
    		ItemStack i = new ItemStack(this);
    		this.setAspects(i, new AspectList().add(tag, base));
    		items.add(i);
		}
	}

	@Override
	public AspectList getAspects(ItemStack itemstack) {
		if (ThaumcraftApiHelper.hasCustomData(itemstack)) {
			AspectList aspects = new AspectList();
			aspects.readFromNBT(ThaumcraftApiHelper.getCustomData(itemstack));
			return aspects.size()>0?aspects:null;
		}
		return null;
	}

	@Override
	public void setAspects(ItemStack itemstack, AspectList aspects) {
		ThaumcraftApiHelper.updateCustomData(itemstack, tag -> aspects.writeToNBT(tag));
	}

	@Override
	public boolean ignoreContainedAspects() {return false;}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, EquipmentSlot slot) {
		if (!ThaumcraftApiHelper.hasCustomData(stack)) {
			setRandomAspect(stack, world.getRandom());
		}
		super.inventoryTick(stack, world, entity, slot);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player player) {
		if (!player.level().isClientSide() && !ThaumcraftApiHelper.hasCustomData(stack)) {
			setRandomAspect(stack, player.level().getRandom());
		}
	}

	private void setRandomAspect(ItemStack stack, RandomSource rand) {
		Aspect[] displayAspects = Aspect.aspects.values().toArray(new Aspect[]{});
		this.setAspects(stack, new AspectList().add(displayAspects[rand.nextInt(displayAspects.length)], base));
	}
}
