package com.artillexstudios.axsellchest.commands;

import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellchest.AxSellChestPlugin;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.chests.ChestType;
import com.artillexstudios.axsellchest.chests.Chests;
import com.artillexstudios.axsellchest.config.impl.Messages;
import com.artillexstudios.axsellchest.converter.Converter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Command({"axsellchest", "sellchest", "axsc"})
public class AxSellChestCommand {

    @Subcommand("give")
    @CommandPermission("axsellchest.command.give")
    public void give(CommandSender sender, Player player, ChestType chestType, @Default("1") int amount) {
        player.getInventory().addItem(new ItemBuilder(chestType.getItem(BigInteger.ZERO, BigDecimal.ZERO)).amount(amount).get());
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

    @Subcommand("convert")
    @CommandPermission("axsellchest.command.convert")
    public void convert(CommandSender sender, String converter) {
        switch (converter) {
            case "voidchest" -> {
                Converter.CONVERTERS.get(0).convert();
            }
            case "voidchestv2" -> {
                Converter.CONVERTERS.get(1).convert();
            }
        }
    }

    @DefaultFor({"~", "~ help"})
    @CommandPermission("axsellchest.command.help")
    public void help(CommandSender sender) {
        for (String s : Messages.HELP) {
            sender.sendMessage(StringUtils.formatToString(s));
        }
    }
}
