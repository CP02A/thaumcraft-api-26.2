package thaumcraft.api.internal;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WorldCoordinates implements Comparable<WorldCoordinates>
{
    public BlockPos pos;
    
    public ResourceKey<Level> dim;

    public WorldCoordinates() {}

    public WorldCoordinates(BlockPos pos, ResourceKey<Level> d)
    {
        this.pos = pos;
        this.dim = d;
    }
    
    public WorldCoordinates(BlockEntity tile)
    {
        this.pos = tile.getBlockPos();
        this.dim = tile.getLevel().dimension();
    }

    public WorldCoordinates(WorldCoordinates par1ChunkCoordinates)
    {
        this.pos = par1ChunkCoordinates.pos;
        this.dim = par1ChunkCoordinates.dim;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof WorldCoordinates))
        {
            return false;
        }
        else
        {
        	WorldCoordinates coordinates = (WorldCoordinates)par1Obj;
            return this.pos.equals(coordinates.pos) && Objects.equals(this.dim, coordinates.dim) ;
        }
    }

    public int hashCode()
    {
        return Objects.hash(this.pos, this.dim);
    }

    /**
     * Compare the coordinate with another coordinate
     */
    public int compareWorldCoordinate(WorldCoordinates par1)
    {
        return Objects.equals(this.dim, par1.dim) ? this.pos.compareTo(par1.pos) : -1;
    }

    public void set(BlockPos pos, ResourceKey<Level> d)
    {
        this.pos = pos;
        this.dim = d;
    }

    /**
     * Returns the squared distance between this coordinates and the coordinates given as argument.
     */
    public double getDistanceSquared(BlockPos pos)
    {
        return this.pos.distSqr(pos);
    }

    /**
     * Return the squared distance between this coordinates and the ChunkCoordinates given as argument.
     */
    public double getDistanceSquaredToWorldCoordinates(WorldCoordinates par1ChunkCoordinates)
    {
        return this.getDistanceSquared(par1ChunkCoordinates.pos);
    }

    public int compareTo(WorldCoordinates par1Obj)
    {
        return this.compareWorldCoordinate(par1Obj);
    }
    
    public void readNBT(CompoundTag nbt) {
    	int x = nbt.getIntOr("w_x", 0);
    	int y = nbt.getIntOr("w_y", 0);
    	int z = nbt.getIntOr("w_z", 0);
    	this.pos = new BlockPos(x,y,z);
    	this.dim = ResourceKey.create(Registries.DIMENSION, Identifier.parse(nbt.getStringOr("w_d", "minecraft:overworld")));
    }
    
    public void writeNBT(CompoundTag nbt) {
    	nbt.putInt("w_x",pos.getX());
    	nbt.putInt("w_y",pos.getY());
    	nbt.putInt("w_z",pos.getZ());
    	if (dim != null) nbt.putString("w_d",dim.identifier().toString());
    }
    
}
