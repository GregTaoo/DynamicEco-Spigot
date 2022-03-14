package top.gregtao.dynamiceco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import top.gregtao.dynamiceco.DynamicEco;
import top.gregtao.dynamiceco.TitleColor;

public class SystemShopConfigCommand implements CommandExecutor {
    public DynamicEco plugin;

    public SystemShopConfigCommand(DynamicEco plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1: {
                if (args[0].equalsIgnoreCase("setToDefault")) {
                    sender.sendMessage(TitleColor.RED.getWith("Set configs to default states!"));
                    this.plugin.log("Set configs to default states!");
                    this.plugin.shopMap.clear();
                    this.plugin.addDefaultShops();
                    this.plugin.saveShops();
                } else if (args[0].equalsIgnoreCase("reloadShops")) {
                    sender.sendMessage(TitleColor.RED.getWith("Reloading configs"));
                    this.plugin.log("Reloading configs");
                    this.plugin.shopMap.clear();
                    this.plugin.readShops();
                }
                break;
            }
            default: return false;
        }
        return true;
    }
}
