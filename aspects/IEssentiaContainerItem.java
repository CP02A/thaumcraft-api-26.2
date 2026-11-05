package thaumcraft.api.aspects;

import net.minecraft.world.item.ItemStack;

/**
 * 
 * @author azanor
 * 
 * Used by wispy essences and essentia phials to hold their aspects. 
 * Useful for similar item containers that store their aspect information in nbt form so TC
 * automatically picks up the aspects they contain.
 *
 */
public interface IEssentiaContainerItem {
	public AspectList getAspects(ItemStack itemstack);
	public void setAspects(ItemStack itemstack, AspectList aspects);
	
	/**
	 * Return true if the contained aspect should not be used to calculate the actual item aspects. For example: jar labels. 
	 */
	public boolean ignoreContainedAspects();
}

//Example implementation, storing the aspects in the stack's custom data component
/*  
	@Override
	public AspectList getAspects(ItemStack itemstack) {
		CustomData data = itemstack.get(DataComponents.CUSTOM_DATA);
		if (data != null) {
			AspectList aspects = new AspectList();
			aspects.readFromNBT(data.copyTag());
			return aspects.size()>0?aspects:null;
		}
		return null;
	}
	
	@Override
	public void setAspects(ItemStack itemstack, AspectList aspects) {
		CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> aspects.writeToNBT(tag));
	}
	
	@Override
	public boolean ignoreContainedAspects() {return false;}
*/
