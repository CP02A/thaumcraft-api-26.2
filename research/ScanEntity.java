package thaumcraft.api.research;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueOutput;
import thaumcraft.api.ThaumcraftApi.EntityTagsNBT;
import thaumcraft.api.ThaumcraftApiHelper;

public class ScanEntity implements IScanThing {
	
	String research;	
	Class entityClass;
	EntityTagsNBT[] NBTData;
	
	/**
	 * false if the specific entity class should be used, or true if anything the inherits from that class is also allowed. 
	 */
	boolean inheritedClasses=false;

	public ScanEntity(String research, Class entityClass, boolean inheritedClasses) {
		this.research = research;
		this.entityClass = entityClass;
		this.inheritedClasses = inheritedClasses;
	}
	
	/**
	 * @param nbt values that must be present in the entity's saved data for it to match
	 */
	public ScanEntity(String research, Class entityClass, boolean inheritedClasses, EntityTagsNBT... nbt) {
		this.research = research;
		this.entityClass = entityClass;
		this.inheritedClasses = inheritedClasses;
		this.NBTData = nbt;
	}

	@Override
	public boolean checkThing(Player player, Object obj) {		
		if (obj!=null && ((!inheritedClasses && entityClass==obj.getClass()) || 
				(inheritedClasses && entityClass.isInstance(obj)))) {			
			if (NBTData!=null && NBTData.length>0 && obj instanceof Entity entity) {
				TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess());
				entity.saveWithoutId(out);
				CompoundTag tc = out.buildResult();
				for (EntityTagsNBT nbt:NBTData) {
					if (!tc.contains(nbt.name) || !ThaumcraftApiHelper.getNBTDataFromId(tc, tc.get(nbt.name).getId(), nbt.name).equals(nbt.value)) {
						return false;
					} 					
				} 			
			} 			
			return true;
		}
		return false;
	}

	@Override
	public String getResearchKey(Player player, Object object) {
		return research;
	}
	
}
