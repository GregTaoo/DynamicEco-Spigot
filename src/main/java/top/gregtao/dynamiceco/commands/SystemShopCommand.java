package top.gregtao.dynamiceco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.gregtao.dynamiceco.DynamicEco;
import top.gregtao.dynamiceco.SystemShopGUI;

public class SystemShopCommand implements CommandExecutor {
    private final DynamicEco plugin;

    public SystemShopCommand(DynamicEco plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("systemshop") && sender instanceof Player) {
            Player player = (Player) sender;
            player.openInventory(new SystemShopGUI(this.plugin).gui);
            return true;
        }
        return false;
    }
}