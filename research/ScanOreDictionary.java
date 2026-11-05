package thaumcraft.api.research;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.internal.CommonInternals;

/**
 * The ore dictionary has been replaced by item tags. This matches any item (or the item form of any block) 
 * that is in one of the given item tags.
 */
public class ScanOreDictionary implements IScanThing {
	
	String research;	
	String[] entries;	
	
	public ConcurrentHashMap<Integer,Boolean> cache = new ConcurrentHashMap<>();

	/**
	 * @param research
	 * @param entries item tag ids, optionally prefixed with #, for example "c:ingots/iron"
	 */
	public ScanOreDictionary(String research, String ... entries) {
		this.research = research;
		this.entries = entries;
	}
	
	@Override
	public boolean checkThing(Player player, Object obj) {	
		ItemStack stack = null;
		if (obj!=null) {
			if (obj instanceof BlockPos) {
				BlockState state = player.level().getBlockState((BlockPos) obj);
				stack = state.getCloneItemStack(player.level(), (BlockPos) obj, false);			
			}
			else
			if (obj instanceof ItemStack) 
				stack = (ItemStack) obj;
			else
			if (obj instanceof ItemEntity && ((ItemEntity)obj).getItem()!=null) 
				stack = ((ItemEntity)obj).getItem();
		}
		
		if (stack!=null && !stack.isEmpty()) {			
			int hid = CommonInternals.generateUniqueItemstackId(stack);
			if (cache.containsKey(hid)) {
				return cache.get(hid);
			}
			
			for (String entry:entries) {
				if (stack.is(ThaumcraftApiHelper.itemTag(entry))) {
					synchronized(cache) { cache.put(hid,true); }
					return true;
				}
			}
			synchronized(cache) { cache.put(hid,false); }
		}
		
		return false;
	}
	
	@Override
	public String getResearchKey(Player player, Object object) {		
		return research;
	}
}
