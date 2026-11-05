package thaumcraft.api.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aura.AuraHelper;

/**
 * 
 * @author Azanor
 * 
 * Helper methods to manipulate charge in items that use IRechargable.
 * The charge is stored as an int under {@link #NBT_TAG} in the item's custom data component.
 * 
 */
public class RechargeHelper {
	
	public static final String NBT_TAG = "tc.charge";

	/**
	 * This method is called to recharge an item from the aura. 
	 * Call it wherever you need to call it from, but only if you add some way to recharge such items.
	 * In general this should only be called every 5 ticks.
	 * @param world
	 * @param is
	 * @param pos
	 * @param player May be null
	 * @param amt
	 * @return how much charge was actually added
	 */
	public static float rechargeItem(Level world, ItemStack is, BlockPos pos, Player player, int amt) {	
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return 0;		
		IRechargable chargeItem = (IRechargable)is.getItem();		
		if (player!=null && AuraHelper.shouldPreserveAura(world,player,pos)) return 0;				
		amt = (int) Math.min(amt, chargeItem.getMaxCharge(is,player) - getCharge(is));		
		int drained = (int) AuraHelper.drainVis(world, pos, amt, false);		
		if (drained>0) {
			addCharge(is, player, drained);
			return drained;
		}		
		return 0;
	}
	
	/**
	 * This method is called to recharge an item. 
	 * Nothing is drained from the aura in this version - it is just recharged blindly.
	 * @param is
	 * @param player May be null
	 * @param amt
	 * @return how much charge was actually added
	 */
	public static float rechargeItemBlindly(ItemStack is, Player player, int amt) {	
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return 0;		
		IRechargable chargeItem = (IRechargable)is.getItem();		
		amt = (int) Math.min(amt, chargeItem.getMaxCharge(is,player) - getCharge(is));		
		if (amt>0) addCharge(is, player, amt);		
		return amt;
	}
	
	private static void addCharge(ItemStack is, LivingEntity player, int amt) {
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return;
		IRechargable chargeItem = (IRechargable)is.getItem();
		int amount = Math.min(chargeItem.getMaxCharge(is,player), amt + getCharge(is));
		ThaumcraftApiHelper.updateCustomData(is, tag -> tag.putInt(NBT_TAG, amount));
	}
	
	/**
	 * @param is
	 * @return returns charge amount or -1 if item is not rechargable
	 */
	public static int getCharge(ItemStack is) {
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return -1;
		return ThaumcraftApiHelper.getCustomData(is).getIntOr(NBT_TAG, 0);
	}
	
	/**
	 * 
	 * @param is
	 * @return return charge level as a float (with 1 being full)
	 */
	public static float getChargePercentage(ItemStack is, Player player) {
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return -1;
		float c = getCharge(is);
		float m =  ((IRechargable)is.getItem()).getMaxCharge(is, player);
		return c / m;
	}
	
	/**
	 * Consumes vis charge from the item
	 * @param is
	 * @param player
	 * @param amt
	 * @return if the item had the charge removed
	 */
	public static boolean consumeCharge(ItemStack is, LivingEntity player, int amt) {
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return false;
		if (ThaumcraftApiHelper.hasCustomData(is)) {
			int charge = ThaumcraftApiHelper.getCustomData(is).getIntOr(NBT_TAG, 0);
			if (charge>=amt) {
				charge -= amt;
				final int c = charge;
				ThaumcraftApiHelper.updateCustomData(is, tag -> tag.putInt(NBT_TAG, c));
				return true;
			}
		}
		return false;
	}
}
