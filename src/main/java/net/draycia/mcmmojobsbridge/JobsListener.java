package net.draycia.mcmmojobsbridge;

import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class JobsListener implements Listener {
    private McMMoJobsBridge main;

    JobsListener(McMMoJobsBridge main) {
        this.main = main;
    }

    @EventHandler
    public void onJobsExpGain(JobsPrePaymentEvent event) {
        if (event.getAmount() == 0 && event.getPoints() == 0) return; // Unsure if this will ever be true

        McMMOPlayer mmoPlayer = UserManager.getPlayer(event.getPlayer().getPlayer());
        if (mmoPlayer == null) return;

        if (event.getJob() == null) {
            System.out.println("Job is null!");
            return;
        } else {
            System.out.println("Job is not null!");
        }

        String jobName = event.getJob().getName();

        ConfigurationSection section = main.getConfig().getConfigurationSection("Jobs." + jobName);
        if (section == null) return;

        List<String> skills = section.getStringList("Skills");
        if (skills.isEmpty()) return;

        String targetType = section.getString("TargetType");

        int skillMin = section.getInt("SkillMin");
        int skillMax = section.getInt("SkillMax");
        int multMin = section.getInt("MultMin");
        int multMax = section.getInt("MultMax");

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

        double multiplier = mapRange(skillMin, skillMax, multMin, multMax, targetLevel);

        event.setAmount(event.getAmount() * multiplier);
        event.setPoints(event.getPoints() * multiplier);
    }

    private static double mapRange(double a1, double a2, double b1, double b2, double s){
        return b1 + ((s - a1)*(b2 - b1))/(a2 - a1);
    }
}
