package com.ashkiano.infinitestew;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class InfiniteStew extends JavaPlugin implements Listener {

    private String configLore;
    private String permissionNode;

    @Override
    public void onEnable() {
        this.getCommand("infinitestew").setExecutor(new StewCommand());
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        // Initialize config
        this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Load lore from config
        configLore = this.getConfig().getString("stew-lore");

        // Load permission from config
        permissionNode = this.getConfig().getString("command-permission");

        Metrics metrics = new Metrics(this, 19504);
    }

    public class StewCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // Check permission
                if (permissionNode != null && !player.hasPermission(permissionNode)) {
                    player.sendMessage("You don't have permission to use this command.");
                    return true;
                }

                ItemStack stew = new ItemStack(Material.RABBIT_STEW);
                ItemMeta meta = stew.getItemMeta();

                if (meta != null) {
                    List<String> lore = new ArrayList<>();
                    lore.add(configLore);
                    meta.setLore(lore);
                    stew.setItemMeta(meta);
                }

                player.getInventory().addItem(stew);
                player.sendMessage("You've received an infinite rabbit stew!");
            }

            return true;
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item.getType() == Material.RABBIT_STEW && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore() && meta.getLore().contains(configLore)) {
                event.setCancelled(true);

                int newFoodLevel = Math.min(player.getFoodLevel() + 10, 20);
                player.setFoodLevel(newFoodLevel);
                float newSaturation = player.getSaturation() + 12F;
                player.setSaturation(newSaturation);

                double newHealth = Math.min(player.getHealth() + 3.0D, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                player.setHealth(newHealth);
            }
        }
    }
}