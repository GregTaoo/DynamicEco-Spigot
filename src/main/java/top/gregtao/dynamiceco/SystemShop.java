package top.gregtao.dynamiceco;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemShop {

    public Material item;
    public int amount;
    public float price;
    public String name;
    public float delta;
    public int minPrice;
    public int maxPrice;
    public DynamicEco plugin;

    public int soldAmount = 0;
    public int getAmount = 0;

    public SystemShop(Material material, int amount, float price, String name, float delta, int minPrice, int maxPrice, DynamicEco plugin) {
        this.item = material;
        this.amount = amount;
        this.price = price;
        this.name = name;
        this.delta = delta;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.plugin = plugin;
    }

    public float getProportion() {
        return this.getAmount == 0 ? 0 : (float) this.soldAmount / (float) this.getAmount;
    }

    public float getPriceByProportion() {
        return Math.max(this.maxPrice - this.amount * this.delta / 16, this.minPrice);
    }

    public void setNewPrice() {
        this.price = this.getPriceByProportion();
    }

    public ItemStack getSlot() {
        ItemStack itemStack = new ItemStack(this.item);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.name);
            List<String> list = new ArrayList<>();
            list.add(TitleColor.ORANGE.getWith("Price: " + this.price));
            list.add(TitleColor.GREEN.getWith("Amount: " + this.amount));
            meta.setLore(list);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public ItemStack buy(int amount) {
        amount = Math.min(amount, this.amount);
        this.amount -= amount;
        this.getAmount += amount;
        this.setNewPrice();
        return new ItemStack(this.item, amount);
    }

    public void sale() {
        this.amount++;
        this.soldAmount++;
        this.setNewPrice();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("item", this.item.name());
        map.put("amount", this.amount);
        map.put("price", this.price);
        map.put("delta", this.delta);
        map.put("minprice", this.minPrice);
        map.put("maxprice", this.maxPrice);
        return map;
    }

}
