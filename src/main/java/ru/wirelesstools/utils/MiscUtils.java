package ru.wirelesstools.utils;

import cofh.api.energy.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.config.ConfigWI;

public class MiscUtils {

	public static void chargeRFItemFromArmor(ItemStack currentarmorstack, ItemStack rfitemstack) {
		IEnergyContainerItem item = (IEnergyContainerItem) rfitemstack.getItem();
		int amountRfCanBeReceivedIncludesLimit = item.receiveEnergy(rfitemstack, Integer.MAX_VALUE, true);
		double helmetChargeRF = ElectricItem.manager.getCharge(currentarmorstack) * ConfigWI.EuToRfmultiplier;

		double realSentEnergyRF = Math.min(amountRfCanBeReceivedIncludesLimit, helmetChargeRF);
		double realDischargedEUFromHelmet = realSentEnergyRF / ConfigWI.EuToRfmultiplier;

		item.receiveEnergy(rfitemstack, (int) realSentEnergyRF, false);
		ElectricItem.manager.discharge(currentarmorstack, realDischargedEUFromHelmet, Integer.MAX_VALUE, true, false,
				false);
	}
}
