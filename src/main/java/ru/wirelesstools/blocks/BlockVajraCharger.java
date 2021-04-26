package ru.wirelesstools.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;
import ru.wirelesstools.item.tool.LuckyVajra;
import ru.wirelesstools.tiles.TileVajraCharger;

public class BlockVajraCharger extends Block implements ITileEntityProvider {

	public BlockVajraCharger(String unlocalizedName, Material mat) {
		super(mat);
		this.setBlockName(unlocalizedName);
		setBlockTextureName(Reference.PathTex + "blockVajraCharger");
		setCreativeTab(MainWI.tabwi);
		setHardness(3.0F);
		setResistance(5.0F);
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {

		return new TileVajraCharger();

	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
			float subY, float subZ) {
		if (!world.isRemote) {
			// TileVajraCharger tileentity2 = (TileVajraCharger)world.getTileEntity(x, y,
			// z);
			/*
			 * player.addChatMessage(new
			 * ChatComponentTranslation(StatCollector.translateToLocal(
			 * "tile.vajracharger.currentenergy") + ": " +
			 * String.valueOf(tileentity2.getStored() + " EU")));
			 */
			player.openGui(MainWI.instance, 1, world, x, y, z);
		}

		return true;

	}

}
