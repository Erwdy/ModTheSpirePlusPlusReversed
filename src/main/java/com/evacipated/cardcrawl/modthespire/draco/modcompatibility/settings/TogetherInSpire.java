package com.evacipated.cardcrawl.modthespire.draco.modcompatibility.settings;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.draco.modcompatibility.MtSModIntegration;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.RaidUserProfile;
import com.evacipated.cardcrawl.modthespire.draco.util.MtSPPConfigManager;
import java.awt.event.ActionEvent;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TogetherInSpire extends MtSModIntegration {
  private static String settings_raidintegration = "tis_raidintegration";
  
  public TogetherInSpire() {
    if (IsRaidTime() && MtSPPConfigManager.MTSPP_CONFIG.getBool(settings_raidintegration)) {
      RaidUserProfile raidUserProfile = new RaidUserProfile();
      Loader.ex.dProfilesList.insertItemAt(raidUserProfile, Loader.ex.dProfilesList.getItemCount());
    } 
  }
  
  public static boolean IsRaidTime() {
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
    ZonedDateTime lastSaturday = now.with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));
    if (now.toLocalDate().equals(lastSaturday.toLocalDate())) {
      LocalTime targetTime = LocalTime.of(13, 0);
      Duration duration = Duration.between(now.toLocalTime(), targetTime);
      return (duration.toMinutes() <= 30L && duration.toMinutes() >= -120L);
    } 
    return false;
  }
  
  public JPanel makeSettingsPanel() {
    JPanel settingsPanel = new JPanel();
    settingsPanel.setLayout(new BoxLayout(settingsPanel, 1));
    JLabel header = new JLabel("Together in Spire");
    header.setFont(header.getFont().deriveFont(1));
    settingsPanel.add(header);
    final JCheckBox raidCheckbox = new JCheckBox("Enable raid integration");
    raidCheckbox.setSelected(MtSPPConfigManager.MTSPP_CONFIG.getBool(settings_raidintegration));
    raidCheckbox.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            MtSPPConfigManager.MTSPP_CONFIG.setBool(TogetherInSpire.settings_raidintegration, raidCheckbox.isSelected());
            try {
              MtSPPConfigManager.MTSPP_CONFIG.save();
            } catch (Exception exception) {}
          }
        });
    settingsPanel.add(raidCheckbox);
    return settingsPanel;
  }
}
