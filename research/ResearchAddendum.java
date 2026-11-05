package thaumcraft.api.research;

import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;

public class ResearchAddendum {
	String text;
	Identifier[] recipes;
	String[] research;
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	public String getTextLocalized() {
		return Language.getInstance().getOrDefault(getText());
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}	
	
	/**
	 * @return the recipes
	 */
	public Identifier[] getRecipes() {
		return recipes;
	}
	/**
	 * @param recipes the recipes to set
	 */
	public void setRecipes(Identifier[] recipes) {
		this.recipes = recipes;
	}
	
	/**
	 * @return the research
	 */
	public String[] getResearch() {
		return research;
	}
	/**
	 * @param research the research to set
	 */
	public void setResearch(String[] research) {
		this.research = research;
	}
	
}
