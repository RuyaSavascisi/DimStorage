package edivad.dimstorage.blocks;

import javax.annotation.Nullable;

import edivad.dimstorage.compat.top.FluidElement;
import edivad.dimstorage.compat.top.TOPInfoProvider;
import edivad.dimstorage.storage.DimTankStorage;
import edivad.dimstorage.tile.TileEntityDimTank;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DimTank extends DimBlockBase implements TOPInfoProvider, IWaterLoggable {

	private static final VoxelShape BOX = VoxelShapes.create(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 16 / 16D, 14 / 16D);
	private static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");

	public DimTank()
	{
		super(Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(5.0F).notSolid());
		this.setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new TileEntityDimTank();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		if(worldIn.isRemote)
			return ActionResultType.SUCCESS;

		TileEntity tile = worldIn.getTileEntity(pos);
		if(!(tile instanceof TileEntityDimTank) || !(tile instanceof INamedContainerProvider))
			return ActionResultType.FAIL;

		TileEntityDimTank owner = (TileEntityDimTank) tile;

		return owner.activate(player, worldIn, pos, handIn);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return BOX;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return BOX;
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityDimTank)
		{
			return ((TileEntityDimTank) tile).getLightValue();
		}
		return 0;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		return (te instanceof TileEntityDimTank) ? ((TileEntityDimTank) te).getComparatorInput() : 0;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data)
	{
		TileEntity te = world.getTileEntity(data.getPos());
		if(te instanceof TileEntityDimTank)
		{
			TileEntityDimTank tank = (TileEntityDimTank) te;

			if(tank.frequency.hasOwner())
			{
				if(tank.canAccess(player))
					probeInfo.horizontal().text(TextFormatting.GREEN + "Owner: " + tank.frequency.getOwner());
				else
					probeInfo.horizontal().text(TextFormatting.RED + "Owner: " + tank.frequency.getOwner());
			}
			probeInfo.horizontal().text("Frequency: " + tank.frequency.getChannel());
			if(tank.locked)
				probeInfo.horizontal().text("Locked: Yes");
			if(tank.autoEject)
				probeInfo.horizontal().text("Auto-eject: Yes");

			if(!tank.liquidState.serverLiquid.isEmpty())
				probeInfo.element(new FluidElement(tank.liquidState.serverLiquid, DimTankStorage.CAPACITY));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public IFluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn)
	{
		return IWaterLoggable.super.receiveFluid(worldIn, pos, state, fluidStateIn);
	}

	@Override
	public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
	{
		return IWaterLoggable.super.canContainFluid(worldIn, pos, state, fluidIn);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		super.fillStateContainer(builder);
		builder.add(WATERLOGGED);
	}
}
