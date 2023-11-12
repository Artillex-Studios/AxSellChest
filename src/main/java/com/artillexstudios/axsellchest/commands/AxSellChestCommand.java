package com.artillexstudios.axsellchest.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellchest.AxSellChestPlugin;
import com.artillexstudios.axsellchest.chests.ChestType;
import com.artillexstudios.axsellchest.config.impl.Messages;
import com.artillexstudios.axsellchest.utils.ItemBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"axsellchest", "sellchest", "axsc"})
public class AxSellChestCommand {

    @Subcommand("give")
    @CommandPermission("axsellchest.command.give")
    public void give(CommandSender sender, Player player, ChestType chestType, int amount) {
        player.getInventory().addItem(new ItemBuilder(chestType.getItem(0, 0)).amount(amount).get());
        sender.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.GIVE_SUCCESS, Placeholder.parsed("amount", String.valueOf(amount)), Placeholder.parsed("player", player.getName()), Placeholder.parsed("type", chestType.getName())));
        player.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.RECEIVE_SUCCESS, Placeholder.parsed("amount", String.valueOf(amount)), Placeholder.parsed("type", chestType.getName())));
    }

    @Subcommand("give")
    @CommandPermission("axsellchest.command.give")
    public void give(Player sender, ChestType chestType, int amount) {
        sender.getInventory().addItem(new ItemBuilder(chestType.getItem(0, 0)).amount(amount).get());
        sender.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.GIVE_SUCCESS, Placeholder.parsed("amount", String.valueOf(amount)), Placeholder.parsed("player", sender.getName()), Placeholder.parsed("type", chestType.getName())));
    }

    @Subcommand("reload")
    @CommandPermission("axsellchest.command.reload")
    public void reload(CommandSender sender) {
        sender.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.RELOAD, Placeholder.parsed("time", String.valueOf(AxSellChestPlugin.getInstance().reloadWithTime()))));
    }

    @Subcommand({"stats", "statistics"})
    @CommandPermission("axsellchest.command.statistics")
    public void stats(CommandSender sender) {

    }
}
