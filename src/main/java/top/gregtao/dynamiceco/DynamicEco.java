package top.gregtao.dynamiceco;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import top.gregtao.dynamiceco.commands.SystemShopCommand;
import top.gregtao.dynamiceco.commands.SystemShopConfigCommand;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DynamicEco extends JavaPlugin {

    public Config config;
    public List<SystemShop> shopList = new ArrayList<>();
    public Economy economy = null;

    public InputStream getFileInResource(String file) {
        return this.getResource(file);
    }

    public void copyFromResource(String from, File to) {
        try {
            if (!to.exists() && to.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(to), StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.getFileInResource(from), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }
                writer.close();
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPages() {
        return this.shopList.size() / 18;
    }

    public String getLanguage(String str) {
        String string = this.config.language.get(str);
        return string == null ? "" : string;
    }

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
        this.config = new Config(this, "./plugins/DynamicEco");

        this.addCommand("systemshop", new SystemShopCommand(this));
        this.addCommand("systemshopconfig", new SystemShopConfigCommand(this));

        this.addListener(new SystemShopGUI(this, -1));

        if (!setupEconomy()) {
            this.error("Vault dependency not found!");
        }
    }

    @Override
    public void onDisable() {
        this.config.saveShops();
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
        this.addShop(Material.QUARTZ, 100);

        this.addShop(Material.DIAMOND_BLOCK, 900);
        this.addShop(Material.IRON_BLOCK, 900);
        this.addShop(Material.COAL_BLOCK, 900);
        this.addShop(Material.LAPIS_BLOCK, 900);
        this.addShop(Material.GOLD_BLOCK, 900);
        this.addShop(Material.REDSTONE_BLOCK, 900);
        this.addShop(Material.NETHERITE_BLOCK, 900);
        this.addShop(Material.QUARTZ_BLOCK, 900);

        this.addShop(Material.OBSIDIAN, 200);
        this.addShop(Material.CRYING_OBSIDIAN, 200);

        this.addShop(Material.IRON_NUGGET, 50);
        this.addShop(Material.GOLD_NUGGET, 50);

        this.addShop(Material.NETHERITE_SCRAP, 50);
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
