package thaumcraft.api.damagesource;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

/**
 * Builds indirect damage sources (damage dealt by one entity, e.g. a projectile, on behalf of another).
 * <p>
 * This used to be a subclass of {@code EntityDamageSourceIndirect} created with
 * {@code new DamageSourceIndirectThaumcraftEntity(name, direct, indirect)} and configured with
 * {@code setFireDamage()} / {@code setDamageBypassesArmor()}. Damage sources are now built from a
 * data-driven {@code DamageType}; use {@link #create} with a damage type key, and express fire or
 * armor-bypassing behaviour by adding that damage type to the {@code minecraft:is_fire} or
 * {@code minecraft:bypasses_armor} damage type tags in the data pack.
 */
public class DamageSourceIndirectThaumcraftEntity {

	private DamageSourceIndirectThaumcraftEntity() {}

	/**
	 * Replaces the former constructor {@code DamageSourceIndirectThaumcraftEntity(String, Entity, Entity)}.
	 * @param type the damage type key (formerly the damage name string)
	 * @param directEntity the entity that dealt the damage (formerly par2Entity), used for registry access
	 * @param causingEntity the entity responsible for the damage (formerly par3Entity), may be null
	 */
	public static DamageSource create(ResourceKey<DamageType> type, Entity directEntity, @Nullable Entity causingEntity) {
		return DamageSourceThaumcraft.create(directEntity.level(), type, directEntity, causingEntity);
	}
}
