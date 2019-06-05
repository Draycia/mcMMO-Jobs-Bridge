package net.draycia.mcmmojobsbridge;

import org.bukkit.plugin.java.JavaPlugin;

public final class McMMoJobsBridge extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new JobsListener(this), this);
    }
}
