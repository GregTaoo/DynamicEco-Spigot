package top.gregtao.dynamiceco;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SystemShopGUI implements Listener {
    public DynamicEco plugin;
    public String title = "SystemShop-";
    public Inventory gui;
    public int page;

    public static ItemStack border;
    public static ItemStack leftArrow;
    public static ItemStack rightArrow;

    static {
        border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        border = setNameForStack(border, TitleColor.GREEN.getWith("BORDER"));
        leftArrow = new ItemStack(Material.ARROW);
        leftArrow = setNameForStack(leftArrow, TitleColor.AQUA.getWith("LEFT PAGE"));
        rightArrow = new ItemStack(Material.ARROW);
        rightArrow = setNameForStack(rightArrow, TitleColor.AQUA.getWith("RIGHT PAGE"));
    }

    public static ItemStack setNameForStack(ItemStack itemStack, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public SystemShopGUI(DynamicEco plugin, int page) {
        this.plugin = plugin;
        this.page = page;
        this.title = TitleColor.AQUA.getWith(this.title + (this.page < 0 ? "" : this.page));
        this.gui = Bukkit.createInventory(null, 27, this.title);
        if (page >= 0) {
            for (int i = page * 18; i < Math.min(this.plugin.shopList.size(), page * 18 + 18); ++i) {
                SystemShop shop = this.plugin.shopList.get(i);
                this.gui.setItem(this.getSlot(shop.id), shop.getSlot());
            }
            this.setGUIBottom();
        }
    }

    public void setGUIBottom() {
        for (int i = 19; i <= 25; ++i) {
            this.gui.setItem(i, border.clone());
        }
        this.gui.setItem(18, this.page == 0 ? border : leftArrow);
        this.gui.setItem(26, this.page == this.plugin.getPages() ? border : rightArrow);
    }

    public int getSlot(int id) {
        return id % 18;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.length() > 13 && title.substring(0, 13).equals(this.title) && event.getClickedInventory() != null &&
                !event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            int page = MathHelper.parseInt(title.substring(13));
            if (page < 0) {
                player.closeInventory();
                return;
            }
            if (slot == 18) {
                event.setCancelled(true);
                player.openInventory(new SystemShopGUI(this.plugin, Math.max(0, page - 1)).gui);
                return;
            } else if (slot == 26) {
                event.setCancelled(true);
                player.openInventory(new SystemShopGUI(this.plugin, Math.min(this.plugin.getPages(), page + 1)).gui);
                return;
            } else if (slot >= 18 || (page == this.plugin.getPages() && slot > this.plugin.shopList.size() % 18)) {
                event.setCancelled(true);
                return;
            }
            if (!this.plugin.economy.hasAccount(player)) this.plugin.economy.createPlayerAccount(player);
            SystemShop shop = this.plugin.shopList.get(slot + page * 18);
            if (shop.removed) {
                player.sendMessage(this.plugin.getLanguage("shop-buy_failure_removed"));
                return;
            }
            if (event.isLeftClick()) {
                if (this.plugin.economy.getBalance(player) < shop.price) {
                    player.sendMessage(this.plugin.getLanguage("shop-buy_failure_balance"));
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
                player.sendMessage(this.plugin.getLanguage(success ? "shop-buy_success" : "shop-buy_failure_inventory"));
                event.setCancelled(true);
                if (success) {
                    this.gui.setItem(slot, shop.getSlot());
                    player.getOpenInventory().setItem(slot, shop.getSlot());
                }
            } else if (event.isRightClick()) {
                if (shop.amount >= shop.maxAmount) {
                    player.sendMessage(this.plugin.getLanguage("shop-sale_failure_full"));
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
                player.sendMessage(this.plugin.getLanguage(success ? "shop-sale_success" : "shop-sale_failure_no"));
                event.setCancelled(true);
                if (success) {
                    this.gui.setItem(slot, shop.getSlot());
                    player.getOpenInventory().setItem(slot, shop.getSlot());
                }
            }
        }
    }

}

