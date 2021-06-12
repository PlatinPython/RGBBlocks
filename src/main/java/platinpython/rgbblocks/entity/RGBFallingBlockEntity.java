package platinpython.rgbblocks.entity;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import platinpython.rgbblocks.util.registries.EntityRegistry;

public class RGBFallingBlockEntity extends FallingBlockEntity implements IEntityAdditionalSpawnData {
	int color;
	private BlockState blockState;
	private boolean cancelDrop;
	private boolean hurtEntities;
	private int fallDamageMax = 40;
	private float fallDamageAmount = 2.0F;

	public RGBFallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world) {
		super(EntityRegistry.RGB_FALLING_BLOCK.get(), world);

	}

	public RGBFallingBlockEntity(World world, double x, double y, double z, BlockState state, BlockPos pos) {
		this(EntityRegistry.RGB_FALLING_BLOCK.get(), world);
		this.blockState = state;
		this.blocksBuilding = true;
		this.setPos(x, y + (double) ((1.0F - this.getBbHeight()) / 2.0F), z);
		this.setDeltaMovement(Vector3d.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setStartPos(this.blockPosition());
		color = world.getBlockEntity(pos).getUpdateTag().getInt("color");
	}
	
	public int getColor() {
		return color;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		if (this.blockState.isAir()) {
			this.remove();
		} else {
			Block block = this.blockState.getBlock();
			if (this.time++ == 0) {
				BlockPos blockpos = this.blockPosition();
				if (this.level.getBlockState(blockpos).is(block)) {
					this.level.removeBlock(blockpos, false);
				} else if (!this.level.isClientSide) {
					this.remove();
					return;
				}
			}

			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
			if (!this.level.isClientSide) {
				BlockPos blockpos1 = this.blockPosition();
				boolean flag = this.blockState.getBlock() instanceof ConcretePowderBlock;
				boolean flag1 = flag && this.level.getFluidState(blockpos1).is(FluidTags.WATER);
				double d0 = this.getDeltaMovement().lengthSqr();
				if (flag && d0 > 1.0D) {
					BlockRayTraceResult blockraytraceresult = this.level
							.clip(new RayTraceContext(new Vector3d(this.xo, this.yo, this.zo), this.position(),
									RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, this));
					if (blockraytraceresult.getType() != RayTraceResult.Type.MISS
							&& this.level.getFluidState(blockraytraceresult.getBlockPos()).is(FluidTags.WATER)) {
						blockpos1 = blockraytraceresult.getBlockPos();
						flag1 = true;
					}
				}

				if (!this.onGround && !flag1) {
					if (!this.level.isClientSide && (this.time > 100 && (blockpos1.getY() < 1 || blockpos1.getY() > 256)
							|| this.time > 600)) {
						if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							this.spawnAtLocation(block);
						}

						this.remove();
					}
				} else {
					BlockState blockstate = this.level.getBlockState(blockpos1);
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
					if (!blockstate.is(Blocks.MOVING_PISTON)) {
						this.remove();
						if (!this.cancelDrop) {
							boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, blockpos1,
									Direction.DOWN, ItemStack.EMPTY, Direction.UP));
							boolean flag3 = FallingBlock.isFree(this.level.getBlockState(blockpos1.below()))
									&& (!flag || !flag1);
							boolean flag4 = this.blockState.canSurvive(this.level, blockpos1) && !flag3;
							if (flag2 && flag4) {
								if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED)
										&& this.level.getFluidState(blockpos1).getType() == Fluids.WATER) {
									this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED,
											Boolean.valueOf(true));
								}

								if (this.level.setBlock(blockpos1, this.blockState, 3)) {
									if (block instanceof FallingBlock) {
										((FallingBlock) block).onLand(this.level, blockpos1, this.blockState,
												blockstate, this);
									}

									if (this.blockData != null && this.blockState.hasTileEntity()) {
										TileEntity tileentity = this.level.getBlockEntity(blockpos1);
										if (tileentity != null) {
											CompoundNBT compoundnbt = tileentity.save(new CompoundNBT());

											for (String s : this.blockData.getAllKeys()) {
												INBT inbt = this.blockData.get(s);
												if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
													compoundnbt.put(s, inbt.copy());
												}
											}

											tileentity.load(this.blockState, compoundnbt);
											tileentity.setChanged();
										}
									}
								} else if (this.dropItem
										&& this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
									ItemStack item = new ItemStack(block);
									item.getOrCreateTag().putInt("color", color);
									this.spawnAtLocation(item);
								}
							} else if (this.dropItem
									&& this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
								ItemStack item = new ItemStack(block);
								item.getOrCreateTag().putInt("color", color);
								this.spawnAtLocation(item);
							}
						} else if (block instanceof FallingBlock) {
							((FallingBlock) block).onBroken(this.level, blockpos1, this);
						}
					}
				}
			}

			this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
		}
	}

	@Override
	public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
		if (this.hurtEntities) {
			int i = MathHelper.ceil(p_225503_1_ - 1.0F);
			if (i > 0) {
				List<Entity> list = Lists.newArrayList(this.level.getEntities(this, this.getBoundingBox()));
				boolean flag = this.blockState.is(BlockTags.ANVIL);
				DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;

				for (Entity entity : list) {
					entity.hurt(damagesource,
							(float) Math.min(MathHelper.floor((float) i * this.fallDamageAmount), this.fallDamageMax));
				}

				if (flag && (double) this.random.nextFloat() < (double) 0.05F + (double) i * 0.05D) {
					BlockState blockstate = AnvilBlock.damage(this.blockState);
					if (blockstate == null) {
						this.cancelDrop = true;
					} else {
						this.blockState = blockstate;
					}
				}
			}
		}

		return false;
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		compound.put("BlockState", NBTUtil.writeBlockState(this.blockState));
		compound.putInt("Time", this.time);
		compound.putBoolean("DropItem", this.dropItem);
		compound.putBoolean("HurtEntities", this.hurtEntities);
		compound.putFloat("FallHurtAmount", this.fallDamageAmount);
		compound.putInt("FallHurtMax", this.fallDamageMax);
		if (this.blockData != null) {
			compound.put("TileEntityData", this.blockData);
		}
		compound.putInt("color", color);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		this.blockState = NBTUtil.readBlockState(compound.getCompound("BlockState"));
		this.time = compound.getInt("Time");
		if (compound.contains("HurtEntities", 99)) {
			this.hurtEntities = compound.getBoolean("HurtEntities");
			this.fallDamageAmount = compound.getFloat("FallHurtAmount");
			this.fallDamageMax = compound.getInt("FallHurtMax");
		} else if (this.blockState.is(BlockTags.ANVIL)) {
			this.hurtEntities = true;
		}

		if (compound.contains("DropItem", 99)) {
			this.dropItem = compound.getBoolean("DropItem");
		}

		if (compound.contains("TileEntityData", 10)) {
			this.blockData = compound.getCompound("TileEntityData");
		}

		if (this.blockState.isAir()) {
			this.blockState = Blocks.SAND.defaultBlockState();
		}
		this.color = compound.getInt("color");
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		try {
			buffer.writeWithCodec(BlockState.CODEC, getBlockState());
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.writeInt(color);
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		try {
			blockState = additionalData.readWithCodec(BlockState.CODEC);
		} catch (IOException e) {
			e.printStackTrace();
		}
		color = additionalData.readInt();
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public BlockState getBlockState() {
		return this.blockState;
	}
}
