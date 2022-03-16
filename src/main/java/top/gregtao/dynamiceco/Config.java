package top.gregtao.dynamiceco;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

public class Config {
    public DynamicEco plugin;
    public Language language;
    public File shopConfigFile;
    public File settingsFile;
    public File configFolder;

    public Config(DynamicEco plugin, String folder) {
        this.plugin = plugin;
        this.configFolder = new File(folder);
        this.shopConfigFile = new File(this.configFolder.getPath() + "/system_shops.yml");
        this.settingsFile = new File(this.configFolder.getPath() + "/.settings.yml");
        if (!this.configFolder.exists()) {
            if (!this.configFolder.mkdir()) {
                this.plugin.warn("Could not mkdir " + this.configFolder);
            } else {
                this.plugin.log("Created plugin file folder");
            }
        }
        if (!this.settingsFile.exists()) {
            this.plugin.copyFromResource(".settings.yml", this.settingsFile);
        }
        this.language = new Language(this.plugin, "en_us", this.configFolder);
        this.initSettings();
        this.readShops();
    }

    public void initSettings() {
        FileConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(this.settingsFile);
            String lang = configuration.getString("language") == null ? "en_us" : configuration.getString("language");
            this.language = new Language(this.plugin, lang, this.configFolder);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void readShops() {
        this.plugin.shopList.clear();
        FileConfiguration configuration = new YamlConfiguration();
        try {
            if (!this.shopConfigFile.exists()) {
                if (!this.shopConfigFile.createNewFile()) {
                    this.plugin.warn("Could not create file " + this.shopConfigFile);
                    return;
                }
                this.plugin.log("Created default config file");
                this.plugin.addDefaultShops();
                this.saveShops();
                return;
            }
            configuration.load(this.shopConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = configuration.getValues(false);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            MemorySection section = (MemorySection) entry.getValue();
            MemorySection item = (MemorySection) section.getValues(true).get("item");
            if (item == null) {
                this.plugin.error("Shop '" + entry.getKey() + "' is not available!");
                continue;
            }
            this.plugin.shopList.add(
                    new SystemShop(
                            ItemStack.deserialize(item.getValues(false)), section.getInt("amount"),
                            (float) section.getDouble("price"), entry.getKey(), (float) section.getDouble("delta"),
                            section.getInt("minprice"), section.getInt("maxprice"), section.getInt("maxamount"),
                            section.getInt("soldamount"), section.getInt("getamount"), this.plugin.shopList.size(), this.plugin)
            );
        }
        this.plugin.shopList.sort(Comparator.comparingInt(o -> o.id));
        this.plugin.log("Successfully loaded " + this.plugin.shopList.size() + " shops!");
    }

    public void saveShops() {
        this.plugin.log("Saving data!");
        FileConfiguration configuration = new YamlConfiguration();
        for (SystemShop shop : this.plugin.shopList) {
            if (!shop.removed) configuration.set(shop.name, shop.serialize());
            else configuration.set(shop.name, null);
        }
        try {
            configuration.save(this.shopConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
