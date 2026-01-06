package com.evacipated.cardcrawl.modthespire.draco.modcompatibility;

import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ModCompatibilityManager {
  private static ArrayList<MtSModIntegration> modIntegrations = new ArrayList<>();
  
  public static void Register(MtSModIntegration mod) {
    modIntegrations.add(mod);
  }
  
  public static boolean AnyCompatFound() {
    return !modIntegrations.isEmpty();
  }
  
  public static JPanel MakeSettingsPanel() {
    JPanel modCompatibility = new JPanel(new FlowLayout(3));
    if (!AnyCompatFound()) {
      modCompatibility.add(new JLabel("No mods integrating with MtS++ found."));
      return modCompatibility;
    } 
    for (MtSModIntegration i : modIntegrations)
      modCompatibility.add(i.makeSettingsPanel()); 
    return modCompatibility;
  }
}
