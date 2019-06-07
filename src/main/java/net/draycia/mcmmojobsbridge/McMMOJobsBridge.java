package net.draycia.mcmmojobsbridge;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class McMMOJobsBridge extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            Class.forName("com.gamingmesh.jobs.api.JobsPrePaymentEvent");
        } catch (ClassNotFoundException e){
            getLogger().warning("This plugin requires a version of Jobs with JobsPrePaymentEvent! Please give Jobs an update.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPI.registerExpansion(new MJBPlaceholders(this));
        }

        getServer().getPluginManager().registerEvents(new JobsListener(this), this);
        getServer().getPluginManager().registerEvents(new McMMOListener(this), this);

        getCommand("mjb").setExecutor(new MJBCommand(this));
    }

    // https://rosettacode.org/wiki/Map_range#Java
    static double mapRange(float inputMin, float inputMax, double outputMin, double outputMax, float input){
        return outputMin + ((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin);
    }
}
