package com.evacipated.cardcrawl.modthespire.draco.ui;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.draco.util.StacktraceHelpers;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CrashExtensions {
  public static void showCrashReportToolbar(Throwable exception) {
    ArrayList<String> urlsToReportTo = new ArrayList<>();
    ArrayList<ModInfo> modsInStacktrace = StacktraceHelpers.getModsFromStacktrace(exception);
    for (ModInfo modInStacktrace : modsInStacktrace) {
      if (modInStacktrace.modInfoExtended != null);
    } 
    Loader.ex.getContentPane().add(makeCrashReportPanel(urlsToReportTo), "South");
    Loader.ex.repaint();
    Loader.ex.revalidate();
  }
  
  private static JPanel makeCrashReportPanel(ArrayList<String> urlsToReportTo) {
    JPanel panel = new JPanel(new BorderLayout());
    JButton closeButton = new JButton("CLOSE");
    closeButton.setBackground(Color.WHITE);
    closeButton.addActionListener(e -> Loader.closeWindow());
    panel.add(closeButton, "West");
    JButton openLogInExplorer = new JButton("OPEN LOG");
    openLogInExplorer.setBackground(Color.WHITE);
    openLogInExplorer.addActionListener(e -> {
          try {
            File file = new File(ModSelectWindowExtensions.LOG_FILE);
            if (!file.exists())
              return; 
            Desktop.getDesktop().open(file);
          } catch (IOException iOException) {}
        });
    panel.add(openLogInExplorer, "East");
    if (!urlsToReportTo.isEmpty()) {
      JButton sendToDevButton = new JButton("SEND LOG TO MOD DEVELOPER(S)");
      sendToDevButton.setBackground(Color.BLUE);
      sendToDevButton.setForeground(Color.WHITE);
      sendToDevButton.addActionListener(e -> {
          
          });
      panel.add(sendToDevButton, "East");
    } 
    return panel;
  }
}
