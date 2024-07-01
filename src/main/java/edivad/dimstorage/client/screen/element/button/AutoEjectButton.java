package edivad.dimstorage.client.screen.element.button;

import edivad.dimstorage.blockentities.BlockEntityDimTank;
import edivad.dimstorage.network.to_server.UpdateDimTank;
import edivad.dimstorage.tools.Translations;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class AutoEjectButton extends AbstractButton {

  private final BlockEntityDimTank tank;

  public AutoEjectButton(int x, int y, BlockEntityDimTank tank) {
    super(x, y, 64, 20, getText(tank.autoEject));
    this.tank = tank;
  }

  private static Component getText(boolean autoEject) {
    return Component.translatable(autoEject ? Translations.EJECT : Translations.IDLE);
  }

  @Override
  public void onPress() {
    tank.swapAutoEject();
    PacketDistributor.sendToServer(new UpdateDimTank(tank));
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    defaultButtonNarrationText(narrationElementOutput);
  }
}
