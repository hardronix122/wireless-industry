package ru.wirelesstools.tiles;

import com.mojang.authlib.GameProfile;

public interface IWirelessCharger {
	
	/** ��� �����, ������� ��������� ���-�� ������� � ��������� */
	public void decreaseEnergy(double amount);
	
	public double getMaxStorageOfCharger();
	
	public double getCurrentEnergyInCharger();
	
	public GameProfile getOwnerCharger();
	
	public int getXCoord();
	
	public int getYCoord();
	
	public int getZCoord();

}
