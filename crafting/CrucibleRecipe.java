package thaumcraft.api.crafting;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class CrucibleRecipe implements IThaumcraftRecipe  {

	private ItemStack recipeOutput;	
	private Ingredient catalyst;
	private AspectList aspects;
	private String research;
	private String name;
	public int hash;
	
	
	public CrucibleRecipe(String researchKey, ItemStack result, Object catalyst, AspectList tags) {
		recipeOutput = result;
		this.name="";
		this.setAspects(tags);
		this.research = researchKey;
		this.setCatalyst(ThaumcraftApiHelper.getIngredient(catalyst));
		
		if (this.getCatalyst() == null)
        {
            throw new RuntimeException("Invalid crucible recipe catalyst: "+ catalyst);
        }
		
		generateHash(catalyst);
	}
		
	/**
	 * The hash is built from the research, the output and the catalyst as it was passed in.
	 * Tag ingredients are hashed by their tag id rather than their contents, because tags are not
	 * bound until data packs load and their contents can change on reload.
	 */
	private void generateHash(Object cat) {
		String hc = research;		
		hc += recipeOutput.toString();
		if (!recipeOutput.getComponentsPatch().isEmpty()) {
			hc += recipeOutput.getComponentsPatch().toString();
		}	
		if (cat instanceof ItemStack) {
			ItemStack is = (ItemStack) cat;
			hc += is.toString();
			if (!is.getComponentsPatch().isEmpty()) {
				hc += is.getComponentsPatch().toString();
			}
		} else if (cat instanceof Ingredient) {
			hc += ((Ingredient) cat).display().toString();
		} else {
			hc += String.valueOf(cat);
		}
		hash = hc.hashCode();
	}

	public boolean matches(AspectList itags, ItemStack cat) {	
		if (!getCatalyst().test(cat)) return false;		
		if (itags==null) return false;
		for (Aspect tag:getAspects().getAspects()) {
			if (itags.getAmount(tag)<getAspects().getAmount(tag)) return false;
		}
		return true;
	}
	
	public boolean catalystMatches(ItemStack cat) {
		return getCatalyst().test(cat);
	}
	
	public AspectList removeMatching(AspectList itags) {
		AspectList temptags = new AspectList();
		temptags.aspects.putAll(itags.aspects);		
		for (Aspect tag:getAspects().getAspects()) {
			temptags.remove(tag, getAspects().getAmount(tag));
		}		
		itags = temptags;
		return itags;
	}
	
	public ItemStack getRecipeOutput() {
		return recipeOutput;
	}

	@Override
	public String getResearch() {
		return research;
	}

	public Ingredient getCatalyst() {
		return catalyst;
	}

	public void setCatalyst(Ingredient catalyst) {
		this.catalyst = catalyst;
	}

	public AspectList getAspects() {
		return aspects;
	}

	public void setAspects(AspectList aspects) {
		this.aspects = aspects;
	}

	private String group="";
	
	@Override
	public String getGroup() {
		return group;
	}
	
	public CrucibleRecipe setGroup(Identifier s) {
		this.group=s.toString();
		return this;
	}
}
