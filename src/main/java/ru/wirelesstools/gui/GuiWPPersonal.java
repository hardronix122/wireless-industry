package ru.wirelesstools.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerWPPersonal;
import ru.wirelesstools.packets.PacketGuiPressButton;
import ru.wirelesstools.tiles.TileWPBasePersonal;

public class GuiWPPersonal extends GuiContainer {

	private static ResourceLocation tex = new ResourceLocation(Reference.NAME, "textures/gui/wirelesssolarpanel.png");

	private TileWPBasePersonal tileentity;

	private GameProfile owner;

	public GuiWPPersonal(InventoryPlayer inventoryplayer, TileWPBasePersonal tileentitysolarpanel) {
		super(new ContainerWPPersonal(inventoryplayer, tileentitysolarpanel));
		this.tileentity = tileentitysolarpanel;
		this.xSize = 194;
		this.ySize = 168;
		this.allowUserInput = false;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(tex);
		int h = (this.width - this.xSize) / 2; // = 0 ?
		int k = (this.height - this.ySize) / 2; // = 0 ?

		// ����� 6 ����������.
		// ������ ��� - ��� ���������� ������(x, y),
		// ������ ��� - ��� ������������� ��� ������ (x, y),
		// ��������� ��� - ������ � ������ ������� (x ��������, y ��������).
		this.drawTexturedModalRect(h, k, 0, 0, this.xSize, this.ySize);
		if (this.tileentity.storage > 0) {
			int l = this.tileentity.gaugeEnergyScaled(47);
			this.drawTexturedModalRect(h + 19, k + 24, 195, 0, l + 1, 14);
		}

		if (this.tileentity.skyIsVisible) {
			if (this.tileentity.sunIsUp) {

				this.drawTexturedModalRect(h + 24, k + 42, 195, 15, 14, 14);
			} else if (!this.tileentity.sunIsUp) {

				this.drawTexturedModalRect(h + 24, k + 42, 210, 15, 14, 14);
			}

		}

	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {

		this.owner = this.tileentity.getOwner();

		String formatPanelName = I18n.format(this.tileentity.panelName, new Object[0]);
		int nmPos = (this.xSize - this.fontRendererObj.getStringWidth(formatPanelName)) / 2;
		this.fontRendererObj.drawString(formatPanelName, nmPos, 7, 7718655);
		String storageString = I18n.format("gui.WP.storage", new Object[0]) + ": ";
		String maxOutputString = I18n.format("gui.WP.maxOutput", new Object[0]) + ": ";
		String generatingString = I18n.format("gui.WP.generating", new Object[0]) + ": ";
		String energyPerTickString = I18n.format("gui.WP.energyPerTick", new Object[0]);
		String channelString = I18n.format("gui.WPPersonal.channel", new Object[0]) + ": ";
		String owner1 = I18n.format("gui.WPP.owner", new Object[0]) + ": ";

		this.fontRendererObj.drawString(
				storageString + (int) this.tileentity.storage + "/" + this.tileentity.maxStorage, 77, 22, 13487565);
		this.fontRendererObj.drawString(maxOutputString + this.tileentity.production + " " + energyPerTickString, 77,
				32, 13487565);
		this.fontRendererObj.drawString(generatingString + this.tileentity.generating + " " + energyPerTickString, 77,
				42, 13487565);
		if (this.owner != null) {

			this.fontRendererObj.drawString(owner1 + this.owner.getName(), 77, 52, 13487565);
		}

		this.fontRendererObj.drawString(channelString + this.tileentity.getChannel(), 77, 62, 13487565);

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
		this.buttonList.add(new GuiButton(2, xGuiPos + 18, yGuiPos + 62, 22, 12,
				I18n.format("button.increment.channel", new Object[0])));
		this.buttonList.add(new GuiButton(3, xGuiPos + 45, yGuiPos + 62, 22, 12,
				I18n.format("button.decrement.channel", new Object[0])));
	}

	protected void actionPerformed(GuiButton button) {

		try {

			if (button.id == 2) {

				PacketGuiPressButton.issue(this.tileentity, 2);
			}

			if (button.id == 3) {

				PacketGuiPressButton.issue(this.tileentity, 3);
			}

		}

		catch (Exception e) {

			e.printStackTrace();
		}

		super.actionPerformed(button);
	}

}
