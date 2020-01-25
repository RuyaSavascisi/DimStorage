package edivad.dimstorage.items.dimpad;

import edivad.dimstorage.Main;
import edivad.dimstorage.client.screen.BaseScreen;
import edivad.dimstorage.tools.Translate;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenDimPad extends BaseScreen<ContainerDimPad>{

	private String name, inventory;
	public ScreenDimPad(ContainerDimPad container, PlayerInventory invPlayer, ITextComponent text)
	{
		super(container, invPlayer, text, new ResourceLocation(Main.MODID, "textures/gui/dimchest.png"));
		this.xSize = 176;//176
		this.ySize = 222;//222
	}
	
	@Override
	protected void init()
	{
		super.init();

		// Get translation
		inventory = Translate.translateToLocal("container.inventory");
		name = Translate.translateToLocal("item." + Main.MODID + ".dim_pad");
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		super.drawGuiContainerBackgroundLayer(f, i, j);
		this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.font.drawString(name, 8, 6, 4210752);
		this.font.drawString(inventory, 8, 128, 4210752);
	}
}
