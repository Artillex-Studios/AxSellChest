package com.artillexstudios.axsellchest.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellchest.AxSellChestPlugin;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.chests.ChestType;
import com.artillexstudios.axsellchest.chests.Chests;
import com.artillexstudios.axsellchest.config.impl.Messages;
import com.artillexstudios.axsellchest.utils.ItemBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

@Command({"axsellchest", "sellchest", "axsc"})
public class AxSellChestCommand {

    @Subcommand("give")
    @CommandPermission("axsellchest.command.give")
    public void give(CommandSender sender, Player player, ChestType chestType, @Default("1") int amount) {
        player.getInventory().addItem(new ItemBuilder(chestType.getItem(0, 0)).amount(amount).get());
        sender.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.GIVE_SUCCESS, Placeholder.parsed("amount", String.valueOf(amount)), Placeholder.parsed("player", player.getName()), Placeholder.parsed("type", chestType.getName())));
        player.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.RECEIVE_SUCCESS, Placeholder.parsed("amount", String.valueOf(amount)), Placeholder.parsed("type", chestType.getName())));
    }

    @Subcommand("reload")
    @CommandPermission("axsellchest.command.reload")
    public void reload(CommandSender sender) {
        sender.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.RELOAD, Placeholder.parsed("time", String.valueOf(AxSellChestPlugin.getInstance().reloadWithTime()))));
    }

    @Subcommand({"stats", "statistics"})
    @CommandPermission("axsellchest.command.statistics")
    public void stats(CommandSender sender) {
        List<Chest> chests = Chests.getChests();
        int ticking = 0;
        int notTicking = 0;

        for (int i = 0; i < chests.size(); i++) {
            Chest chest = chests.get(i);
            if (chest.isTicking()) {
                ticking++;
            } else {
                notTicking++;
            }
        }

        sender.sendMessage(StringUtils.formatToString("""
                Ticking chests: <ticking>
                Not ticking chests: <not-ticking>
                All: <all>\
                """, Placeholder.parsed("ticking", String.valueOf(ticking)), Placeholder.parsed("not-ticking", String.valueOf(notTicking)), Placeholder.parsed("all", String.valueOf(notTicking + ticking))));
    }

    @Subcommand("startticking")
    @CommandPermission("axsellchest.command.startticking")
    public void startTicking(Player sender) {
        Chunk chunk = sender.getLocation().getChunk();
        Chests.startTicking(chunk);
    }
}
