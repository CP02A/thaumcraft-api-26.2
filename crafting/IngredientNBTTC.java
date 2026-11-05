package thaumcraft.api.crafting;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.ingredients.AbstractIngredient;
import net.minecraftforge.common.crafting.ingredients.IIngredientSerializer;
import thaumcraft.api.ThaumcraftInvHelper;

/**
 * An ingredient that matches the same item with a relaxed custom data match: every key in the recipe stack's
 * custom data must be present and equal in the tested stack, extra keys are ignored.
 * See {@link ThaumcraftInvHelper#areItemStackTagsEqualRelaxed(ItemStack, ItemStack)}.
 * <p>
 * {@link #SERIALIZER} is only needed if this ingredient is written to data or sent over the network; in that case
 * it must be registered in Forge's ingredient serializer registry.
 */
public class IngredientNBTTC extends AbstractIngredient
{
	public static final MapCodec<IngredientNBTTC> CODEC = ItemStack.CODEC.fieldOf("stack").xmap(IngredientNBTTC::new, IngredientNBTTC::getStack);

	public static final IIngredientSerializer<IngredientNBTTC> SERIALIZER = new IIngredientSerializer<IngredientNBTTC>() {
		@Override
		public MapCodec<? extends IngredientNBTTC> codec() {
			return CODEC;
		}

		@Override
		public void write(RegistryFriendlyByteBuf buffer, IngredientNBTTC value) {
			ItemStack.STREAM_CODEC.encode(buffer, value.stack);
		}

		@Override
		public IngredientNBTTC read(RegistryFriendlyByteBuf buffer) {
			return new IngredientNBTTC(ItemStack.STREAM_CODEC.decode(buffer));
		}
	};

	private final ItemStack stack;
    public IngredientNBTTC(ItemStack stack)
    {
        super(HolderSet.direct(stack.typeHolder()));
        this.stack = stack.copy();
    }

    public ItemStack getStack() {
    	return stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input)
    {
        if (input == null)
        	return false;
        return  this.stack.getItem() == input.getItem() &&
        		ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(this.stack, input);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

	@Override
	public IIngredientSerializer<? extends Ingredient> serializer() {
		return SERIALIZER;
	}
}
