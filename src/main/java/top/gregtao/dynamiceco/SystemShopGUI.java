package top.gregtao.dynamiceco;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public class SystemShopGUI implements Listener {
    public DynamicEco plugin;
    public Inventory gui = Bukkit.createInventory(null, 54, TitleColor.AQUA.getWith("SystemShop"));

    public SystemShopGUI(DynamicEco plugin) {
        this.plugin = plugin;
        for (Map.Entry<Integer, SystemShop> entry : this.plugin.shopMap.entrySet()) {
            this.gui.setItem(entry.getKey(), entry.getValue().getSlot());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(TitleColor.AQUA.getWith("SystemShop")) &&
                !Objects.requireNonNull(event.getClickedInventory()).getType().equals(InventoryType.PLAYER)) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if (!this.plugin.shopMap.containsKey(slot)) {
                event.setCancelled(true);
                return;
            }
            if (!this.plugin.economy.hasAccount(player)) this.plugin.economy.createPlayerAccount(player);
            SystemShop shop = this.plugin.shopMap.get(slot);
            if (event.isLeftClick()) {
                if (this.plugin.economy.getBalance(player) < shop.price) {
                    player.sendMessage("购买失败，余额不足");
                    event.setCancelled(true);
                    return;
                }
                int p = -1;
                boolean success = false;
                if (shop.amount > 0) {
                    for (ItemStack itemStack : player.getInventory()) {
                        p++;
                        if (itemStack == null) {
                            player.getInventory().setItem(p, shop.buy(1));
                            success = true;
                            break;
                        } else if (itemStack.getType().equals(shop.item) && itemStack.getAmount() < 64) {
                            itemStack.setAmount(itemStack.getAmount() + 1);
                            shop.buy(1);
                            this.plugin.economy.withdrawPlayer(player, shop.price);
                            success = true;
                            break;
                        }
                    }
                }
                player.sendMessage(success ? "购买成功！" : "购买失败");
                event.setCancelled(true);
                if (success) {
                    this.gui.setItem(slot, shop.getSlot());
                    player.getOpenInventory().setItem(slot, shop.getSlot());
                }
            } else if (event.isRightClick()) {
                boolean success = false;
                for (ItemStack itemStack : player.getInventory()) {
                    if (itemStack != null && itemStack.getType().equals(shop.item)) {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        shop.sale();
                        this.plugin.economy.depositPlayer(player, shop.price);
                        success = true;
                        break;
                    }
                }
                player.sendMessage(success ? "出售成功！" : "出售失败，物品不足！");
                event.setCancelled(true);
                if (success) {
                    this.gui.setItem(slot, shop.getSlot());
                    player.getOpenInventory().setItem(slot, shop.getSlot());
                }
            }
        }
    }

}

