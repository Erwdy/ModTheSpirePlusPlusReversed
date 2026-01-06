package com.evacipated.cardcrawl.modthespire.draco.ui;

import com.evacipated.cardcrawl.modthespire.draco.ui.objects.SettingsWindow;
import com.evacipated.cardcrawl.modthespire.draco.util.MtSPPConfigManager;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

public class ThemeManager {
  public static void loadDefaultTheme() {
    try {
      String defaultTheme = MtSPPConfigManager.MTSPP_CONFIG.getString(SettingsWindow.settings_defaultTheme);
      if (defaultTheme != null) {
        try {
          UIManager.setLookAndFeel((LookAndFeel)Class.forName(defaultTheme).newInstance());
        } catch (ClassNotFoundException|InstantiationException|javax.swing.UnsupportedLookAndFeelException|IllegalAccessException e) {
          UIManager.setLookAndFeel((LookAndFeel)new FlatLightLaf());
        } 
      } else {
        UIManager.setLookAndFeel((LookAndFeel)new FlatLightLaf());
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
}
