package top.gregtao.dynamiceco;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SystemShopGUI implements Listener {
    public DynamicEco plugin;
    public Inventory gui = Bukkit.createInventory(null, 54, TitleColor.AQUA.getWith("SystemShop"));

    public SystemShopGUI(DynamicEco plugin) {
        this.plugin = plugin;
        for (SystemShop shop : this.plugin.shopList) {
            if (!shop.removed) this.gui.setItem(shop.id, shop.getSlot());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(TitleColor.AQUA.getWith("SystemShop")) && event.getClickedInventory() != null &&
               !event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if (this.plugin.shopList.size() <= slot) {
                event.setCancelled(true);
                return;
            }
            if (!this.plugin.economy.hasAccount(player)) this.plugin.economy.createPlayerAccount(player);
            SystemShop shop = this.plugin.shopList.get(slot);
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
                        } else if (itemStack.isSimilar(shop.item) && itemStack.getAmount() < itemStack.getType().getMaxStackSize()) {
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
                if (shop.amount >= shop.maxAmount) {
                    player.sendMessage("库存已满，无法出售！");
                    event.setCancelled(true);
                    return;
                }
                boolean success = false;
                for (ItemStack itemStack : player.getInventory()) {
                    if (itemStack != null && itemStack.isSimilar(shop.item)) {
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

