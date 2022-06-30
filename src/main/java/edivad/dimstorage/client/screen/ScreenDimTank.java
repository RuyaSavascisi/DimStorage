package edivad.dimstorage.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import edivad.dimstorage.Main;
import edivad.dimstorage.blockentities.BlockEntityDimTank;
import edivad.dimstorage.client.screen.element.button.AutoEjectButton;
import edivad.dimstorage.client.screen.pattern.FrequencyScreen;
import edivad.dimstorage.container.ContainerDimTank;
import edivad.dimstorage.storage.DimTankStorage;
import edivad.dimstorage.tools.Translations;
import edivad.edivadlib.tools.utils.FluidUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

public class ScreenDimTank extends FrequencyScreen<ContainerDimTank> {

    private static final ResourceLocation DIMTANK_GUI = new ResourceLocation(Main.MODID, "textures/gui/dimtank.png");
    private static final MutableComponent LIQUID = Component.translatable(Translations.LIQUID);
    private static final MutableComponent AMOUNT = Component.translatable(Translations.AMOUNT);
    private static final MutableComponent TEMPERATURE = Component.translatable(Translations.TEMPERATURE);
    private static final MutableComponent LUMINOSITY = Component.translatable(Translations.LUMINOSITY);
    private static final MutableComponent GASEOUS = Component.translatable(Translations.GAS);
    private static final MutableComponent EMPTY = Component.translatable(Translations.EMPTY);
    private static final MutableComponent YES = Component.translatable(Translations.YES);
    private static final MutableComponent NO = Component.translatable(Translations.NO);

    public ScreenDimTank(ContainerDimTank container, Inventory inventory, Component text) {
        super(container, container.owner, inventory, text, DIMTANK_GUI, container.isOpen);
    }

    @Override
    protected void init() {
        super.init();

        addComponent(new AutoEjectButton(width / 2 + 95, height / 2 + 75, (BlockEntityDimTank) blockEntityFrequencyOwner));

        drawSettings(drawSettings);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        FluidStack liquidStack = ((BlockEntityDimTank) blockEntityFrequencyOwner).liquidState.clientLiquid;

        if(!liquidStack.isEmpty()) {
            FluidType fluidType = liquidStack.getFluid().getFluidType();
            String liquidName = liquidStack.getDisplayName().getString();
            this.font.draw(poseStack, LIQUID.copy().append(" " + liquidName.substring(0, Math.min(14, liquidName.length()))), 50, 25, 4210752);
            this.font.draw(poseStack, AMOUNT.copy().append(" " + liquidStack.getAmount() + " mB"), 50, 35, 4210752);
            this.font.draw(poseStack, TEMPERATURE.copy().append(" " + (fluidType.getTemperature() - 273) + "C"), 50, 45, 4210752);
            this.font.draw(poseStack, LUMINOSITY.copy().append(" " + fluidType.getLightLevel()), 50, 55, 4210752);
            this.font.draw(poseStack, GASEOUS.copy().append(" " + (fluidType.isLighterThanAir() ? YES : NO)), 50, 65, 4210752);
        }
        else {
            this.font.draw(poseStack, LIQUID.copy().append(" ").append(EMPTY), 50, 25, 4210752);
        }

    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);

        FluidStack fluid = ((BlockEntityDimTank) blockEntityFrequencyOwner).liquidState.clientLiquid;
        int z = getFluidScaled(60, fluid.getAmount());
        TextureAtlasSprite fluidTexture = FluidUtils.getFluidTexture(fluid);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        FluidUtils.color(FluidUtils.getLiquidColorWithBiome(fluid, blockEntityFrequencyOwner));
        ScreenDimTank.blit(poseStack, this.leftPos + 11, this.topPos + 21 + z, 176, 16, 60 - z, fluidTexture);
    }

    private static int getFluidScaled(int pixels, int currentLiquidAmount) {
        int maxLiquidAmount = DimTankStorage.CAPACITY;
        int x = currentLiquidAmount * pixels / maxLiquidAmount;
        return pixels - x;
    }
}
