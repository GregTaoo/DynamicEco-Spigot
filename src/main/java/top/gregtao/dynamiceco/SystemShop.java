package top.gregtao.dynamiceco;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemShop {

    public ItemStack item;
    public int amount;
    public float price;
    public String name;
    public float delta;
    public int minPrice;
    public int maxPrice;
    public int maxAmount;
    public DynamicEco plugin;
    public int id;

    public int soldAmount;
    public int getAmount;

    public boolean removed = false;

    public SystemShop(ItemStack item, int amount, float price, String name, float delta, int minPrice, int maxPrice,
                      int maxAmount, int soldAmount, int getAmount, int id, DynamicEco plugin) {
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.name = name;
        this.delta = delta;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.maxAmount = maxAmount;
        this.soldAmount = soldAmount;
        this.getAmount = getAmount;
        this.id = id;
        this.plugin = plugin;
    }

    public float getPriceByProportion() {
        return (float) Math.round(Math.max(this.maxPrice - this.amount * this.delta / 16, this.minPrice) * 100) / 100;
    }

    public void setNewPrice() { //更新价格
        this.price = this.getPriceByProportion();
    }

    public ItemStack getSlot() { //获取GUI显示stack
        ItemStack itemStack = new ItemStack(this.item);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.name);
            List<String> list = new ArrayList<>();
            list.add(TitleColor.ORANGE.getWith("Price: " + this.price));
            list.add(TitleColor.GREEN.getWith("Amount: " + this.amount));
            list.add(TitleColor.GREY.getWith("ID: " + this.id));
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
        ItemStack newStack = this.item.clone();
        newStack.setAmount(amount);
        return newStack;
    }

    public void sale() {
        this.amount++;
        this.soldAmount++;
        this.setNewPrice();
    }

    public Map<String, Object> serialize() { //保存到Map内准备写入配置文件
        Map<String, Object> map = new HashMap<>();
        map.put("item", this.item.serialize());
        map.put("amount", this.amount);
        map.put("price", this.price);
        map.put("delta", this.delta);
        map.put("minprice", this.minPrice);
        map.put("maxprice", this.maxPrice);
        map.put("maxamount", this.maxAmount);
        map.put("soldamount", this.soldAmount);
        map.put("getamount", this.getAmount);
        return map;
    }

}
