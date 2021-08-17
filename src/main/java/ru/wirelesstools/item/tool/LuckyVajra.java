package ru.wirelesstools.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.tiles.TileVajraCharger;
import ru.wirelesstools.utils.MiscUtils;
import ru.wirelesstools.utils.UtilFormatNumber;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class LuckyVajra extends ItemTool implements IElectricItem {

    public double maxCharge;
    protected int tier;
    protected int energyPerOperation;
    protected double transferLimit;

    public LuckyVajra(ToolMaterial material) {
        super(0.0F, material, new HashSet());
        this.setUnlocalizedName("wirelessvajra");
        this.setTextureName(Reference.PathTex + "itemVajraLucky");
        this.setCreativeTab(MainWI.tabwi);
        this.tier = 3;
        this.maxCharge = ConfigWI.maxVajraCharge;
        this.transferLimit = 500000.0;
        this.efficiencyOnProperMaterial = 20000F; //effective power
        this.energyPerOperation = ConfigWI.vajraEnergyPerOperation;
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return this.maxCharge;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return this.tier;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return this.transferLimit;
    }

    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k,
                                  int side, float a, float b, float c) {
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(i, j, k);
            if(te instanceof TileVajraCharger) {
                TileVajraCharger tilewch = (TileVajraCharger) te;
                if(tilewch.getStored() > 0 && ElectricItem.manager.charge(itemstack, Integer.MAX_VALUE,
                        Integer.MAX_VALUE, true, true) > 0) {
                    double energySent = ElectricItem.manager.charge(itemstack, tilewch.getStored(),
                            Integer.MAX_VALUE, true, false);
                    tilewch.addEnergy((int) -energySent);
                    MiscUtils.sendChatMessageColoredMulti(entityplayer, "chat.message.luckyvajra.charged", EnumChatFormatting.BLUE,
                            new ChatComponentText(" " + UtilFormatNumber.formatNumber(energySent) + " EU").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)));
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(!world.isRemote) {
            if(player.isSneaking()) {
                boolean modenew = !nbt.getBoolean("vajramode");
                nbt.setBoolean("vajramode", modenew);
                Map<Integer, Integer> enchantmentMaplocal = EnchantmentHelper.getEnchantments(stack);
                if(modenew) {
                    enchantmentMaplocal.put(Integer.valueOf(Enchantment.fortune.effectId), Integer.valueOf(5));
                    MiscUtils.sendColoredMessageToPlayer(player, "chat.message.fortune.active", EnumChatFormatting.AQUA);
                } else {
                    enchantmentMaplocal.remove(Integer.valueOf(Enchantment.fortune.effectId));
                    MiscUtils.sendColoredMessageToPlayer(player, "chat.message.fortune.none", EnumChatFormatting.DARK_PURPLE);
                }
                EnchantmentHelper.setEnchantments(enchantmentMaplocal, stack);
            }
        }
        return stack;
    }

    public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
        if(ElectricItem.manager.use(itemstack, (this.energyPerOperation * 2), attacker)) {
            entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 25.0F);
        } else {
            entityliving.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker), 1.0F);
        }

        return false;
    }

    public boolean canHarvestBlock(Block block, ItemStack stack) {
        return block != Blocks.bedrock;
    }

    public float getDigSpeed(ItemStack tool, Block block, int meta) {
        if(!ElectricItem.manager.canUse(tool, this.energyPerOperation)) {
            return 1.0F;
        }

        if(canHarvestBlock(block, tool)) {
            return this.efficiencyOnProperMaterial;
        }

        return 1.0F;
    }

    public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int xPos, int yPos, int zPos,
                                    EntityLivingBase entityliving) {
        if(block.getBlockHardness(world, xPos, yPos, zPos) != 0.0D) {
            if(entityliving != null) {
                ElectricItem.manager.use(itemstack, this.energyPerOperation, entityliving);
            } else {
                ElectricItem.manager.discharge(itemstack, this.energyPerOperation, this.tier, true, false, false);
            }
        }
        return true;
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

    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            info.add(I18n.format("about.ench1") + " " + EnumChatFormatting.AQUA
                    + I18n.format("tooltip.fortune"));
            info.add(I18n.format("about.ench2"));
        } else {
            info.add(EnumChatFormatting.ITALIC + I18n.format("press.lshift"));
        }
    }

    protected ItemStack getItemStack(double charge) {
        ItemStack st = new ItemStack(this);
        ElectricItem.manager.charge(st, charge, Integer.MAX_VALUE, true, false);
        return st;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        itemList.add(this.getItemStack(Double.POSITIVE_INFINITY));
        itemList.add(this.getItemStack(0.0));
    }

}
