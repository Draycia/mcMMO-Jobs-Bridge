package net.draycia.mcmmojobsbridge;

import org.bukkit.plugin.java.JavaPlugin;

public final class McMMoJobsBridge extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new JobsListener(this), this);

        getCommand("mjb").setExecutor(new MJBCommand(this));
    }
}
