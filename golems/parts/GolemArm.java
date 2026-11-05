package thaumcraft.api.golems.parts;

import java.util.Locale;

import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;

public class GolemArm {
	
	protected static GolemArm[] arms = new GolemArm[1];
	
	public byte id;
	public String key;
	public String[] research;
	public Identifier icon;
	public Object[] components;
	public EnumGolemTrait[] traits;
	public IArmFunction function;
	public PartModel model;

	public GolemArm(String key, String[] research, Identifier icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
		this.key = key;
		this.research = research;
		this.icon = icon;
		this.components = comp;
		this.traits = tags;
		this.model = model;
		this.function = null;
	}
	
	public GolemArm(String key, String[] research, Identifier icon, PartModel model, Object[] comp, IArmFunction function, EnumGolemTrait[] tags) {
		this(key, research, icon, model, comp, tags);
		this.function = function;
	}
	
	private static byte lastID = 0;
	
	public static void register(GolemArm thing) {
		thing.id = lastID;
		lastID++;
		// allocate space
		if (thing.id >= arms.length) {
			GolemArm[] temp = new GolemArm[thing.id + 1];
			System.arraycopy(arms, 0, temp, 0, arms.length);
			arms = temp;
		}
		arms[thing.id] = thing;
	}
	
	public String getLocalizedName() {
		return Language.getInstance().getOrDefault("golem.arm." + this.key.toLowerCase(Locale.ROOT));
	}
	
	public String getLocalizedDescription() {
		return Language.getInstance().getOrDefault("golem.arm.text." + this.key.toLowerCase(Locale.ROOT));
	}
	
	public static GolemArm[] getArms() {
		return arms;
	}
	
	public interface IArmFunction extends IGenericFunction {
		public void onMeleeAttack(IGolemAPI golem, Entity target);
		
		public void onRangedAttack(IGolemAPI golem, LivingEntity target, float range);
		
		public RangedAttackGoal getRangedAttackAI(RangedAttackMob golem);
	}
	
}
