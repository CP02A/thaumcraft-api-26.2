package thaumcraft.api.golems.parts;

import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.IGolemAPI;

/**
 * Describes the model of a golem part. The render hooks are called by Thaumcraft's golem renderer
 * and do nothing by default; override them to adjust how a part is drawn.
 */
public class PartModel {
	
	private Identifier objModel;
	private Identifier texture;
	private EnumAttachPoint attachPoint;
	
	public enum EnumAttachPoint {
		ARMS, LEGS, BODY, HEAD;
	}
	
	public enum EnumLimbSide {
		LEFT, RIGHT, MIDDLE;
	}
	
	public PartModel(Identifier objModel, Identifier objTexture, EnumAttachPoint attachPoint) {
		this.objModel = objModel;
		this.texture = objTexture;
		this.attachPoint = attachPoint;
	}

	public Identifier getObjModel() {
		return objModel;
	}

	public Identifier getTexture() {
		return texture;
	}

	public EnumAttachPoint getAttachPoint() {
		return attachPoint;
	}
	
	/**
	 * @return true if the named model part should be drawn with the golem material texture instead of the part texture.
	 * By default this is any part whose name starts with "bm".
	 */
	public boolean useMaterialTextureForObjectPart(String partName) {
		return partName.startsWith("bm");
	}
	
	public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) { }
	
	public void postRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, EnumLimbSide side) { }
	
	public float preRenderArmRotationX(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
		return inputRot;
	}
	
	public float preRenderArmRotationY(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
		return inputRot;
	}
	
	public float preRenderArmRotationZ(IGolemAPI golem, float partialTicks, EnumLimbSide side, float inputRot) {
		return inputRot;
	}
	
}
