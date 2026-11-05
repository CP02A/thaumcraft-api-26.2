package thaumcraft.api.capabilities;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * 
 * @author Azanor
 *
 */
public class ThaumcraftCapabilities {

	//PLAYER RESEARCH/////////////////////////////////////////
	
	/**
	 * The capability object for IPlayerKnowledge
	 */
	public static final Capability<IPlayerKnowledge> KNOWLEDGE = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * Retrieves the knowledge capability handler for the supplied player
	 */
	public static IPlayerKnowledge getKnowledge(@Nonnull Player player)
	{
		return player.getCapability(KNOWLEDGE).orElse(null);
	}
	
	/**
	 * Shortcut method to check if player knows the passed research entries. All must be true
	 * Research does not need to be complete, just 'in progress' 
	 * Individual entries can also contain && to do a 'and' check, e.g. "basicgolemancy&&infusion"
	 * Handy for recipes where multiple researches need to be true to be craftable 
	 * Individual entries can also contain || to do an 'or' check, e.g. "basicgolemancy||infusion"
	 * Queries should NOT contain both && and || - shennanigans will occur.
	 * @param player
	 * @param research 
	 * @return
	 */
	public static boolean knowsResearch(@Nonnull Player player, @Nonnull String... research) {
		for (String r : research) {
			if (r.contains("&&")) {
				String[] rr = r.split("&&");
				if (!knowsResearch(player,rr)) return false;
			} else
			if (r.contains("||")) {
				String[] rr = r.split("\\|\\|");
				for (String str : rr)
					if (knowsResearch(player,str)) return true;
			} else
			if (!getKnowledge(player).isResearchKnown(r)) return false;
		}
		return true;
	}
	
	/**
	 * Shortcut method to check if player knows all the passed research entries. 
	 * Research needs to be complete and 'in progress' research will only count if a stage is passed in the research paramater (using @, eg. "FOCUSFIRE@2")
	 * Individual entries can also contain && to do a 'and' check, e.g. "basicgolemancy&&infusion"
	 * Handy for recipes where multiple researches need to be true to be craftable 
	 * Individual entries can also contain || to do an 'or' check, e.g. "basicgolemancy||infusion"
	 * Queries should NOT contain both && and || - shennanigans will occur.
	 * @param player
	 * @param research
	 * @return
	 */
	public static boolean knowsResearchStrict(@Nonnull Player player, @Nonnull String... research) {
		for (String r : research) {
			if (r.contains("&&")) {
				String[] rr = r.split("&&");
				if (!knowsResearchStrict(player,rr)) return false;
			} else
			if (r.contains("||")) {
				String[] rr = r.split("\\|\\|");
				for (String str : rr)
					if (knowsResearchStrict(player,str)) return true;
			} else
			if (r.contains("@")) {
				if (!getKnowledge(player).isResearchKnown(r)) return false;
			} else {
				if (!getKnowledge(player).isResearchComplete(r)) return false; 
			}
		}
		return true;
	}
	
	
	//PLAYER WARP/////////////////////////////////////////

	/**
	 * The capability object for IPlayerWarp
	 */
	public static final Capability<IPlayerWarp> WARP = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * Retrieves the warp capability handler for the supplied player
	 */
	public static IPlayerWarp getWarp(@Nonnull Player player)
	{
		return player.getCapability(WARP).orElse(null);
	}

	
	
	
}
