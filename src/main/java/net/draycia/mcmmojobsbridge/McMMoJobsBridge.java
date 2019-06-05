package net.draycia.mcmmojobsbridge;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class McMMoJobsBridge extends JavaPlugin {

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

        getServer().getPluginManager().registerEvents(new JobsListener(this), this);

        getCommand("mjb").setExecutor(new MJBCommand(this));
    }
}
