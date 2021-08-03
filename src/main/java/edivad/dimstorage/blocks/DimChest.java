package edivad.dimstorage.blocks;

import edivad.dimstorage.setup.Registration;
import edivad.dimstorage.tile.TileEntityDimChest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class DimChest extends DimBlockBase {

    public DimChest() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.5F).noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileEntityDimChest(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createDimBlockTicker(level, blockEntityType, Registration.DIMCHEST_TILE.get());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(level.isClientSide)
            return InteractionResult.SUCCESS;

        BlockEntity tile = level.getBlockEntity(pos);

        if(tile instanceof TileEntityDimChest chest) {
            if(!player.isCrouching())
                return chest.activate(player, level, pos, handIn);
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tile = level.getBlockEntity(pos);
        if(tile instanceof TileEntityDimChest chest) {
            chest.onPlaced(placer);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        return (te instanceof TileEntityDimChest chest) ? chest.getComparatorInput() : 0;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventID, int eventParam) {
        BlockEntity tile = level.getBlockEntity(pos);
        return tile != null && tile.triggerEvent(eventID, eventParam);
    }
}
