package platinpython.rgbblocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.registries.TileEntityRegistry;

public class RGBFlatSlabBlock extends SlabBlock {
	public RGBFlatSlabBlock() {
		super(Block.Properties.create(new Material.Builder(MaterialColor.AIR).build()));
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityRegistry.RGB.get().create();
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(stack.hasTag() == true) {
			RGBTileEntity tileEntity = (RGBTileEntity) worldIn.getTileEntity(pos);
			tileEntity.red = stack.getTag().getInt("red");
			tileEntity.green = stack.getTag().getInt("green");
			tileEntity.blue = stack.getTag().getInt("blue");
		}
	}
	
	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		ItemStack stack = new ItemStack(this.asItem());
		RGBTileEntity tileEntity = (RGBTileEntity) world.getTileEntity(pos);
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("red", tileEntity.red);
		tag.putInt("green", tileEntity.green);
		tag.putInt("blue", tileEntity.blue);
		stack.setTag(tag);
		return stack;
	}
}
