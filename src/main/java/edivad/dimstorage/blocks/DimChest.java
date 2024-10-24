package edivad.dimstorage.blocks;

import org.jetbrains.annotations.Nullable;
import edivad.dimstorage.blockentities.BlockEntityDimChest;
import edivad.dimstorage.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class DimChest extends DimBlockBase {

  public DimChest() {
    super(Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
        .requiresCorrectToolForDrops().strength(3.5F).noOcclusion());
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new BlockEntityDimChest(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
      BlockEntityType<T> blockEntityType) {
    return createDimBlockTicker(level, blockEntityType, Registration.DIMCHEST_TILE.get());
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
      BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    if (player instanceof ServerPlayer serverPlayer) {
      if (level.getBlockEntity(pos) instanceof BlockEntityDimChest chest) {
        if (!player.isCrouching()) {
          return chest.useItemOn(serverPlayer, level, pos, hand);
        }
      }
    }
    return ItemInteractionResult.sidedSuccess(level.isClientSide());
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer,
      ItemStack stack) {
    level.getBlockEntity(pos, Registration.DIMCHEST_TILE.get())
        .ifPresent(chest -> chest.onPlaced(placer));
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    return level.getBlockEntity(pos, Registration.DIMCHEST_TILE.get())
        .map(BlockEntityDimChest::getComparatorInput).orElse(0);
  }

  @Override
  public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventID,
      int eventParam) {
    var blockentity = level.getBlockEntity(pos);
    return blockentity != null && blockentity.triggerEvent(eventID, eventParam);
  }
}
