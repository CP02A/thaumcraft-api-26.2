package thaumcraft.api.research;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Matches one exact block state (block plus all of its property values).
 * To match a block regardless of its properties use {@link ScanBlock}.
 */
public class ScanBlockState implements IScanThing {
	
	String research;	
	BlockState blockState;
	
	public ScanBlockState(BlockState blockState) {
		this.research = "!"+blockState.toString();
		this.blockState = blockState;
	}
	
	public ScanBlockState(String research, BlockState blockState) {
		this.research = research;
		this.blockState = blockState;
	}
	
	/**
	 * @param item if true the item form of the block is also registered as scannable. 
	 * Since item variants no longer carry block state data this matches the block's item regardless of state.
	 */
	public ScanBlockState(String research, BlockState blockState, boolean item) {
		this.research = research;
		this.blockState = blockState;
		if (item) 
			ScanningManager.addScannableThing(new ScanItem(research, new ItemStack(blockState.getBlock())));
	}

	@Override
	public boolean checkThing(Player player, Object obj) {		
		if (obj!=null && obj instanceof BlockPos && player.level().getBlockState((BlockPos) obj)==blockState) {
				return true;
		}
		return false;
	}
	
	@Override
	public String getResearchKey(Player player, Object object) {
		return research;
	}
}
