package thaumcraft.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.AspectList;

/**
 * A recipe crafted in the arcane workbench. Arcane recipes are kept in Thaumcraft's recipe catalog
 * (see {@link thaumcraft.api.ThaumcraftApi#addArcaneCraftingRecipe}), not in the vanilla recipe manager.
 * <p>
 * The workbench passes its contents as a {@link RecipeInput} with 15 slots: slots 0-8 are the 3x3 crafting grid
 * (row by row), slots 9-14 are the crystal slots.
 * The input must not be trimmed, so do not use {@link net.minecraft.world.item.crafting.CraftingInput#of},
 * which removes empty rows and columns and so changes the slot indices.
 */
public interface IArcaneRecipe extends IThaumcraftRecipe
{
	/**
	 * @return true if the items in the workbench match this recipe, including the required crystals
	 */
	public boolean matches(RecipeInput input, Level level);

	/**
	 * @return the crafted item for the given workbench contents.
	 */
	public ItemStack assemble(RecipeInput input);

	/**
	 * @return the normal result of this recipe, for display. Do not modify the returned stack.
	 */
	public ItemStack getRecipeOutput();

    public int getVis();
    public String getResearch();
    public AspectList getCrystals();
}
