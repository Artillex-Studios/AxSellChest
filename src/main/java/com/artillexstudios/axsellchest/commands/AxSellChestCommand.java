package com.artillexstudios.axsellchest.commands;

import com.artillexstudios.axsellchest.chests.ChestType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;

@Command({"axsellchest", "sellchest", "axsc"})
public class AxSellChestCommand {

    @Subcommand("give")
    public void give(CommandSender sender, Player player, ChestType chestType, int amount) {

    }

    @Subcommand("give")
    public void give(Player sender, ChestType chestType, int amount) {

    }

    @Subcommand("reload")
    public void reload(CommandSender sender) {

    }
}
