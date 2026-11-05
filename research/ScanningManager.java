package thaumcraft.api.research;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class ScanningManager {
	
	static ArrayList<IScanThing> things = new ArrayList<IScanThing>();
	
	/**
	 * Add things to scan
	 * @example 
	 * <i>ScanningManager.addScannableThing(new ScanItem("HIPSTER",new ItemStack(Items.APPLE)));</i><br>
	 * This will unlock the <b>HIPSTER</b> research if you scan an apple.
	 */	
	public static void addScannableThing(IScanThing obj) {		
		things.add(obj);		
	}
	
	/**
	 * @param player
	 * @param object this could in theory be anything, but vanilla tc scanning tools only pass in Entity, BlockPos, Itemstack or null
	 */
	public static void scanTheThing(Player player, Object object) {
		boolean found = false;
		boolean suppress = false;
		for (IScanThing thing:things) {
			if (thing.checkThing(player, object)) {						
				if (thing.getResearchKey(player, object)==null || thing.getResearchKey(player, object).isEmpty() ||
						ThaumcraftApi.internalMethods.progressResearch(player, thing.getResearchKey(player, object))) {		
					if (thing.getResearchKey(player, object)==null || thing.getResearchKey(player, object).isEmpty())
						suppress = true;
					found = true;
					thing.onSuccess(player, object);
				}
			}			
		}
		if (!suppress) {
			if (!found) {
				player.sendOverlayMessage(Component.translatable("tc.unknownobject").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
			} else {
				player.sendOverlayMessage(Component.translatable("tc.knownobject").withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC));
			}
		}
		
		// scan contents of inventories
		if (object instanceof BlockPos) {
			IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(player.level(), (BlockPos) object, Direction.UP);
			if (handler != null) {
				int scanned = 0;
				for (int slot=0;slot<handler.getSlots();slot++) {
					ItemStack stack = handler.getStackInSlot(slot);
					if (stack!=null && !stack.isEmpty()) {
						scanTheThing(player,stack);
						scanned++;
					}
					if (scanned>=100) {
						player.sendOverlayMessage(Component.translatable("tc.invtoolarge").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
						break; // to prevent lag with massive inventories
					}
				}
			}			
			return;
		}
		
	}
	
	/**
	 * @param player
	 * @param object
	 * @return true if the object can be scanned for research the player has not yet discovered
	 */
	public static boolean isThingStillScannable(Player player, Object object) {		
		for (IScanThing thing:things) {
			if (thing.checkThing(player, object)) {
				try {
					if (!ThaumcraftCapabilities.knowsResearch(player,thing.getResearchKey(player, object))) {					
						return true;
					}
				} catch (Exception e) {	}
			}			
		}
		return false;
	}
		
	
	public static ItemStack getItemFromParms(Player player, Object obj) {
		ItemStack is = ItemStack.EMPTY;
		if (obj instanceof ItemStack) 
			is = (ItemStack) obj;
		if (obj instanceof ItemEntity && ((ItemEntity)obj).getItem()!=null) 
			is = ((ItemEntity)obj).getItem();
		if (obj instanceof BlockPos) {
			BlockState state = player.level().getBlockState((BlockPos) obj);
			try {
				is = state.getCloneItemStack(player.level(), (BlockPos) obj, false);
			} catch (Exception e) {	}
			// Water and Lava blocks cant be registered as itemstacks anymore? Oh Minecraft!
			try {
				if ((is==null||is.isEmpty()) && state.getFluidState().is(FluidTags.WATER)) {
					is = new ItemStack(Items.WATER_BUCKET);
				}
				if ((is==null||is.isEmpty()) && state.getFluidState().is(FluidTags.LAVA)) {
					is = new ItemStack(Items.LAVA_BUCKET);
				}
			} catch (Exception e) {}			
		}
		return is;
	}
}
