package org.cyci.mc.oldenchants.listeners;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.cyci.mc.oldenchants.OldEnchants;

import java.util.*;

/**
 * @author - Phil
 * @project - ENCHANTING
 * @website - https://cyci.org
 * @email - staff@cyci.org
 * @created Wed - 21/Jun/2023 - 8:49 PM
 */
public class EnchantListener implements Listener {
    private OldEnchants plugin;

    private Set<UUID> resetLock = new HashSet<>();

    private Map<UUID, Integer> debt = new HashMap<>();


    private ItemStack lapis;
    public EnchantListener(OldEnchants plugin) {
        this.plugin = plugin;
        this.lapis = new ItemStack(Material.LAPIS_LAZULI);
        this.lapis.setAmount(64);
    }
    @EventHandler
    public void openInventoryEvent(InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, this.lapis);
            this.plugin.inventories.add((EnchantingInventory)e
                    .getInventory());
        }
    }

    @EventHandler
    public void closeInventoryEvent(InventoryCloseEvent e) {
        if (e.getInventory() instanceof EnchantingInventory &&
                this.plugin.inventories.contains(e
                        .getInventory())) {
            e.getInventory().setItem(1, null);
            this.plugin.inventories.remove(e
                    .getInventory());
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof EnchantingInventory &&
                this.plugin.inventories.contains(e
                        .getInventory()) &&
                e.getSlot() == 1)
            e.setCancelled(true);
    }

    @EventHandler
    public void enchantItemEvent(EnchantItemEvent e) {
        if (this.plugin.inventories.contains(e
                .getInventory()))
            e.getInventory().setItem(1, this.lapis);
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent ev) {
        if (ev.getClickedInventory() == null || ev
                .getClickedInventory().getType() != InventoryType.ENCHANTING)
            return;
        this.resetLock.remove(ev.getWhoClicked().getUniqueId());
    }

    @EventHandler
    public void invCloseEvent(InventoryCloseEvent ev) {
        this.resetLock.remove(ev.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void commitEnchant(EnchantItemEvent ev) {
        Player player = ev.getEnchanter();
        int buffer = ev.whichButton() + 1;
        if (ev.getExpLevelCost() <= buffer)
            return;
        int expLoss = ev.getExpLevelCost() - buffer;
        this.resetLock.remove(ev.getEnchanter().getUniqueId());
        this.debt.put(ev.getEnchanter().getUniqueId(), expLoss);
    }

    @EventHandler
    public void logout(PlayerQuitEvent ev) {
        this.resetLock.remove(ev.getPlayer().getUniqueId());
    }

    @EventHandler
    public void kick(PlayerKickEvent ev) {
        this.resetLock.remove(ev.getPlayer().getUniqueId());
    }

    @EventHandler
    public void prepEnchant(PrepareItemEnchantEvent ev) {
        UUID id = ev.getEnchanter().getUniqueId();
        if (this.resetLock.contains(id))
            return;
        if (this.debt.containsKey(id)) {
            ev.getEnchanter().giveExpLevels(-(Integer) this.debt.get(id));
            this.debt.remove(id);
        }
        this.resetLock.add(id);
    }
}
