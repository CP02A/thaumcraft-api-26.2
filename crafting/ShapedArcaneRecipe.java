package thaumcraft.api.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

/**
 * A shaped arcane workbench recipe. The pattern may be placed anywhere in the 3x3 grid, and also matches when mirrored
 * (unless the recipe was created with a leading <code>false</code>).
 * <p>
 * The recipe arguments use the classic form: an optional leading Boolean (mirrored, default true), then the pattern rows
 * (as separate Strings or one String[]), then character/value pairs. A space in the pattern is an empty slot.
 * Values can be anything {@link ThaumcraftApiHelper#getIngredient(Object)} accepts, for example:
 * <pre>new ShapedArcaneRecipe(group, "RESEARCH", 25, crystals, result, "AB", "BA", 'A', Items.STICK, 'B', "#c:ingots/iron")</pre>
 */
public class ShapedArcaneRecipe implements IArcaneRecipe
{
	private static final int MAX_CRAFT_GRID_WIDTH = 3;
	private static final int MAX_CRAFT_GRID_HEIGHT = 3;

	private String research;
	private int vis;
	private AspectList crystals;
	private String group;
	@Nonnull
	private ItemStack output;
	private List<Optional<Ingredient>> input;
	private int width;
	private int height;
	private boolean mirrored = true;

	public ShapedArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, ItemLike result, Object... recipe){ this(group, res, vis, crystals, new ItemStack(result), recipe); }
	public ShapedArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, @Nonnull ItemStack result, Object... recipe) {
		this.group = group == null ? "" : group.toString();
		this.research = res;
		this.vis = vis;
		this.crystals = crystals;
		this.output = result.copy();
		parseShaped(recipe);
	}

	private void parseShaped(Object... recipe) {
		String shape = "";
		int idx = 0;

		if (idx < recipe.length && recipe[idx] instanceof Boolean) {
			mirrored = (Boolean) recipe[idx];
			idx++;
		}

		if (idx < recipe.length && recipe[idx] instanceof String[]) {
			String[] parts = (String[]) recipe[idx++];
			for (String s : parts) {
				width = s.length();
				shape += s;
			}
			height = parts.length;
		} else {
			while (idx < recipe.length && recipe[idx] instanceof String) {
				String s = (String) recipe[idx++];
				shape += s;
				width = s.length();
				height++;
			}
		}

		if (width * height != shape.length() || shape.length() == 0 || width > MAX_CRAFT_GRID_WIDTH || height > MAX_CRAFT_GRID_HEIGHT) {
			throw new RuntimeException("Invalid shaped arcane recipe: " + recipeToString(recipe));
		}

		Map<Character, Ingredient> itemMap = new HashMap<>();
		for (; idx < recipe.length; idx += 2) {
			if (!(recipe[idx] instanceof Character) || idx + 1 >= recipe.length) {
				throw new RuntimeException("Invalid shaped arcane recipe: " + recipeToString(recipe));
			}
			Character chr = (Character) recipe[idx];
			if (chr == ' ') {
				throw new RuntimeException("Invalid key entry: ' ' is a reserved symbol. " + recipeToString(recipe));
			}
			Ingredient ing = ThaumcraftApiHelper.getIngredient(recipe[idx + 1]);
			if (ing == null) {
				throw new RuntimeException("Invalid shaped arcane recipe ingredient for '" + chr + "': " + recipe[idx + 1] + " in " + recipeToString(recipe));
			}
			itemMap.put(chr, ing);
		}

		ArrayList<Optional<Ingredient>> list = new ArrayList<>(shape.length());
		for (char chr : shape.toCharArray()) {
			if (chr == ' ') {
				list.add(Optional.empty());
				continue;
			}
			Ingredient ing = itemMap.get(chr);
			if (ing == null) {
				throw new RuntimeException("Pattern references symbol '" + chr + "' but it's not defined in the key. " + recipeToString(recipe));
			}
			list.add(Optional.of(ing));
		}
		input = Collections.unmodifiableList(list);
	}

	private static String recipeToString(Object[] recipe) {
		StringBuilder ret = new StringBuilder();
		for (Object tmp : recipe) ret.append(tmp).append(", ");
		return ret.toString();
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

		if (inv.size()<15) return false;

		if (crystals!=null)
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

		for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++) {
			for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y) {
				if (checkMatch(inv, x, y, false)) return true;
				if (mirrored && checkMatch(inv, x, y, true)) return true;
			}
		}
		return false;
	}

	/**
	 * Checks the 3x3 grid (slots 0-8) against the pattern placed with its top left corner at startX, startY.
	 */
	protected boolean checkMatch(RecipeInput inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
			for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
				int subX = x - startX;
				int subY = y - startY;
				Optional<Ingredient> target = Optional.empty();

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = input.get(width - subX - 1 + subY * width);
					} else {
						target = input.get(subX + subY * width);
					}
				}

				if (!Ingredient.testOptionalIngredient(target, inv.getItem(x + y * MAX_CRAFT_GRID_WIDTH))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return the pattern ingredients, row by row ({@link #getWidth()} per row). Empty entries are empty slots.
	 */
	public List<Optional<Ingredient>> getIngredients() {
		return input;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isMirrored() {
		return mirrored;
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
