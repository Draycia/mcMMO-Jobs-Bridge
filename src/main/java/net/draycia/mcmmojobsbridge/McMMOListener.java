package net.draycia.mcmmojobsbridge;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class McMMOListener implements Listener {
    private McMMOJobsBridge main;

    McMMOListener(McMMOJobsBridge main) {
        this.main = main;
    }

    @EventHandler
    public void onMcmmoExp(McMMOPlayerXpGainEvent event) {
        if (!main.getConfig().getBoolean("ModifySkills")) return;

        boolean debug = main.getConfig().getBoolean("DebugMode");

        if (event.getRawXpGained() == 0) {
            if (debug) {
                main.getLogger().info("====================================================");
                main.getLogger().info("mcMMO - XP gained is set to 0!");
            }

            return;
        }

        double multiplier = getMultiplier(main, debug, event.getPlayer().getPlayer(), event.getSkill().getName());

        if (debug) {
            main.getLogger().info("Debug - mcMMO: [" + event.getSkill().getName() + "], OldXP: [" + event.getRawXpGained() + "], NewXP: ["
                    + event.getRawXpGained() * multiplier + "], Multiplier: [" + String.format("%.2f", multiplier) + "], Player: ["
                    + event.getPlayer().getName() + "]");
            main.getLogger().info("====================================================");
        }

        event.setRawXpGained((float)(event.getRawXpGained() * multiplier));
    }

    public static double getMultiplier(McMMOJobsBridge main, boolean debug, Player player, String skillName) {
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            if (debug) main.getLogger().info("mcMMO player is not loaded! This is not an issue, but plugin multipliers will not apply until it is loaded!");
            return 0f;
        }

        ConfigurationSection section = main.getConfig().getConfigurationSection("Skills." + skillName);
        if (section == null) {
            if (debug) main.getLogger().info("This skill is not listed in this plugin's config! Not applying multiplier.");
            return 0f;
        }

        List<String> jobs = section.getStringList("Jobs");
        if (jobs.isEmpty()) {
            if (debug) main.getLogger().info("This skill is configured but no Jobs are listed for this job! Not applying multiplier.");
            return 0f;
        }

        String targetType = section.getString("TargetType");

        int levelMin = section.getInt("LevelMin");
        int levelMax = section.getInt("LevelMax");
        double multMin = section.getDouble("MultMin");
        double multMax = section.getDouble("MultMax");

        int targetLevel = 0;
        int totalLevel = 0;
        int highestLevel = 0;

        for (String job : jobs) {
            Job jobInstance = Jobs.getJob(job);
            if (jobInstance == null) continue;

            JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player.getUniqueId());
            if (jobsPlayer == null) continue;

            int level = jobsPlayer.getJobProgression(jobInstance).getLevel();

            if (level > 0) {
                if (targetType == null || targetType.equalsIgnoreCase("Average")) {
                    totalLevel += level;
                } else if (targetType.equalsIgnoreCase("FirstNonZero")) {
                    targetLevel = level;
                    break;
                } else if (targetType.equalsIgnoreCase("Highest")) {
                    if (level > highestLevel) {
                        highestLevel = level;
                    }
                }
            }
        }

        if (targetType == null || targetType.equalsIgnoreCase("Average")) {
            targetLevel = totalLevel / jobs.size();
        } else if (targetType.equalsIgnoreCase("Highest")) {
            targetLevel = highestLevel;
        }

        if (targetLevel == 0) {
            targetLevel = section.getInt("EnforceMinimum");
        }

        if (debug) {
            main.getLogger().info("LevelMin: [" + levelMin + "], LevelMax: [" + levelMax + "], MultMin: [" +
                    multMin + "], MultMax: [" + multMax + "], TargetLevel: [" + targetLevel + "]");
        }

        return McMMOJobsBridge.mapRange(levelMin, levelMax, multMin, multMax, targetLevel);
    }
}
