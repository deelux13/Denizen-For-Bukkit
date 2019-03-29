package net.aufdemrand.denizen.utilities.depends;

import net.citizensnpcs.Citizens;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;


public class Depends {

    public static Citizens citizens = null;

    public static Economy economy = null;
    public static Permission permissions = null;
    public static Chat chat = null;

    public void initialize() {
        setupEconomy();
        setupPermissions();
        setupChat();
        setupCitizens();
    }

    private boolean setupEconomy() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null || !plugin.isEnabled()) {
            return false;
        }
        try {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            economy = rsp.getProvider();
        }
        catch (Exception e) {
        }
        return economy != null;
    }

    private boolean setupChat() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null || !plugin.isEnabled()) {
            return false;
        }
        try {
            RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
            chat = rsp.getProvider();
        }
        catch (Exception e) {
        }
        return chat != null;
    }

    private boolean setupPermissions() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null || !plugin.isEnabled()) {
            return false;
        }
        try {
            RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            permissions = rsp.getProvider();
        }
        catch (Exception e) {
        }
        return permissions != null;
    }

    private boolean setupCitizens() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Citizens");
        if (plugin == null || !plugin.isEnabled()) {
            return false;
        }
        try {
            citizens = (Citizens) Bukkit.getServer().getPluginManager().getPlugin("Citizens");
        }
        catch (Exception e) {
        }
        return citizens != null;
    }
}
