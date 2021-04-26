package ru.wirelesstools.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ru.wirelesstools.tiles.TileEntityWirelessCharger;

public class ContainerWCharger extends Container {

	protected TileEntityWirelessCharger tile;

	public ContainerWCharger(InventoryPlayer inventoryplayer, TileEntityWirelessCharger tile) {
		this.tile = tile;

		for (int i = 0; i < 3; i++) {
			for (int m = 0; m < 9; m++) {

				this.addSlotToContainer(new Slot((IInventory) inventoryplayer, m + i * 9 + 9, 8 + m * 18, 79 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++) {

			this.addSlotToContainer(new Slot((IInventory) inventoryplayer, j, 8 + j * 18, 137));
		}
	}

	public void onCraftGuiOpened(ICrafting icrafting) {
		super.addCraftingToCrafters(icrafting);
		icrafting.sendProgressBarUpdate(this, 1, (int) this.tile.energy & 0xFFFF);
		icrafting.sendProgressBarUpdate(this, 2, (int) this.tile.energy >>> 16);
		icrafting.sendProgressBarUpdate(this, 3, this.tile.playercount);
	}

	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < this.crafters.size(); i++) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);
			icrafting.sendProgressBarUpdate(this, 1, (int) this.tile.energy & 0xFFFF);
			icrafting.sendProgressBarUpdate(this, 2, (int) this.tile.energy >>> 16);
			icrafting.sendProgressBarUpdate(this, 3, this.tile.playercount);
		}
	}

	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		int slot = par2;
		ItemStack stack = null;
		Slot slotObject = (Slot) this.inventorySlots.get(par2);

		if (slotObject != null && slotObject.getHasStack()) {

			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			if (stackInSlot.stackSize == 0) {

				slotObject.putStack(null);
			} else {

				slotObject.onSlotChanged();
			}

			if (stack.stackSize != stackInSlot.stackSize) {

				slotObject.onPickupFromSlot(par1EntityPlayer, stackInSlot);
			} else {

				return null;
			}
		}

		return stack;
	}

	public void updateProgressBar(int id, int j) {

		if (id == 1) {

			this.tile.energy = (int) this.tile.energy & 0xFFFF0000 | j;
		}

		if (id == 2) {

			this.tile.energy = (int) this.tile.energy & 0xFFFF | j << 16;
		}

		if (id == 3) {

			this.tile.playercount = j;
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return this.tile.isUseableByPlayer(player);
	}

}
