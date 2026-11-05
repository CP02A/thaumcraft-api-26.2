package thaumcraft.api.golems;

import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;

public enum EnumGolemTrait {
	SMART(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_smart.png")), 
	DEFT(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_deft.png")), 
	CLUMSY(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_clumsy.png")), 
	FIGHTER(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_fighter.png")), 
	WHEELED(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_wheeled.png")), 
	FLYER(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_flyer.png")), 
	CLIMBER(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_climber.png")),
	HEAVY(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_heavy.png")),
	LIGHT(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_light.png")),
	FRAGILE(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_fragile.png")),
	REPAIR(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_repair.png")), 
	SCOUT(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_scout.png")), 
	ARMORED(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_armored.png")), 
	BRUTAL(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_brutal.png")),
	FIREPROOF(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_fireproof.png")),
	BREAKER(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_breaker.png")),
	HAULER(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_hauler.png")),
	RANGED(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_ranged.png")),
	BLASTPROOF(Identifier.fromNamespaceAndPath("thaumcraft","textures/misc/golem/tag_blastproof.png"));
	
	static {
		CLUMSY.opposite = DEFT;
		DEFT.opposite = CLUMSY;
		
		HEAVY.opposite = LIGHT;
		LIGHT.opposite = HEAVY;
		
		FRAGILE.opposite = ARMORED;
		ARMORED.opposite = FRAGILE;
	}
	
	public Identifier icon;
	public EnumGolemTrait opposite;
	
	private EnumGolemTrait(Identifier icon) {
		this.icon = icon;
	}
	
	public String getLocalizedName() {
		return Language.getInstance().getOrDefault("golem.trait."+this.name().toLowerCase());
	}
	
	public String getLocalizedDescription() {
		return Language.getInstance().getOrDefault("golem.trait.text."+this.name().toLowerCase());
	}
}