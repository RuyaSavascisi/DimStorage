package edivad.dimstorage.client.screen.pattern;

import com.mojang.blaze3d.vertex.PoseStack;
import edivad.dimstorage.blockentities.BlockEntityDimChest;
import edivad.dimstorage.blockentities.BlockEntityDimTank;
import edivad.dimstorage.blockentities.BlockEntityFrequencyOwner;
import edivad.dimstorage.client.screen.element.button.ChangeButton;
import edivad.dimstorage.client.screen.element.button.LockButton;
import edivad.dimstorage.client.screen.element.button.OwnerButton;
import edivad.dimstorage.client.screen.element.textfield.FrequencyText;
import edivad.dimstorage.network.PacketHandler;
import edivad.dimstorage.network.packet.UpdateDimChest;
import edivad.dimstorage.network.packet.UpdateDimTank;
import edivad.dimstorage.tools.Translations;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class FrequencyScreen<T extends AbstractContainerMenu> extends PanelScreen<T> {

    private static final Component OWNER = Component.translatable(Translations.OWNER);
    private static final Component FREQ = Component.translatable(Translations.FREQUENCY);
    private static final Component LOCKED = Component.translatable(Translations.LOCKED);

    protected BlockEntityFrequencyOwner blockEntityFrequencyOwner;
    private FrequencyText freqTextField;

    public FrequencyScreen(T container, BlockEntityFrequencyOwner blockEntityFrequencyOwner, Inventory inventory, Component text, ResourceLocation background, boolean drawSettings) {
        super(container, inventory, text, background, drawSettings);
        this.blockEntityFrequencyOwner = blockEntityFrequencyOwner;
    }

    @Override
    protected void init() {
        super.init();

        clearComponent();
        addComponent(new OwnerButton(width / 2 + 95, height / 2 - 53, blockEntityFrequencyOwner));
        addComponent(new ChangeButton(width / 2 + 95, height / 2 + 7, b -> changeFrequency()));
        addComponent(new LockButton(width / 2 + 95, height / 2 + 46, blockEntityFrequencyOwner));

        freqTextField = new FrequencyText(width / 2 + 95, height / 2 - 12, blockEntityFrequencyOwner.getFrequency());
        addComponent(freqTextField);
        drawSettings(drawSettings);
    }

    private void changeFrequency() {
        int prevChannel = blockEntityFrequencyOwner.getFrequency().getChannel();
        try {
            int newFreq = Math.abs(Integer.parseInt(freqTextField.getValue()));
            blockEntityFrequencyOwner.setFrequency(blockEntityFrequencyOwner.getFrequency().setChannel(newFreq));

            if(blockEntityFrequencyOwner instanceof BlockEntityDimChest chest)
                PacketHandler.INSTANCE.sendToServer(new UpdateDimChest(chest));
            else if(blockEntityFrequencyOwner instanceof BlockEntityDimTank tank)
                PacketHandler.INSTANCE.sendToServer(new UpdateDimTank(tank));
        }
        catch(Exception e) {
            freqTextField.setValue(String.valueOf(prevChannel));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        freqTextField.tick();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        freqTextField.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        freqTextField.mouseClicked(mouseX, mouseY, clickedButton);
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        if(drawSettings) {
            int posY = 45;

            // owner
            this.font.draw(poseStack, OWNER, 185, posY, 4210752);
            posY += 9;
            this.hLine(poseStack, 185, 185 + this.font.width(OWNER), posY, 0xFF333333);
            posY += 31;

            // freq
            this.font.draw(poseStack, FREQ, 185, posY, 4210752);
            posY += 9;
            this.hLine(poseStack, 185, 185 + this.font.width(FREQ), posY, 0xFF333333);
            posY += 50;

            // locked
            this.font.draw(poseStack, LOCKED, 185, posY, 4210752);
            posY += 9;
            this.hLine(poseStack, 185, 185 + this.font.width(LOCKED), posY, 0xFF333333);
        }
    }
}
