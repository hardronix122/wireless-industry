package ru.wirelesstools.item.armor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.core.IC2;
import ic2.core.IC2Potion;
import ic2.core.Ic2Items;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.utils.MiscUtils;

public class ItemSolarWirelessEURFHelmet extends ItemArmor implements IElectricItem, IMetalArmor, ISpecialArmor {

	public double maxenergyEU;
	protected double transferLimit;
	protected int tier;

//	private boolean sunIsUp;
//	private boolean skyIsVisible;
//	private boolean noSunWorld;
//	private boolean wetBiome;

//	private int generating;
	private final int genDay;
	private final int genNight;

	protected static final Map<Integer, Integer> potionRemovalCost = new HashMap<Integer, Integer>();

	public ItemSolarWirelessEURFHelmet(String name) {
		super(ArmorMaterial.DIAMOND, 0, 0);
		this.setUnlocalizedName(name);
		this.setMaxStackSize(1);
		this.maxenergyEU = 250000000.0D;
		this.transferLimit = 250000.0D;
		this.tier = 4;
		this.genDay = ConfigWI.EuRfSolarHelmetGenDay;
		this.genNight = ConfigWI.EuRfSolarHelmetGenNight;
		this.setMaxDamage(27);
		this.setCreativeTab(MainWI.tabwi);
		potionRemovalCost.put(Potion.poison.id, 100);
		potionRemovalCost.put(Potion.wither.id, 100);
		potionRemovalCost.put(Potion.weakness.id, 100);
		potionRemovalCost.put(Potion.hunger.id, 200);
		potionRemovalCost.put(Potion.confusion.id, 200);
		potionRemovalCost.put(Potion.blindness.id, 200);
		potionRemovalCost.put(IC2Potion.radiation.id, 200);
	}

	public ItemSolarWirelessEURFHelmet() {

		this("wirelessuniversalsolarhelmet");
	}

	public EnumRarity getRarity(ItemStack itemstack) {

		return MainWI.RARITY_RF;
	}

	@SideOnly(value = Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
		String isonoff = nbt.getBoolean("active") ? "info.helmet.yes" : "info.helmet.no";
		int nmode = nbt.getInteger("NightVisMode");
		String strmode = "";
		switch (nmode) {
		case 0:
			strmode = "info.helmet.nightmode.off";
			break;
		case 1:
			strmode = "info.helmet.nightmode.auto";
			break;
		case 2:
			strmode = "info.helmet.nightmode.on";
			break;
		}
		list.add(StatCollector.translateToLocal("info.wirelesshelmet.about"));
		list.add(StatCollector.translateToLocal("info.wirelesshelmet.removespotions"));
		list.add(StatCollector.translateToLocal("info.wirelesshelmet.selfcharge"));
		if (org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_LSHIFT)) {
			list.add(StatCollector.translateToLocal("info.wirelesshelmet.mode") + ": "
					+ StatCollector.translateToLocal(isonoff));
			list.add(StatCollector.translateToLocal("info.wirelesshelmet.nightmode") + ": "
					+ StatCollector.translateToLocal(strmode));
			list.add(StatCollector.translateToLocal("info.wirelesshelmet.radius") + ": " + String.valueOf(15) + " "
					+ StatCollector.translateToLocal("info.wirelesscharge.blocks"));
			list.add(StatCollector.translateToLocal("info.helmet.sneak.press.mode.key") + " " + "IC2 Mode Switch Key"
					+ " " + StatCollector.translateToLocal("info.helmet.to.switch"));
			list.add(StatCollector.translateToLocal("info.helmet.sneak.press.altmode.key") + " " + "IC2 Alt Key" + " + "
					+ "Mode Switch Key" + " " + StatCollector.translateToLocal("info.helmet.to.switch.nv"));
		} else {
			list.add(StatCollector.translateToLocal("info.wirelesshelmet.press.lshift"));
		}

	}

	@SideOnly(value = Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		ItemStack stack = new ItemStack(this, 1);
		ElectricItem.manager.charge(stack, 2.147483647E9, Integer.MAX_VALUE, true, false);
		list.add(stack);
		ItemStack stack1 = new ItemStack(this, 1);
		ElectricItem.manager.charge(stack1, 0.0D, Integer.MAX_VALUE, true, false);
		list.add(stack1);
	}

	@SideOnly(value = Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(Reference.PathTex + "WirelessSolarHelmet");
	}

	@SideOnly(value = Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return Reference.PathTex + "textures/armor/wirelessEuRfSolarHelmet.png";
	}

	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
		boolean active = nbt.getBoolean("active");
		byte toggleTimer = nbt.getByte("toggleTimer");
		int nightvisionmode = nbt.getInteger("NightVisMode");

		if (!world.isRemote) {
			this.gainFuel(player, world, stack);

			for (Object effect : new LinkedList(player.getActivePotionEffects())) {
				int id = ((PotionEffect) effect).getPotionID();
				Integer cost = potionRemovalCost.get(id);
				if (cost == null || !ElectricItem.manager.canUse(stack,
						(double) (cost = Integer.valueOf(cost * (((PotionEffect) effect).getAmplifier() + 1)))
								.intValue()))
					continue;
				ElectricItem.manager.use(stack, (double) cost.intValue(), player);
				IC2.platform.removePotion(player, id);
			}

			if (ElectricItem.manager.canUse(stack, 1000.0) && player.getFoodStats().needFood()) {
				int slot = -1;
				for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
					if (player.inventory.mainInventory[i] == null
							|| !(player.inventory.mainInventory[i].getItem() instanceof ItemFood))
						continue;
					slot = i;
					break;
				}

				if (slot > -1) {
					ItemStack stack1 = player.inventory.mainInventory[slot];
					ItemFood can = (ItemFood) stack1.getItem();
					stack1 = can.onEaten(stack1, world, player);
					if (stack1.stackSize <= 0) {
						player.inventory.mainInventory[slot] = null;
					}
					ElectricItem.manager.use(stack, 1000.0, null);
					player.inventoryContainer.detectAndSendChanges();
				}
			} else if (player.getFoodStats().getFoodLevel() <= 0) {

				IC2.achievements.issueAchievement(player, "starveWithQHelmet");
			}

			int airLevel = player.getAir();
			if (ElectricItem.manager.canUse(stack, 1000.0) && airLevel < 100) {
				player.setAir(airLevel + 200);
				ElectricItem.manager.use(stack, 1000.0, player);
				player.inventoryContainer.detectAndSendChanges();
			}

			ElectricItem.manager.charge(stack, nbt.getInteger("generating"), Integer.MAX_VALUE, false, false);

			for (ItemStack st2 : player.inventory.mainInventory) {
				if (st2 == null)
					continue;
				if (st2.getItem() instanceof IEnergyContainerItem) {
					if (ElectricItem.manager.getCharge(stack) > 0.0) {

						MiscUtils.chargeRFItemFromArmor(stack, st2);
					}
				}
			}
		}

		if (player.isSneaking() && IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player)
				&& toggleTimer == 0) {
			toggleTimer = 10;
			nightvisionmode += 1;
			if (nightvisionmode > 2) {
				nightvisionmode = 0;
			}

			if (IC2.platform.isSimulating()) {
				nbt.setInteger("NightVisMode", nightvisionmode);
				/*
				 * switch (nightvisionmode) { case 0: player.addChatMessage(new
				 * ChatComponentTranslation( EnumChatFormatting.RED +
				 * StatCollector.translateToLocal("chat.message.nightvision.off"), new
				 * Object[0])); // IC2.platform.messagePlayer(player, EnumChatFormatting.RED +
				 * // StatCollector.translateToLocal("chat.message.nightvision.off"), new //
				 * Object[0]); break; case 1: player.addChatMessage(new
				 * ChatComponentTranslation(EnumChatFormatting.DARK_AQUA +
				 * StatCollector.translateToLocal("chat.message.nightvision.auto"), new
				 * Object[0])); // IC2.platform.messagePlayer(player,
				 * EnumChatFormatting.DARK_AQUA + //
				 * StatCollector.translateToLocal("chat.message.nightvision.auto"), new //
				 * Object[0]); break; case 2: player.addChatMessage(new
				 * ChatComponentTranslation( EnumChatFormatting.GREEN +
				 * StatCollector.translateToLocal("chat.message.nightvision.on"), new
				 * Object[0])); // IC2.platform.messagePlayer(player, EnumChatFormatting.GREEN +
				 * // StatCollector.translateToLocal("chat.message.nightvision.on"), new //
				 * Object[0]);
				 * 
				 * }
				 */
			}
		}
		if (player.isSneaking() && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
			toggleTimer = 10;
			active = !active;
			if (IC2.platform.isSimulating()) {
				nbt.setBoolean("active", active);
				if (active) {
					player.addChatMessage(new ChatComponentTranslation(
							EnumChatFormatting.DARK_GREEN
									+ StatCollector.translateToLocal("chat.message.wirelesschargerf.on"),
							new Object[0]));
				} else {
					player.addChatMessage(new ChatComponentTranslation(
							EnumChatFormatting.DARK_RED
									+ StatCollector.translateToLocal("chat.message.wirelesschargerf.off"),
							new Object[0]));
				}
			}

		}

		if (nightvisionmode == 1 && IC2.platform.isSimulating()) {
			int x = MathHelper.floor_double((double) player.posX);
			int z = MathHelper.floor_double((double) player.posZ);
			int y = MathHelper.floor_double((double) player.posY);
			int skylight = player.worldObj.getBlockLightValue(x, y, z);
			if (skylight > 8) {
				IC2.platform.removePotion(player, Potion.nightVision.id);
			} else {
				player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, 0, true));
			}
			player.inventoryContainer.detectAndSendChanges();
		} else if (nightvisionmode == 2 && IC2.platform.isSimulating()) {
			player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, 0, true));
			player.inventoryContainer.detectAndSendChanges();
		}

		if (!world.isRemote) {
			if (active) {

				this.checkPlayers(player, world, stack);
			}
		}

		if (IC2.platform.isSimulating() && toggleTimer > 0) {
			toggleTimer = (byte) (toggleTimer - 1);
			nbt.setByte("toggleTimer", toggleTimer);
		}

		if (player.isBurning()) {
			player.extinguish();
		}

	}

	protected void checkPlayers(EntityPlayer player, World world, ItemStack thisarmor) {
		AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(player.posX - 15.0, player.posY - 15.0,
				player.posZ - 15.0, player.posX + 15.0, player.posY + 15.0, player.posZ + 15.0);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, axisalignedbb);
		for (Entity entityinlist : list) {
			if (entityinlist instanceof EntityPlayer) {
				EntityPlayer player1 = (EntityPlayer) entityinlist;
				if (player1 != null) {

					this.checkPlayerInventories(player1, thisarmor);
				}
			}
		}
	}

	protected void checkPlayerInventories(EntityPlayer player, ItemStack thisarmor) {
		for (ItemStack current : player.inventory.armorInventory) {
			if (current == null)
				continue;
			if (current.getItem() instanceof IEnergyContainerItem) {

				MiscUtils.chargeRFItemFromArmor(thisarmor, current);
			}
		}

		for (ItemStack current : player.inventory.mainInventory) {
			if (current == null)
				continue;
			if (current.getItem() instanceof IEnergyContainerItem) {

				MiscUtils.chargeRFItemFromArmor(thisarmor, current);
			}
		}
	}

	protected void gainFuel(EntityPlayer player, World world, ItemStack stack) {
		NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);

		if (world.getTotalWorldTime() % 20 == 0) {
			this.updateVisibility(player, stack);
		}

		if (nbt.getBoolean("Sunisup") && nbt.getBoolean("Skyisvisible")) {
			// this.generating = this.genDay;
			nbt.setInteger("generating", this.genDay);
			// return this.generating;
		} else if (nbt.getBoolean("Skyisvisible")) {
			// this.generating = this.genNight;
			nbt.setInteger("generating", this.genNight);
			// return this.generating;
		} else {
			// this.generating = 0;
			nbt.setInteger("generating", 0);
		}
		// return this.generating;
	}

	protected void updateVisibility(EntityPlayer player, ItemStack stack) {
		NBTTagCompound nbt1 = StackUtil.getOrCreateNbtData(stack);
		boolean wetBiome1 = player.worldObj.getWorldChunkManager().getBiomeGenAt((int) player.posX, (int) player.posZ)
				.getIntRainfall() > 0;
		boolean noSunWorld1 = player.worldObj.provider.hasNoSky;
		boolean rainWeather1 = wetBiome1 && (player.worldObj.isRaining() || player.worldObj.isThundering());
		boolean sunIsUp1 = player.worldObj.isDaytime() && !rainWeather1;
		boolean skyIsVisible1 = player.worldObj.canBlockSeeTheSky((int) player.posX, (int) player.posY + 1,
				(int) player.posZ) && !noSunWorld1;
		nbt1.setBoolean("Wetbiome", wetBiome1);
		nbt1.setBoolean("Nosunworld", noSunWorld1);
		nbt1.setBoolean("Sunisup", sunIsUp1);
		nbt1.setBoolean("Skyisvisible", skyIsVisible1);
		// this.wetBiome =
		// player.worldObj.getWorldChunkManager().getBiomeGenAt((int)player.posX,
		// (int)player.posZ).getIntRainfall() > 0;
		// this.noSunWorld = player.worldObj.provider.hasNoSky;
		// boolean rainWeather = this.wetBiome && (player.worldObj.isRaining() ||
		// player.worldObj.isThundering());
		// this.sunIsUp = player.worldObj.isDaytime() && !rainWeather;
		// this.skyIsVisible = player.worldObj.canBlockSeeTheSky((int)player.posX,
		// (int)player.posY + 1, (int)player.posZ) && !this.noSunWorld;
	}

	public int getEnergyPerDamage() {
		return 2000;
	}

	public double getDamageAbsorptionRatio() {
		return 1.0;
	}

	private double getBaseAbsorptionRatio() {
		return 0.15;
	}

	public boolean isRepairable() {

		return false;
	}

	public int getItemEnchantability() {

		return 0;
	}

	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {

		return false;
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage,
			int slot) {
		double absorptionRatio = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
		int energyPerDamage = this.getEnergyPerDamage();
		int damageLimit = (int) (energyPerDamage > 0
				? 25.0 * ElectricItem.manager.getCharge(armor) / (double) energyPerDamage
				: 0.0);
		return new ISpecialArmor.ArmorProperties(0, absorptionRatio, damageLimit);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {

		return (int) Math.round(20.0 * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio());
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {

		ElectricItem.manager.discharge(stack, (double) (damage * this.getEnergyPerDamage()), Integer.MAX_VALUE, true,
				false, false);
	}

	@Override
	public boolean isMetalArmor(ItemStack var1, EntityPlayer var2) {

		return true;
	}

	@Override
	public boolean canProvideEnergy(ItemStack var1) {

		return false;
	}

	@Override
	public Item getChargedItem(ItemStack var1) {

		return this;
	}

	@Override
	public Item getEmptyItem(ItemStack var1) {

		return this;
	}

	@Override
	public double getMaxCharge(ItemStack var1) {

		return this.maxenergyEU;
	}

	@Override
	public int getTier(ItemStack var1) {

		return this.tier;
	}

	@Override
	public double getTransferLimit(ItemStack var1) {

		return this.transferLimit;
	}

}
