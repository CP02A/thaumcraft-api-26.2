package thaumcraft.api.potions;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;

/**
 * Flux taint effect. Registered by Thaumcraft as {@code thaumcraft:flux_taint}; its icon is the
 * {@code mob_effect} sprite of that name and its display name the translation key derived from the registry name.
 */
public class PotionFluxTaint extends MobEffect
{
    /** The registered effect; filled in by Thaumcraft at registration time. */
    public static Holder<MobEffect> instance = null; // will be instantiated at runtime
    
    /**
     * @param par2 unused: this effect is always harmful
     * @param par3 the effect color
     */
    public PotionFluxTaint(boolean par2, int par3)
    {
    	super(MobEffectCategory.HARMFUL, par3);
    }
	
	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity target, int strength) {
		AttributeInstance cai = target.getAttribute(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(ThaumcraftApiHelper.CHAMPION_MOD));
		if (target instanceof ITaintedMob || (cai!=null && (int) cai.getValue() == 13)) {
			target.heal(1);
		} else {
			if (!target.isInvertedHealAndHarm() && !(target instanceof Player))
	        {
				target.hurtServer(level, DamageSourceThaumcraft.taint(level), 1);		
	        } 
			else
			if (!target.isInvertedHealAndHarm() && (target.getMaxHealth() > 1 || (target instanceof Player)))
	        {
				target.hurtServer(level, DamageSourceThaumcraft.taint(level), 1);
	        } 
		}
		return true;
	}
    
	@Override
	public boolean shouldApplyEffectTickThisTick(int par1, int par2)
    {
		int k = 40 >> par2;
        return k > 0 ? par1 % k == 0 : true;
    }
    
}
