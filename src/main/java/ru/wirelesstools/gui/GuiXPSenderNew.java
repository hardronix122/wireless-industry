package ru.wirelesstools.gui;

import org.lwjgl.opengl.GL11;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerXPSenderNew;
import ru.wirelesstools.utils.UtilFormatGUI;

public class GuiXPSenderNew extends GuiIC2 {

	private ContainerXPSenderNew container;

	public GuiXPSenderNew(ContainerXPSenderNew container) {
		super(container, 175, 160);
		this.container = container;
	}

	@Override
	public String getName() {

		return StatCollector.translateToLocal(this.container.base.xpsendername);
	}

	@Override
	public ResourceLocation getResourceLocation() {

		return new ResourceLocation(Reference.NAME, "textures/gui/GuiXPSender.png");
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String tileentityname = I18n.format(this.container.base.xpsendername, new Object[0]);
		String storageString = I18n.format("gui.wirind.xpsenderstorage", new Object[0]) + ": ";
		String energyformatted = UtilFormatGUI.formatNumber(this.container.base.energy);
		String maxstorageformatted = UtilFormatGUI.formatNumber((double) this.container.base.maxStorage);
		String totalspentString = I18n.format("gui.wirind.xpsendertotalspent", new Object[0]) + ": ";
		String spentPerPlayerString = I18n.format("gui.wirind.per.player", new Object[0]);
		String radiusString = I18n.format("gui.wirind.radius.xp.send", new Object[0]) + ": ";
		
		String stringEnergyAll = storageString + energyformatted + " / " + maxstorageformatted + " EU";

		String totalspentamount = String.valueOf(this.container.base.getTotalSpentEUValue());

		String totalSpentAll = totalspentString + totalspentamount + " EU " + spentPerPlayerString;
		
		String playercountStringAll = I18n.format("gui.wirind.xpsenderplayercount", new Object[0]) + ": "
				+ this.container.base.getPlayerCount();
		String radiusAll = radiusString + this.container.base.getSendRadius();

		int nmPos1 = (this.xSize - this.fontRendererObj.getStringWidth(tileentityname)) / 2;
		int nmPos2 = (this.xSize - this.fontRendererObj.getStringWidth(stringEnergyAll)) / 2;
		int nmPos3 = (this.xSize - this.fontRendererObj.getStringWidth(totalSpentAll)) / 2;
		int nmPos4 = (this.xSize - this.fontRendererObj.getStringWidth(playercountStringAll)) / 2;
		int nmPos5 = (this.xSize - this.fontRendererObj.getStringWidth(radiusAll)) / 2;

		this.fontRendererObj.drawString(tileentityname, nmPos1, 5, 4210752);
		this.fontRendererObj.drawString(stringEnergyAll, nmPos2, 17, 4210752);
		
		this.fontRendererObj.drawString(totalSpentAll, nmPos3, 29, 4210752);
		this.fontRendererObj.drawString(playercountStringAll, nmPos4, 65, 4210752);
		this.fontRendererObj.drawString(radiusAll, nmPos5, 52, 4210752);

	}

	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(this.getResourceLocation());
		this.xoffset = (this.width - this.xSize) / 2;
		this.yoffset = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(this.xoffset, this.yoffset, 0, 0, this.xSize, this.ySize);

		if (this.container.base.energy > 0) {
			int l = this.container.base.gaugeEnergyScaled(40);
			this.drawTexturedModalRect(this.xoffset + 65, this.yoffset + 39, 177, 15, l + 1, 10);
		}
	}
	
	public void initGui() {
		super.initGui();
		int xGuiPos = (this.width - this.xSize) / 2;
		int yGuiPos = (this.height - this.ySize) / 2;
		// 1 �������� - ID ������,
		// 2 �������� - � X �������,
		// 3 �������� - � Y �������,
		// 4 �������� - ������,
		// 5 �������� - ������,
		// 6 �������� - �����.
		this.buttonList.add(new GuiButton(0, xGuiPos + 140, yGuiPos + 50, 22, 12,
				I18n.format("button.xpsender.increment", new Object[0])));
		this.buttonList.add(new GuiButton(1, xGuiPos + 18, yGuiPos + 50, 22, 12,
				I18n.format("button.xpsender.decrement", new Object[0])));
	}
	
	protected void actionPerformed(GuiButton guibutton) {
		super.actionPerformed(guibutton);
		if (guibutton.id == 0) {
			
			IC2.network.get().initiateClientTileEntityEvent(this.container.base, 0);
		}
		
		if (guibutton.id == 1) {
			
			IC2.network.get().initiateClientTileEntityEvent(this.container.base, 1);
		}
	}

}