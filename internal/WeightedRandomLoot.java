package thaumcraft.api.internal;

import java.util.ArrayList;

import net.minecraft.world.item.ItemStack;

/**
 * A weighted loot bag entry. Weighted random selection is done by Thaumcraft over these lists.
 */
public class WeightedRandomLoot {
	
	/** The item to generate in the bag. */
    public ItemStack item;
    
    /** The relative weight of this entry. */
    public final int weight;

    public WeightedRandomLoot(ItemStack stack, int weight)
    {
        this.weight = weight;
        this.item = stack;
    }
    
    public int getWeight() {
    	return weight;
    }
    
    public static ArrayList<WeightedRandomLoot> lootBagCommon = new ArrayList<WeightedRandomLoot>();
    public static ArrayList<WeightedRandomLoot> lootBagUncommon = new ArrayList<WeightedRandomLoot>();
    public static ArrayList<WeightedRandomLoot> lootBagRare = new ArrayList<WeightedRandomLoot>();
	
}
