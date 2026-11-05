package thaumcraft.api.potions;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Vis exhaustion effect. Registered by Thaumcraft as {@code thaumcraft:vis_exhaust}; its icon is the
 * {@code mob_effect} sprite of that name and its display name the translation key derived from the registry name.
 */
public class PotionVisExhaust extends MobEffect
{
    /** The registered effect; filled in by Thaumcraft at registration time. */
    public static Holder<MobEffect> instance = null; // will be instantiated at runtime
    
    /**
     * @param par2 unused: this effect is always harmful
     * @param par3 the effect color
     */
    public PotionVisExhaust(boolean par2, int par3)
    {
    	super(MobEffectCategory.HARMFUL, par3);
    }
	
	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity target, int par2) {
		return true;
	}
    
    
}
