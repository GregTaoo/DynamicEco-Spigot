package top.gregtao.dynamiceco;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import top.gregtao.dynamiceco.commands.SystemShopCommand;
import top.gregtao.dynamiceco.commands.SystemShopConfigCommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class DynamicEco extends JavaPlugin {
    public File shopConfigFile = new File("./plugins/DynamicEco/system_shops.yml");
    public File configFolder = this.shopConfigFile.getParentFile();
    public List<SystemShop> shopList = new ArrayList<>();
    public Economy economy = null;

    public void addCommand(String commandStr, CommandExecutor executor) {
        PluginCommand command = this.getCommand(commandStr);
        if (command == null) return;
        command.setExecutor(executor);
    }

    public void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void log(String str) {
        this.getLogger().log(Level.INFO, str);
    }

    public void error(String str) {
        this.getLogger().log(Level.SEVERE, str);
    }

    public void warn(String str) {
        this.getLogger().log(Level.WARNING, str);
    }

    @Override
    public void onEnable() {
        if (!this.configFolder.exists()) {
            if (!this.configFolder.mkdir()) {
                this.warn("Could not mkdir " + this.configFolder);
            } else {
                this.log("Created plugin file folder");
            }
        }

        this.addCommand("systemshop", new SystemShopCommand(this));
        this.addCommand("systemshopconfig", new SystemShopConfigCommand(this));

        this.addListener(new SystemShopGUI(this));

        this.readShops();

        if (!setupEconomy()) {
            this.error("Vault dependency not found!");
        }
    }

    @Override
    public void onDisable() {
        this.saveShops();
    }

    public void addShop(Material item, int price) {
        int id = this.shopList.size();
        this.shopList.add(
                new SystemShop(new ItemStack(item), 0, price, item.name(),
                        0.3f, 10, 100, 1000, 0, 0, id, this)
        );
    }

    public void addShop(ItemStack itemStack, String name, int minPrice, int maxPrice, float delta, int maxAmount) {
        int id = this.shopList.size();
        this.shopList.add(
                new SystemShop(itemStack, 0, maxPrice, name,
                        delta, minPrice, maxPrice, maxAmount, 0, 0, id, this)
        );
    }

    public void addDefaultShops() {
        this.addShop(Material.DIAMOND, 100);
        this.addShop(Material.IRON_INGOT, 100);
        this.addShop(Material.COAL, 100);
        this.addShop(Material.LAPIS_LAZULI, 100);
        this.addShop(Material.GOLD_INGOT, 100);
        this.addShop(Material.REDSTONE, 100);
        this.addShop(Material.NETHERITE_INGOT, 100);
    }

    public void readShops() {
        this.shopList.clear();
        FileConfiguration configuration = this.getConfig();
        try {
            if (!this.shopConfigFile.exists()) {
                if (!this.shopConfigFile.createNewFile()) {
                    this.warn("Could not create file " + this.shopConfigFile);
                    return;
                }
                this.log("Created default config file");
                this.addDefaultShops();
                this.saveShops();
                return;
            }
            configuration.load(this.shopConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = configuration.getValues(false);
        int slots = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (slots++ > 54) return;
            MemorySection section = (MemorySection) entry.getValue();
            MemorySection item = (MemorySection) section.getValues(true).get("item");
            if (item == null) {
                this.error("Shop '" + entry.getKey() + "' is not available!");
                continue;
            }
            this.shopList.add(
                    new SystemShop(
                            ItemStack.deserialize(item.getValues(false)), section.getInt("amount"),
                            (float) section.getDouble("price"), entry.getKey(), (float) section.getDouble("delta"),
                            section.getInt("minprice"), section.getInt("maxprice"), section.getInt("maxamount"),
                            section.getInt("soldamount"), section.getInt("getamount"), this.shopList.size(), this)
            );
        }
        this.log("Successfully loaded " + this.shopList.size() + " shops!");
    }

    public void saveShops() {
        this.log("Saving data!");
        FileConfiguration configuration = this.getConfig();
        for (SystemShop shop : this.shopList) {
            if (!shop.removed) configuration.set(shop.name, shop.serialize());
            else configuration.set(shop.name, null);
        }
        try {
            configuration.save(this.shopConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = rsp.getProvider();
        return true;
    }

}
