package org.cyci.mc.oldenchants;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.cyci.mc.oldenchants.listeners.EnchantListener;

import java.util.ArrayList;
import java.util.logging.Logger;

public final class OldEnchants extends JavaPlugin {
    private static Logger log;
    public ArrayList<EnchantingInventory> inventories;

    public void onLoad() {
        log = getLogger();
        log.info("Running on Minecraft Version " + getServer().getVersion());
        this.inventories = new ArrayList<>();
    }

    public void onDisable() {
        for (EnchantingInventory ei : this.inventories)
            ei.setItem(1, null);
        this.inventories = null;
    }

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents((Listener)new EnchantListener(this), (Plugin)this);
    }

    public static Logger getPluginLog() {
        return log;
    }
}
