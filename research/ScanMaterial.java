package thaumcraft.api.research;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

/**
 * Block materials no longer exist. This now matches any block that is in one of the given block tags, 
 * for example <code>BlockTags.LOGS</code> or <code>BlockTags.LEAVES</code>.
 */
public class ScanMaterial implements IScanThing {
	
	String research;	
	TagKey<Block>[] mats;
	
	/**
	 * The research key will be "!" followed by the tag id, for example "!minecraft:logs"
	 */
	@SuppressWarnings("unchecked")
	public ScanMaterial(TagKey<Block> mat) {
		research = "!"+mat.location().toString();
		this.mats = new TagKey[] {mat};
	}

	@SafeVarargs
	public ScanMaterial(String research, TagKey<Block> ... mats) {
		this.research = research;
		this.mats = mats;
	}
	
	@Override
	public boolean checkThing(Player player, Object obj) {		
		if (obj!=null && obj instanceof BlockPos) {
			for (TagKey<Block> mat:mats) 
				if (player.level().getBlockState((BlockPos) obj).is(mat)) 
					return true;
		}
		return false;
	}
	
	@Override
	public String getResearchKey(Player player, Object object) {		
		return research;
	}
}
