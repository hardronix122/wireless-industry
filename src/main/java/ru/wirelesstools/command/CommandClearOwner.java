package ru.wirelesstools.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import ru.wirelesstools.item.armor.IPrivateArmor;

import java.util.ArrayList;
import java.util.List;

public class CommandClearOwner extends CommandBase {

	public static final String NAME_COMMAND = "cleararmorowner";
	public static final String ALIAS_1 = "clo";

	public static final String USAGE_COMMAND = "/" + NAME_COMMAND + " " + "<player>";

	@Override
	public String getCommandName() {

		return NAME_COMMAND;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {

		return USAGE_COMMAND;
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		if (commandSender instanceof EntityPlayer) {
			if (args.length > 1) {
				throw new WrongUsageException(this.getCommandUsage(commandSender));
			} else if (args.length == 1) {
				EntityPlayerMP playermp = getPlayer(commandSender, args[0]);
				EntityPlayerMP playersender = this.getCommandSenderAsPlayer(commandSender);
				if (!playermp.getGameProfile().equals(playersender.getGameProfile())) {
					for (ItemStack stack : playermp.inventory.mainInventory) {
						if (stack == null)
							continue;
						if (stack.getItem() instanceof IPrivateArmor) {
							IPrivateArmor armor = (IPrivateArmor) stack.getItem();
							armor.clearOwner(stack);
							playermp.addChatMessage(
									new ChatComponentTranslation("Cleared armor owner from main inventory"));
							playersender.addChatMessage(
									new ChatComponentTranslation("Cleared armor owner from player inventory"));
						}
					}
				} else {
					playersender.addChatMessage(new ChatComponentTranslation("Please use " + "/" + ALIAS_1));
				}
			} else if (args.length == 0) {
				EntityPlayerMP senderplayer = this.getCommandSenderAsPlayer(commandSender);
				for (ItemStack stack : senderplayer.inventory.mainInventory) {
					if (stack == null)
						continue;
					if (stack.getItem() instanceof IPrivateArmor) {
						IPrivateArmor armor = (IPrivateArmor) stack.getItem();
						if (armor.getArmorOwner(stack) != null && !armor.getArmorOwner(stack)
								.equals(((EntityPlayerMP) commandSender).getGameProfile())) {
							armor.clearOwner(stack);
							senderplayer.addChatMessage(new ChatComponentTranslation(
									"Cleared owner successfully from your main inventory!"));
						} else if (armor.getArmorOwner(stack) == null) {
							senderplayer.addChatMessage(new ChatComponentTranslation("It is already no owner of armor"));
						} else {
							senderplayer.addChatMessage(new ChatComponentTranslation(
									"You cannot clear yourself from owner using this command!"));
						}
					}
				}
			}
		}
	}

	@Override
	public int getRequiredPermissionLevel() {

		return 2;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {

		return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
				: null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender commandSender) {

		// ServerConfigurationManager.func_152596_g(GameProfile p_152596_1_) �������� ��
		// ��, ����� �� ���� ����� ������������ �������
		return commandSender instanceof EntityPlayerMP
				? MinecraftServer.getServer().getConfigurationManager()
						.func_152596_g(((EntityPlayerMP) commandSender).getGameProfile())
				: false;
	}

	@Override
	public List<String> getCommandAliases() {
		List<String> aliaseslist = new ArrayList<>();
		aliaseslist.add(ALIAS_1);

		return aliaseslist;
	}

}
