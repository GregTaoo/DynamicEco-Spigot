package top.gregtao.dynamiceco.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.gregtao.dynamiceco.DynamicEco;
import top.gregtao.dynamiceco.MathHelper;
import top.gregtao.dynamiceco.SystemShop;
import top.gregtao.dynamiceco.TitleColor;

public class SystemShopConfigCommand implements CommandExecutor {
    public DynamicEco plugin;

    public SystemShopConfigCommand(DynamicEco plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1 : {
                if (args[0].equalsIgnoreCase("setToDefault")) {
                    sender.sendMessage(TitleColor.RED.getWith("Set configs to default states!"));
                    this.plugin.log("Set configs to default states!");
                    this.plugin.shopList.clear();
                    this.plugin.addDefaultShops();
                    this.plugin.config.saveShops();
                    return true;
                } else if (args[0].equalsIgnoreCase("reloadShops")) {
                    sender.sendMessage(TitleColor.RED.getWith("Reloading configs"));
                    this.plugin.log("Reloading configs");
                    this.plugin.config.readShops();
                    return true;
                }
                return false;
            }
            case 2 : {
                if (args[0].equalsIgnoreCase("removeShop")) {
                    int id = MathHelper.parseInt(args[1]);
                    if (id != -1) {
                        boolean success = false;
                        for (SystemShop shop : this.plugin.shopList) {
                            if (shop.id == id) {
                                String title = shop.name;
                                shop.removed = success = true;
                                this.plugin.config.saveShops();
                                sender.sendMessage("Successfully removed system shop '" + title + "', whose ID is " + id);
                                break;
                            }
                        }
                        if (!success) sender.sendMessage("Could not found a system shop by ID " + id);
                        else this.plugin.config.readShops();
                        return true;
                    }
                }
                return false;
            }
            case 6 : {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args[0].equalsIgnoreCase("createShop")) {
                        int minPrice = MathHelper.parseInt(args[1]);
                        int maxPrice = MathHelper.parseInt(args[2]);
                        float delta = MathHelper.parseFloat(args[3]);
                        int maxAmount = MathHelper.parseInt(args[4]);
                        String name = args[5];
                        for (SystemShop shop : this.plugin.shopList) {
                            if (name.equals(shop.name)) {
                                player.sendMessage(TitleColor.YELLOW.getWith("This title had been used by another!"));
                                return true;
                            }
                        }
                        ItemStack itemStack = player.getInventory().getItemInMainHand().clone();
                        if (itemStack.getType().isAir()) {
                            player.sendMessage("You must hold something in your main hand!");
                        } else if (maxPrice == -1 || minPrice == -1 || delta == -1 || maxAmount == -1 || name.isEmpty()) {
                            player.sendMessage("Arguments error!");
                        } else {
                            itemStack.setAmount(1);
                            this.plugin.addShop(itemStack, name, minPrice, maxPrice, delta, maxAmount);
                            player.sendMessage("Successfully created a new system shop!");
                        }
                        return true;
                    }
                    return false;
                } else {
                    sender.sendMessage(TitleColor.RED.getWith("You are not a player!"));
                    return true;
                }
            }
            default: return false;
        }
    }
}
