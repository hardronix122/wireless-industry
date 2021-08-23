package ru.wirelesstools.gui;

import com.mojang.authlib.GameProfile;
import ic2.core.IC2;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerWPPersonal;
import ru.wirelesstools.tiles.TileWPBasePersonal;
import ru.wirelesstools.utils.UtilFormatNumber;

public class GuiWPPersonal extends GuiContainer {

    private static final ResourceLocation tex = new ResourceLocation(Reference.IDNAME, "textures/gui/wirelesssolarpanel.png");

    private GuiTextField textField;
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
        if(this.tileentity.storage > 0) {
            int l = this.tileentity.gaugeEnergyScaled(47);
            this.drawTexturedModalRect(h + 19, k + 24, 195, 0, l + 1, 14);
        }

        if(this.tileentity.skyIsVisible) {
            if(this.tileentity.sunIsUp) {
                this.drawTexturedModalRect(h + 24, k + 42, 195, 15, 14, 14);
            } else {
                this.drawTexturedModalRect(h + 24, k + 42, 210, 15, 14, 14);
            }
        }
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.owner = this.tileentity.getOwner();
        String formatPanelName = I18n.format(this.tileentity.panelName);
        int nmPos = (this.xSize - this.fontRendererObj.getStringWidth(formatPanelName)) / 2;
        this.fontRendererObj.drawString(formatPanelName, nmPos, 7, 7718655);
        String storageString = I18n.format("gui.WP.storage") + ": ";
        String maxOutputString = I18n.format("gui.WP.maxOutput") + ": ";
        String generatingString = I18n.format("gui.WP.generating") + ": ";
        String energyPerTickString = I18n.format("gui.WP.energyPerTick");
        String channelString = I18n.format("gui.WPPersonal.channel") + ": ";
        String owner1 = I18n.format("gui.WPP.owner") + ": ";
        this.fontRendererObj.drawString(
                storageString + UtilFormatNumber.formatNumberPanel(this.tileentity.storage) + " / "
                        + UtilFormatNumber.formatNumberPanel(this.tileentity.maxStorage), 77, 22, 13487565);
        this.fontRendererObj.drawString(maxOutputString + UtilFormatNumber.formatNumberPanel(this.tileentity.production) + " " + energyPerTickString, 77,
                32, 13487565);
        this.fontRendererObj.drawString(generatingString + UtilFormatNumber.formatNumberPanel(this.tileentity.generating) + " " + energyPerTickString, 77,
                42, 13487565);
        if(this.owner != null)
            this.fontRendererObj.drawString(owner1 + this.owner.getName(), 77, 52, 13487565);

        this.fontRendererObj.drawString(channelString + this.tileentity.getChannel(), 77, 62, 13487565);

    }

    /*public void initGui() {
        super.initGui();
        int guiX = (this.width - this.xSize) / 2;
        int guiY = (this.height - this.ySize) / 2;

        this.textField = new GuiTextField(this.fontRendererObj, guiX + 62, guiY + 24, 103, 12);
        this.textField.setTextColor(-1);
        this.textField.setDisabledTextColour(-1);
        this.textField.setEnableBackgroundDrawing(false);
        this.textField.setMaxStringLength(40);
        // 1 �������� - ID ������,
        // 2 �������� - � X �������,
        // 3 �������� - � Y �������,
        // 4 �������� - ������,
        // 5 �������� - ������,
        // 6 �������� - �����.
        this.buttonList.add(new GuiButton(2, guiX + 18, guiY + 62, 20, 20,
                I18n.format("button.increment.channel")));
        this.buttonList.add(new GuiButton(3, guiX + 45, guiY + 62, 20, 20,
                I18n.format("button.decrement.channel")));
    }*/

    /*public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        this.textField.drawTextBox();
    }*/

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;

        if(x >= 26 && x <= 36 && y >= 63 && y <= 73) {
            IC2.network.get().initiateClientTileEntityEvent(this.tileentity, 0);
        }

        if(x >= 42 && x <= 52 && y >= 63 && y <= 73) {
            IC2.network.get().initiateClientTileEntityEvent(this.tileentity, 1);
        }
    }

    /*protected void actionPerformed(GuiButton button) {
        try {
            if(button.id == 2) {
                IC2.network.get().initiateClientTileEntityEvent(this.tileentity, 0);
            }

            if(button.id == 3) {
                IC2.network.get().initiateClientTileEntityEvent(this.tileentity, 1);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        super.actionPerformed(button);
    }*/

}
