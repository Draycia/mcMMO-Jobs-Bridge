package net.draycia.mcmmojobsbridge;

import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class JobsListener implements Listener {
    private McMMOJobsBridge main;

    JobsListener(McMMOJobsBridge main) {
        this.main = main;
    }

    @EventHandler
    public void onJobsExpGain(JobsPrePaymentEvent event) {
        if (!main.getConfig().getBoolean("ModifyJobs")) return;

        boolean debug = main.getConfig().getBoolean("DebugMode");

        if (event.getAmount() == 0 && event.getPoints() == 0) {
            if (debug) {
                main.getLogger().info("====================================================");
                main.getLogger().info("Income and Points are set to 0! Is your job configured correctly?");
            }

            return;
        }

        double multiplier = getMultiplier(main, debug, event.getPlayer().getPlayer(), event.getJob().getName());

        if (debug) {
            main.getLogger().info("Debug - Job: [" + event.getJob().getName() + "], OldAmount: [" + event.getAmount() + "], NewAmount: ["
                    + event.getAmount() * multiplier + "], OldPoints: [" + event.getPoints() + "], NewPoints: [" + event.getPoints() * multiplier
                    + "], Multiplier: [" + String.format("%.2f", multiplier) + "], Player: [" + event.getPlayer().getName() + "]");
            main.getLogger().info("====================================================");
        }

        event.setAmount(event.getAmount() * multiplier);
        event.setPoints(event.getPoints() * multiplier);
    }

    public static double getMultiplier(McMMOJobsBridge main, boolean debug, Player player, String jobName) {
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            if (debug) main.getLogger().info("mcMMO player is not loaded! This is not an issue, but plugin multipliers will not apply until it is loaded!");
            return 0d;
        }

        ConfigurationSection section = main.getConfig().getConfigurationSection("Jobs." + jobName);
        if (section == null) {
            if (debug) main.getLogger().info("This job is not listed in this plugin's config! Not applying multiplier.");
            return 0d;
        }

        List<String> skills = section.getStringList("Skills");
        if (skills.isEmpty()) {
            if (debug) main.getLogger().info("This job is configured but no mcMMO skills are listed for this job! Not applying multiplier.");
            return 0d;
        }

        String targetType = section.getString("TargetType");

        int skillMin = section.getInt("SkillMin");
        int skillMax = section.getInt("SkillMax");
        double multMin = section.getDouble("MultMin");
        double multMax = section.getDouble("MultMax");

        int targetLevel = 0;
        int totalLevel = 0;
        int highestLevel = 0;

        for (String skill : skills) {
            PrimarySkillType skillType = PrimarySkillType.getSkill(skill);
            if (skillType == null) continue;

            int level = mmoPlayer.getSkillLevel(skillType);

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
            targetLevel = totalLevel / skills.size();
        } else if (targetType.equalsIgnoreCase("Highest")) {
            targetLevel = highestLevel;
        }

        if (targetLevel == 0) {
            targetLevel = section.getInt("EnforceMinimum");
        }

        if (debug) {
            main.getLogger().info("SkillMin: [" + skillMin + "], SkillMax: [" + skillMax + "], MultMin: [" +
                    multMin + "], MultMax: [" + multMax + "], TargetLevel: [" + targetLevel + "]");
        }

        return McMMOJobsBridge.mapRange(skillMin, skillMax, multMin, multMax, targetLevel);
    }
}
