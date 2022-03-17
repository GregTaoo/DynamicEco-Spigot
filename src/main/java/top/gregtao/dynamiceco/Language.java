package top.gregtao.dynamiceco;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Language {
    public DynamicEco plugin;
    public String language;
    public File folder;
    public File file;

    public Map<String, String> map = new HashMap<>(); //语言文件哈希表

    public Language(DynamicEco plugin, String language, File configFolder) {
        this.plugin = plugin;
        this.language = language;
        this.folder = new File(configFolder.getPath() + "/languages");
        if (!this.folder.exists() && this.folder.mkdir()) { //拷贝默认语言文件
            File en = new File(this.folder.getPath() + "/en_us.yml");
            File zh = new File(this.folder.getPath() + "/zh_cn.yml");
            this.plugin.copyFromResource("en_us.yml", en);
            this.plugin.copyFromResource("zh_cn.yml", zh);
        }
        this.file = new File(this.folder.getPath() + "/" + this.language + ".yml");
        if (!this.file.exists()) { //准备读取语言文件
            this.language = "en_us";
            this.file = new File(this.folder.getPath() + "/" + this.language + ".yml");
            try {
                if (!this.file.exists() && this.file.createNewFile()) {
                    this.generateDefault();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.readLanguageConfig();
    }

    public void add(String key, String value) {
        this.map.put(key, value);
    }

    public String get(String key) {
        return this.map.get(key);
    }

    public void readLanguageConfig() { //读取语言文件
        FileConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(this.file);
            this.map.clear();
            for (Map.Entry<String, Object> entry : configuration.getValues(false).entrySet()) {
                this.add(entry.getKey(), entry.getValue().toString());
            }
            SystemShopGUI.border = SystemShopGUI.setNameForStack(
                    new ItemStack(Material.GRAY_STAINED_GLASS_PANE), TitleColor.GREEN.getWith(this.map.get("gui-border")));
            SystemShopGUI.leftArrow = SystemShopGUI.setNameForStack(
                    new ItemStack(Material.ARROW), TitleColor.AQUA.getWith(this.map.get("gui-left_arrow")));
            SystemShopGUI.rightArrow = SystemShopGUI.setNameForStack(
                    new ItemStack(Material.ARROW), TitleColor.AQUA.getWith(this.map.get("gui-right_arrow")));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void generateDefault() {
        FileConfiguration configuration = new YamlConfiguration(); //新建默认语言英语文件（废弃）
        try {
            configuration.load(this.file);
            this.add("gui-price", "Price");
            this.add("gui-amount", "Amount");
            this.add("gui-border", "BORDER");
            this.add("gui-left_arrow", "LEFT PAGE");
            this.add("gui-right_arrow", "RIGHT PAGE");
            this.add("gui-page_not_available", "This page is not available!");
            this.add("shop-buy_failure_removed", "This shop has been removed.");
            this.add("shop-buy_failure_balance", "Failed to buy! You don't have enough balance!");
            this.add("shop-buy_failure_inventory", "Failed to buy! Please clean your inventory!");
            this.add("shop-buy_success", "Successfully bought.");
            this.add("shop-sale_failure_full", "Failed to sale! This shop is full!");
            this.add("shop-sale_failure_no", "You don't have enough items for sale!");
            this.add("shop-sale_success", "Successfully sold out.");
            this.add("cmd-not_player", "You are not a player!");
            for (Map.Entry<String, String> entry : this.map.entrySet()) {
                configuration.set(entry.getKey(), entry.getValue());
            }
            configuration.save(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
