package com.artillexstudios.axsellchest.commands;

import com.artillexstudios.axsellchest.chests.ChestType;
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

    }

    @Subcommand("give")
    @CommandPermission("axsellchest.command.give")
    public void give(Player sender, ChestType chestType, int amount) {

    }

    @Subcommand("reload")
    @CommandPermission("axsellchest.command.reload")
    public void reload(CommandSender sender) {

    }

    @Subcommand({"stats", "statistics"})
    @CommandPermission("axsellchest.command.statistics")
    public void stats(CommandSender sender) {

    }
}
