package thaumcraft.api.golems.parts;

import java.util.Locale;

import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemHead {
	
	protected static GolemHead[] heads = new GolemHead[1];
	
	public byte id;
	public String key;
	public String[] research;
	public Identifier icon;
	public Object[] components;
	public EnumGolemTrait[] traits;
	public IHeadFunction function;
	public PartModel model;

	public GolemHead(String key, String[] research, Identifier icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
		this.key = key;
		this.research = research;
		this.icon = icon;
		this.components = comp;
		this.traits = tags;
		this.model = model;
		this.function = null;
	}
	
	public GolemHead(String key, String[] research, Identifier icon, PartModel model, Object[] comp, IHeadFunction function, EnumGolemTrait[] tags) {
		this(key, research, icon, model, comp, tags);
		this.function = function;
	}
	
	private static byte lastID = 0;
	
	public static void register(GolemHead thing) {
		thing.id = lastID;
		lastID++;
		// allocate space
		if (thing.id >= heads.length) {
			GolemHead[] temp = new GolemHead[thing.id + 1];
			System.arraycopy(heads, 0, temp, 0, heads.length);
			heads = temp;
		}
		heads[thing.id] = thing;
	}
	
	public String getLocalizedName() {
		return Language.getInstance().getOrDefault("golem.head." + this.key.toLowerCase(Locale.ROOT));
	}
	
	public String getLocalizedDescription() {
		return Language.getInstance().getOrDefault("golem.head.text." + this.key.toLowerCase(Locale.ROOT));
	}
	
	public static GolemHead[] getHeads() {
		return heads;
	}
	
	public interface IHeadFunction extends IGenericFunction { }
	
}
