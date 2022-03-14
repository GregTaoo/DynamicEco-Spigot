package top.gregtao.dynamiceco;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import top.gregtao.dynamiceco.commands.SystemShopCommand;
import top.gregtao.dynamiceco.commands.SystemShopConfigCommand;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class DynamicEco extends JavaPlugin {
    public Map<Integer, SystemShop> shopMap = new HashMap<>();
    public Economy economy = null;
    private Permission permission = null;
    private Chat chat = null;

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

    @Override
    public void onEnable() {
        this.addCommand("systemshop", new SystemShopCommand(this));
        this.addCommand("systemshopconfig", new SystemShopConfigCommand(this));
        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "");
        this.addListener(new SystemShopGUI(this));

        this.readShops();

        if (!setupEconomy()) {
            this.log("Vault dependency not found!");
            return;
        }
        setupPermissions();
        setupChat();
    }

    @Override
    public void onDisable() {
        this.saveShops();
    }

    public void addShop(Material item, int price) {
        this.shopMap.put(
                this.shopMap.size(),
                new SystemShop(item, 0, price, item.name(), 0.3f, 10, price, this)
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
        FileConfiguration configuration = this.getConfig();
        try {
            File file = new File("./system_shops.yml");
            if (!file.exists()) {
                if (!file.createNewFile()) return;
                this.addDefaultShops();
                this.saveShops();
                return;
            }
            configuration.load("./system_shops.yml");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = configuration.getValues(false);
        int slots = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (slots++ > 54) return;
            MemorySection section = (MemorySection) entry.getValue();
            Material material = Material.matchMaterial(Objects.requireNonNull(section.getString("item")));
            this.shopMap.put(
                    this.shopMap.size(),
                    new SystemShop(
                            material, section.getInt("amount"), (float) section.getDouble("price"), entry.getKey(),
                            (float) section.getDouble("delta"),
                            section.getInt("minprice"), section.getInt("maxprice"), this)
            );
        }
    }

    public void saveShops() {
        FileConfiguration configuration = this.getConfig();
        for (Map.Entry<Integer, SystemShop> entry : this.shopMap.entrySet()) {
            configuration.set(entry.getValue().name, entry.getValue().serialize());
        }
        try {
            configuration.save("./system_shops.yml");
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

    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = this.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) return;
        this.chat = rsp.getProvider();
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = this.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return;
        this.permission = rsp.getProvider();
    }

}
