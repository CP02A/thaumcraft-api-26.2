package thaumcraft.api.golems.parts;

import java.util.Locale;

import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemMaterial {
	
	protected static GolemMaterial[] materials = new GolemMaterial[1];
	
	public byte id;
	public String key;
	public String[] research;
	/** The texture used by the golem model */
	public Identifier texture;
	/** The color used to tint golem items made from this material */
	public int itemColor;
	public int healthMod;
	public int armor;
	public int damage;
	/** The base component used to craft golems of this material */
	public ItemStack componentBase;
	/** The mechanism component used to craft golems of this material */
	public ItemStack componentMechanism;
	public EnumGolemTrait[] traits;

	public GolemMaterial(String key, String[] research, Identifier texture, int itemColor, int hp, int armor, int damage, 
			ItemStack compb, ItemStack compm, EnumGolemTrait[] tags) {
		this.key = key;
		this.research = research;
		this.texture = texture;
		this.itemColor = itemColor;
		this.componentBase = compb;
		this.componentMechanism = compm;
		this.healthMod = hp;
		this.armor = armor;
		this.traits = tags;
		this.damage = damage;
	}
	
	private static byte lastID = 0;
	
	public static void register(GolemMaterial thing) {
		thing.id = lastID;
		lastID++;
		// allocate space
		if (thing.id >= materials.length) {
			GolemMaterial[] temp = new GolemMaterial[thing.id + 1];
			System.arraycopy(materials, 0, temp, 0, materials.length);
			materials = temp;
		}
		materials[thing.id] = thing;
	}
	
	public String getLocalizedName() {
		return Language.getInstance().getOrDefault("golem.material." + this.key.toLowerCase(Locale.ROOT));
	}
	
	public String getLocalizedDescription() {
		return Language.getInstance().getOrDefault("golem.material.text." + this.key.toLowerCase(Locale.ROOT));
	}
	
	public static GolemMaterial[] getMaterials() {
		return materials;
	}
	
}
