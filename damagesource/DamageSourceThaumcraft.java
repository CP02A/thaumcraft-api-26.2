package thaumcraft.api.damagesource;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Thaumcraft damage types.
 * <p>
 * Damage types are data-driven: each key below refers to a {@code DamageType} that Thaumcraft
 * ships in its data pack ({@code data/thaumcraft/damage_type/<name>.json}). Properties that used to be
 * set in code (bypasses armor, magic, fire) are expressed through the damage type tags
 * ({@code minecraft:bypasses_armor}, {@code minecraft:is_magic} etc.).
 * <p>
 * The former static fields {@code DamageSourceThaumcraft.taint}, {@code tentacle}, {@code swarm} and
 * {@code dissolve} are replaced by the {@link #TAINT}, {@link #TENTACLE}, {@link #SWARM} and {@link #DISSOLVE}
 * keys and by the helper methods {@link #taint(Level)}, {@link #tentacle(Level)}, {@link #swarm(Level)} and
 * {@link #dissolve(Level)}, which build a {@link DamageSource} for the given level.
 */
public class DamageSourceThaumcraft
{
	/** Formerly {@code DamageSourceThaumcraft.taint} (bypasses armor, magic damage). */
	public static final ResourceKey<DamageType> TAINT = key("taint");
	/** Formerly {@code DamageSourceThaumcraft.tentacle}. */
	public static final ResourceKey<DamageType> TENTACLE = key("tentacle");
	/** Formerly {@code DamageSourceThaumcraft.swarm}. */
	public static final ResourceKey<DamageType> SWARM = key("swarm");
	/** Formerly {@code DamageSourceThaumcraft.dissolve} (bypasses armor). */
	public static final ResourceKey<DamageType> DISSOLVE = key("dissolve");
	
	private DamageSourceThaumcraft() {}
	
	private static ResourceKey<DamageType> key(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("thaumcraft", name));
	}
	
	/**
	 * Looks up the damage type holder for the given key.
	 * @param registries usually {@code level.registryAccess()}
	 */
	public static Holder<DamageType> getDamageType(HolderLookup.Provider registries, ResourceKey<DamageType> type) {
		return registries.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type);
	}
	
	/**
	 * Creates a damage source of the given type.
	 * @param level used for registry access
	 * @param type the damage type key
	 * @param directEntity the entity that directly dealt the damage (e.g. a projectile), may be null
	 * @param causingEntity the entity responsible for the damage, may be null
	 */
	public static DamageSource create(Level level, ResourceKey<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
		return new DamageSource(getDamageType(level.registryAccess(), type), directEntity, causingEntity);
	}
	
	/** Creates a damage source of the given type with no attacker. */
	public static DamageSource create(Level level, ResourceKey<DamageType> type) {
		return new DamageSource(getDamageType(level.registryAccess(), type));
	}
	
	/** Formerly the static field {@code DamageSourceThaumcraft.taint}. */
	public static DamageSource taint(Level level) {
		return create(level, TAINT);
	}
	
	/** Formerly the static field {@code DamageSourceThaumcraft.tentacle}. */
	public static DamageSource tentacle(Level level) {
		return create(level, TENTACLE);
	}
	
	/** Formerly the static field {@code DamageSourceThaumcraft.swarm}. */
	public static DamageSource swarm(Level level) {
		return create(level, SWARM);
	}
	
	/** Formerly the static field {@code DamageSourceThaumcraft.dissolve}. */
	public static DamageSource dissolve(Level level) {
		return create(level, DISSOLVE);
	}
	
	/** Swarm damage attributed to the given entity. */
	public static DamageSource causeSwarmDamage(LivingEntity par0EntityLiving)
	{
		return create(par0EntityLiving.level(), SWARM, par0EntityLiving, par0EntityLiving);
	}

	/** Tentacle damage attributed to the given entity. */
	public static DamageSource causeTentacleDamage(LivingEntity par0EntityLiving)
	{
		return create(par0EntityLiving.level(), TENTACLE, par0EntityLiving, par0EntityLiving);
	}
	
}
