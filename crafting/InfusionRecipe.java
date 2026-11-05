package thaumcraft.api.crafting;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.ingredients.AbstractIngredient;
import net.minecraftforge.common.crafting.ingredients.IIngredientSerializer;
import net.minecraftforge.common.util.RecipeMatcher;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class InfusionRecipe implements IThaumcraftRecipe
{
	/**
	 * Central item ingredient that accepts any item. Replaces 1.12's Ingredient.EMPTY, which no longer exists.
	 * An empty ItemStack passed as the central item is converted to this.
	 * This ingredient cannot be serialized.
	 */
	public static final Ingredient ANY_SOURCE = new AbstractIngredient() {
		@Override
		public boolean test(ItemStack stack) {
			return true;
		}

		@Override
		public boolean isSimple() {
			return false;
		}

		@Override
		public IIngredientSerializer<? extends Ingredient> serializer() {
			throw new UnsupportedOperationException("InfusionRecipe.ANY_SOURCE cannot be serialized");
		}
	};
	
	public AspectList aspects;
	public String research;
	private String name;
	protected NonNullList<Ingredient> components = NonNullList.create();
	public Ingredient sourceInput; //Use ANY_SOURCE if the source item can be anything
	public Object recipeOutput;
	public int instability;
	
	public InfusionRecipe(String research, Object outputResult, int inst, AspectList aspects2, Object centralItem, Object ... recipe) {
		this.name="";
		this.research = research;
		this.recipeOutput = outputResult;
		this.aspects = aspects2;
		this.instability = inst;		
		this.sourceInput = centralItem instanceof ItemStack && ((ItemStack) centralItem).isEmpty() ? ANY_SOURCE : ThaumcraftApiHelper.getIngredient(centralItem);
		if (sourceInput==null) {
			String ret = "Invalid infusion central item: "+centralItem;
            throw new RuntimeException(ret);
		}		
		for (Object in : recipe)
        {
            Ingredient ing = ThaumcraftApiHelper.getIngredient(in);
            if (ing != null) {
            	components.add(ing);
            } else {
                String ret = "Invalid infusion recipe: ";
                for (Object tmp :  recipe)
                {
                    ret += tmp + ", ";
                }
                ret += outputResult;
                throw new RuntimeException(ret);
            }
        }
	}

	/**
     * Used to check if a recipe matches current crafting inventory
     * @param player 
     */
	public boolean matches(List<ItemStack> input, ItemStack central, Level world, Player player) {
		if (getRecipeInput()==null) return false;			
		if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
    		return false;
    	}		
		return (getRecipeInput()==ANY_SOURCE || this.getRecipeInput().test(central)) && RecipeMatcher.findMatches(input, getComponents()) != null;
    }
    
	@Override
    public String getResearch() {
		return research;
    }
    
	public Ingredient getRecipeInput() {
		return sourceInput;
	}

	public NonNullList<Ingredient> getComponents() {
		return components;
	}
	
	public Object getRecipeOutput() {
		return recipeOutput;
	}
	
	public AspectList getAspects() {
		return aspects;
	}			
	
	public Object getRecipeOutput(Player player, ItemStack input, List<ItemStack> comps ) {
		return recipeOutput;
    }
    
    public AspectList getAspects(Player player, ItemStack input, List<ItemStack> comps) {
		return aspects;
    }
    
    public int getInstability(Player player, ItemStack input, List<ItemStack> comps) {
		return instability;
    }
    
    private String group="";
	
	@Override
	public String getGroup() {
		return group;
	}
	
	public InfusionRecipe setGroup(Identifier s) {
		this.group=s.toString();
		return this;
	}
}
