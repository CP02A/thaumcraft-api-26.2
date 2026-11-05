package thaumcraft.api.crafting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

/**
 * A shapeless arcane workbench recipe. The recipe arguments are the ingredients, each anything
 * {@link ThaumcraftApiHelper#getIngredient(Object)} accepts.
 */
public class ShapelessArcaneRecipe implements IArcaneRecipe
{
	private String research;
	private int vis;
	private AspectList crystals;
	private String group;
	@Nonnull
	private ItemStack output;
	private NonNullList<Ingredient> input = NonNullList.create();

	public ShapelessArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, ItemLike result, Object... recipe){ this(group, res, vis, crystals, new ItemStack(result), recipe); }
    public ShapelessArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, NonNullList<Ingredient> input, @Nonnull ItemStack result)
    {
    	this.group = group == null ? "" : group.toString();
    	this.research = res;
		this.vis = vis;
		this.crystals = crystals;
		this.output = result.copy();
		this.input = input;
    }
	public ShapelessArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, @Nonnull ItemStack result, Object... recipe) {
		this.group = group == null ? "" : group.toString();
		this.research = res;
		this.vis = vis;
		this.crystals = crystals;
		this.output = result.copy();
		for (Object in : recipe)
        {
            Ingredient ing = ThaumcraftApiHelper.getIngredient(in);
            if (ing != null) {
                input.add(ing);
            } else {
                String ret = "Invalid shapeless arcane recipe: ";
                for (Object tmp :  recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }
	}

	@Override
	public ItemStack assemble(RecipeInput input) {
		return output.copy();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public boolean matches(RecipeInput inv, Level world) {

		if (crystals!=null && inv.size()>=15)
		for (Aspect aspect:crystals.getAspects()) {
			ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect,crystals.getAmount(aspect));
			boolean b = false;
    		for (int i = 0; i < 6; ++i)
            {
            	ItemStack itemstack1 = inv.getItem(9+i);
            	if (itemstack1!=null && itemstack1.getItem()==ItemsTC.crystalEssence && itemstack1.getCount()>=cs.getCount() && ItemStack.isSameItemSameComponents(cs,itemstack1))
                {
                    b = true;
                }
            }
    		if (!b) return false;
    	}

		List<ItemStack> items = new ArrayList<>();
		for (int a = 0; a < 9 && a < inv.size(); a++) {
			ItemStack stack = inv.getItem(a);
			if (!stack.isEmpty()) items.add(stack);
		}
		return items.size() == input.size() && RecipeMatcher.findMatches(items, input) != null;
	}

	/**
	 * @return the ingredients of this recipe
	 */
	public NonNullList<Ingredient> getIngredients() {
		return input;
	}

	@Override
	public int getVis() {
		return vis;
	}

	@Override
	public String getResearch() {
		return research;
	}

	@Override
	public AspectList getCrystals() {
		return crystals;
	}

	@Override
	public String getGroup() {
		return group;
	}

}
