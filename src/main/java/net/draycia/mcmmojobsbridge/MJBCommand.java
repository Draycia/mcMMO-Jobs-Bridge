package net.draycia.mcmmojobsbridge;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MJBCommand implements CommandExecutor {

    private McMMOJobsBridge main;

    MJBCommand(McMMOJobsBridge main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            main.reloadConfig();

            String message = "&amcMMO-Jobs-Bridge successfully reloaded!";
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

            return true;
        }

        return true;
    }
}
