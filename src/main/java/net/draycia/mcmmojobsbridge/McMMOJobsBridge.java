package net.draycia.mcmmojobsbridge;

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

        getServer().getPluginManager().registerEvents(new JobsListener(this), this);
        getServer().getPluginManager().registerEvents(new McMMOListener(this), this);

        getCommand("mjb").setExecutor(new MJBCommand(this));
    }

    static double mapRange(double a1, double a2, double b1, double b2, double s){
        return b1 + ((s - a1)*(b2 - b1))/(a2 - a1);
    }

    static float mapRange(float a1, float a2, float b1, float b2, float s){
        return b1 + ((s - a1)*(b2 - b1))/(a2 - a1);
    }
}
