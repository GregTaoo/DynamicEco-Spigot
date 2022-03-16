package top.gregtao.dynamiceco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.gregtao.dynamiceco.DynamicEco;
import top.gregtao.dynamiceco.MathHelper;
import top.gregtao.dynamiceco.SystemShopGUI;
import top.gregtao.dynamiceco.TitleColor;

public class SystemShopCommand implements CommandExecutor {
    private final DynamicEco plugin;

    public SystemShopCommand(DynamicEco plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("SystemShop")) {
            int page = args.length == 1 ? MathHelper.parseInt(args[0]) : 0;
            if (!(sender instanceof Player)) {
                sender.sendMessage(TitleColor.RED.getWith(this.plugin.getLanguage("cmd-not_player")));
            } else if (page == -1 || !MathHelper.inRange(0, this.plugin.getPages(), page)) {
                sender.sendMessage(this.plugin.getLanguage("gui-page_not_available"));
            } else  {
                Player player = (Player) sender;
                player.openInventory(new SystemShopGUI(this.plugin, page).gui);
            }
            return true;
        }
        return false;
    }
}