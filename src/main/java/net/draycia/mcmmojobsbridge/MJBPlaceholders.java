package net.draycia.mcmmojobsbridge;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class MJBPlaceholders extends PlaceholderExpansion {
    private McMMOJobsBridge main;

    MJBPlaceholders(McMMOJobsBridge main) {
        this.main = main;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "mjb";
    }

    @Override
    public String getAuthor() {
        return "Draycia (Vicarious)";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.startsWith("mcmmo_multiplier_")) {
            String[] args = identifier.split("_");
            if (args.length < 3) return null;

            return String.format("%.2f", McMMOListener.getMultiplier(main, false, player, args[2]));
        } else if (identifier.equalsIgnoreCase("jobs_multiplier")) {
            String[] args = identifier.split("_");
            if (args.length < 3) return null;

            return String.format("%.2f", JobsListener.getMultiplier(main, false, player, args[2]));
        } else {
            return null;
        }
    }
}
