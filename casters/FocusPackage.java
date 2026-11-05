package thaumcraft.api.casters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

public class FocusPackage implements IFocusElement {
	
	@Override
	public String getResearch() {
		return null;
	}
	
	public Level world;
	private LivingEntity caster;	
	private UUID casterUUID;
	
	private float power = 1;
	private int complexity = 0;
	
	int index;
	UUID uid;
	
	public List<IFocusElement> nodes =  Collections.synchronizedList(new ArrayList<>());	
	
	public FocusPackage() {	}

	public FocusPackage(LivingEntity caster) {
		super();
		this.world = caster.level();
		this.caster = caster;
		this.casterUUID = caster.getUUID();
	}	
		
	@Override
	public String getKey() {
		return "thaumcraft.PACKAGE";
	}

	@Override
	public EnumUnitType getType() {
		return EnumUnitType.PACKAGE;
	}
	
	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public UUID getUniqueID() {
		return uid;
	}

	public void setUniqueID(UUID id) {
		this.uid = id;
	}
	
	public int getExecutionIndex() {
		return index;
	}

	public void setExecutionIndex(int idx) {
		this.index = idx;
	}
	
	public void addNode(IFocusElement e) {
		nodes.add(e);
	}
	
	public UUID getCasterUUID() {
		if (caster!=null) casterUUID = caster.getUUID();
		return casterUUID;
	}

	public void setCasterUUID(UUID casterUUID) {
		this.casterUUID = casterUUID;
	}	
	
	public LivingEntity getCaster() {
		try {
			if (caster==null) {
				caster = world.getPlayerByUUID(getCasterUUID());
			}
			if (caster==null) {
				Entity e = world.getEntity(getCasterUUID());
				if (e instanceof LivingEntity && e.isAlive()) {
					caster = (LivingEntity) e;
				}
			}
		} catch (Exception e) {}
		return caster;
	}
	
	public FocusEffect[] getFocusEffects() {		
		return getFocusEffectsPackage(this);
	}
	
	private FocusEffect[] getFocusEffectsPackage(FocusPackage fp) {
		ArrayList<FocusEffect> out = new ArrayList<>();
		for (IFocusElement el:fp.nodes) {
			if (el instanceof FocusEffect) out.add((FocusEffect) el);
			else
			if (el instanceof FocusPackage) {
				for (FocusEffect fep:getFocusEffectsPackage((FocusPackage) el))
					out.add(fep);
			} else 
			if (el instanceof FocusModSplit) {
				for (FocusPackage fsp:((FocusModSplit)el).getSplitPackages())
					for (FocusEffect fep:getFocusEffectsPackage(fsp))
						out.add(fep);
			}
		}
		return out.toArray(new FocusEffect[]{});
	}

	public void deserialize(CompoundTag nbt) {
		uid = nbt.read("uid", UUIDUtil.CODEC).orElse(null);		
		index = nbt.getIntOr("index", 0);
		ResourceKey<Level> dim = nbt.read("dim", ResourceKey.codec(Registries.DIMENSION)).orElse(Level.OVERWORLD);
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		world = server!=null ? server.getLevel(dim) : null;
		setCasterUUID(nbt.read("casterUUID", UUIDUtil.CODEC).orElse(null));
		power = nbt.getFloatOr("power", 0);
		complexity = nbt.getIntOr("complexity", 0);
				
		ListTag nodelist = nbt.getListOrEmpty("nodes");
		nodes.clear();
		for (int x=0;x<nodelist.size();x++) {
			CompoundTag nodenbt = nodelist.getCompoundOrEmpty(x);
			EnumUnitType ut = EnumUnitType.valueOf(nodenbt.getStringOr("type", ""));
			if (ut!=null) {
				if (ut==EnumUnitType.PACKAGE) {
					FocusPackage fp = new FocusPackage();
					fp.deserialize(nodenbt.getCompoundOrEmpty("package"));
					nodes.add(fp);
					break;
				} else {
					IFocusElement fn = FocusEngine.getElement(nodenbt.getStringOr("key", "")); 
					if (fn!=null) {						
						if (fn instanceof FocusNode) {
							((FocusNode)fn).initialize();
							if (((FocusNode)fn).getSettingList()!=null)
								for (String ns : ((FocusNode)fn).getSettingList()) {
									((FocusNode)fn).getSetting(ns).setValue(nodenbt.getIntOr("setting."+ns, 0));
								}
							
							if (fn instanceof FocusModSplit) {								
								((FocusModSplit)fn).deserialize(nodenbt.getCompoundOrEmpty("packages"));		
							}
						}
						this.addNode(fn);
					}
				}
			}
		}
		
	}

	public CompoundTag serialize() {
		CompoundTag nbt = new CompoundTag();
		if (uid!=null) nbt.store("uid", UUIDUtil.CODEC, uid);
		nbt.putInt("index", index);
		if (getCasterUUID() != null) nbt.store("casterUUID", UUIDUtil.CODEC, getCasterUUID());
		if (world!=null) nbt.store("dim", ResourceKey.codec(Registries.DIMENSION), world.dimension());
		nbt.putFloat("power", power);
		nbt.putInt("complexity", complexity);
		
		//nodes
		ListTag nodelist = new ListTag();
		synchronized (nodes) {
			for (IFocusElement node:nodes) {
				if (node==null || node.getType()==null) continue;
				CompoundTag nodenbt = new CompoundTag();
				nodenbt.putString("type", node.getType().name());
				nodenbt.putString("key", node.getKey());
				if (node.getType()==EnumUnitType.PACKAGE) {
					nodenbt.put("package", ((FocusPackage)node).serialize());
					nodelist.add(nodenbt);
					break;
				} else {				
					if (node instanceof FocusNode && ((FocusNode)node).getSettingList()!=null)
						for (String ns : ((FocusNode)node).getSettingList()) {
							nodenbt.putInt("setting."+ns, ((FocusNode)node).getSettingValue(ns));
						}
					if (node instanceof FocusModSplit) {	
						nodenbt.put("packages", ((FocusModSplit)node).serialize());	
					}
					nodelist.add(nodenbt);
				}			
			}
		}
		nbt.put("nodes", nodelist);					
		
		return nbt;
	}

	public float getPower() {
		return power;
	}

	public void multiplyPower(float pow) {
		this.power *= pow;
	}

	public FocusPackage copy(LivingEntity caster) {
		FocusPackage fp = new FocusPackage(caster);
		fp.deserialize(this.serialize());
		return fp;
	}
	
	public void initialize(LivingEntity caster) {
		world=caster.level();
		IFocusElement node = nodes.get(0);
		if (node instanceof FocusMediumRoot && ((FocusMediumRoot)node).supplyTargets()==null) {
			((FocusMediumRoot)node).setupFromCaster(caster);
		}
	}

	public int getSortingHelper() {
		String s="";
		for (IFocusElement k:this.nodes) {
			s+=k.getKey();
			if (k instanceof FocusNode && ((FocusNode)k).getSettingList()!=null)
				for (String ns : ((FocusNode)k).getSettingList()) {
					s += ""+((FocusNode)k).getSettingValue(ns);
				}
		}		
		return s.hashCode();
	}

	
	
	

	
	
	
	
	

}
