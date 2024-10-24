package edivad.dimstorage.items;

import java.util.List;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.blockentities.BlockEntityFrequencyOwner;
import edivad.dimstorage.items.components.DimStorageComponents;
import edivad.dimstorage.tools.Translations;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemDimBase extends BlockItem {

  public ItemDimBase(Block block, Properties properties) {
    super(block, properties);
  }

  private Frequency getFreq(ItemStack stack) {
    return stack.getOrDefault(DimStorageComponents.FREQUENCY, new Frequency());
  }

  @Override
  protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
    if (super.placeBlock(context, state)) {
      var level = context.getLevel();
      var pos = context.getClickedPos();
      var stack = context.getItemInHand();
      if (level.getBlockEntity(pos) instanceof BlockEntityFrequencyOwner b) {
        b.setFrequency(getFreq(stack));
        return true;
      }
    }
    return false;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context,
      List<Component> tooltip, TooltipFlag tooltipFlag) {
    var frequency = getFreq(stack);
    if (frequency.hasOwner()) {
      tooltip.add(Component.translatable(Translations.OWNER).append(" " + frequency.getOwner())
          .withStyle(ChatFormatting.DARK_RED));
    }
    if (stack.has(DimStorageComponents.FREQUENCY)) {
      tooltip.add(Component.translatable(Translations.FREQUENCY)
          .append(" " + frequency.channel()));
    }
  }
}
