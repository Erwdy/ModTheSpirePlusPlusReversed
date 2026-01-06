package com.evacipated.cardcrawl.modthespire.draco.loadingscreen;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.draco.loadingscreen.components.CustomProgressBar;
import com.evacipated.cardcrawl.modthespire.draco.mods.DModSelectWindow;
import com.evacipated.cardcrawl.modthespire.draco.ui.DUIHelp;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.SettingsWindow;
import com.evacipated.cardcrawl.modthespire.draco.util.MtSPPConfigManager;
import com.evacipated.cardcrawl.modthespire.ui.ModSelectWindow;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LoadingScreenBuilder {
  public static final Icon ICON_LOADINGMONSTER = new ImageIcon(ModSelectWindow.class.getResource("/assets/loading/monster.gif"));
  
  public static final Icon ICON_LOADINGKEYS = new ImageIcon(ModSelectWindow.class.getResource("/assets/loading/keys.gif"));
  
  public static JLabel monsterLabel;
  
  public static CustomProgressBar progressBar;
  
  public static JLabel loadingText;
  
  private static Integer monsterTargetX;
  
  private static boolean monsterTargetXFinished;
  
  public static JScrollPane logWindow;
  
  private static String[] defaultLoadingPhrases = new String[] { 
      "Sharpening Ironclad's sword", "Polishing the Silent’s daggers", "Plugging in the Defect to charge", "Putting Lagavulin to sleep", "Shuffling cards", "Scattering gold", "Drawing the map", "Angering the Gremlin Nob", "Brewing some potions", "Cursing the curses", 
      "Vacuuming merchant's carpet", "Un-awakening The Awakened One", "Charging Neow’s blessings", "Feeding the Maw", "Calibrating the Time Eater’s clock", "Stoking the fire for the Burning Elite", "Assembling the Automaton", "Summoning the Darklings", "Stirring the Slime Boss", "Inflating the Byrds", 
      "Carving the Giant Head", "Activating the Orb Walkers", "Dusting off the Book of Stabbing", "Polishing the Golden Idol" };
  
  private static ArrayList<String> allLoadingPhrases;
  
  public static void buildLoadingScreen(JFrame frame) {
    initializeLoadingPhrases();
    logWindow = DUIHelp.makeLogWindow();
    logWindow.setVisible(false);
    frame.getContentPane().removeAll();
    frame.getContentPane().setBackground(Color.BLACK);
    JLabel loadingKeys = new JLabel(ICON_LOADINGKEYS);
    frame.getContentPane().add(loadingKeys, "First");
    frame.getContentPane().add(MakeLoadingBar(), "South");
    if (MtSPPConfigManager.MTSPP_CONFIG.getBool(SettingsWindow.settings_noloadingscreen))
      showLogWindow(); 
  }
  
  private static void initializeLoadingPhrases() {
    allLoadingPhrases = new ArrayList<>();
    allLoadingPhrases.addAll(Arrays.asList(defaultLoadingPhrases));
    for (File modFile : Loader.ex.modList.getCheckedMods()) {
      ModInfo mod = ModInfo.ReadModInfo(modFile);
      if (mod != null && mod.modInfoExtended != null)
        allLoadingPhrases.addAll(Arrays.asList(mod.modInfoExtended.loading_phrases)); 
    } 
  }
  
  private static JPanel MakeLoadingBar() {
    final JPanel southPanel = new JPanel();
    southPanel.setOpaque(false);
    southPanel.setLayout(new GridBagLayout());
    monsterLabel = new JLabel(ICON_LOADINGMONSTER);
    JPanel monsterPanel = new JPanel();
    monsterPanel.setLayout((LayoutManager)null);
    monsterPanel.add(monsterLabel);
    monsterPanel.setBackground(Color.BLACK);
    GridBagConstraints monsterPanelConstraints = new GridBagConstraints();
    monsterPanelConstraints.gridx = 10;
    monsterPanelConstraints.gridy = 10;
    monsterPanelConstraints.fill = 1;
    monsterPanel.setMinimumSize(monsterLabel.getMinimumSize());
    monsterPanel.setPreferredSize(monsterLabel.getPreferredSize());
    monsterLabel.setBounds(0, 0, 107, 75);
    monsterLabel.setAlignmentY(1.0F);
    monsterLabel.setLocation(-monsterLabel.getWidth(), 5);
    southPanel.add(monsterPanel, monsterPanelConstraints);
    GridBagConstraints southPanelConstraints = new GridBagConstraints();
    southPanelConstraints.gridx = 10;
    southPanelConstraints.gridy = -1;
    southPanelConstraints.anchor = 15;
    progressBar = new CustomProgressBar();
    progressBar.setPreferredSize(new Dimension(750, 50));
    progressBar.setMinimumSize(new Dimension(375, 25));
    progressBar.addChangeListener(new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            LoadingScreenBuilder.monsterTargetX = Integer.valueOf((int)(LoadingScreenBuilder.progressBar.getPercentComplete() * LoadingScreenBuilder.progressBar.getWidth()) + southPanel.getX() - LoadingScreenBuilder.monsterLabel.getWidth() / 2);
            if (LoadingScreenBuilder.progressBar.getPercentComplete() == 1.0D) {
              LoadingScreenBuilder.monsterTargetX = Integer.valueOf(LoadingScreenBuilder.monsterTargetX.intValue() + LoadingScreenBuilder.monsterLabel.getWidth() / 2);
              LoadingScreenBuilder.monsterTargetXFinished = true;
            } 
          }
        });
    Thread t = new Thread() {
        public void run() {
          super.run();
          while (true) {
            try {
              if (LoadingScreenBuilder.monsterTargetX != null && LoadingScreenBuilder.monsterLabel.getX() != LoadingScreenBuilder.monsterTargetX.intValue()) {
                LoadingScreenBuilder.monsterLabel.setLocation(LoadingScreenBuilder.monsterLabel.getX() + 1, LoadingScreenBuilder.monsterLabel.getY());
              } else if (LoadingScreenBuilder.monsterTargetXFinished) {
                break;
              } 
              Thread.sleep(10L);
            } catch (InterruptedException interruptedException) {}
          } 
        }
      };
    t.start();
    loadingText = new JLabel(" ");
    loadingText.setForeground(Color.WHITE);
    loadingText.setFont(DModSelectWindow.FONT_KREON);
    loadingText.setMinimumSize(new Dimension(200, 100));
    loadingText.setAlignmentX(0.5F);
    southPanel.add((Component)progressBar, southPanelConstraints);
    southPanel.add(Box.createRigidArea(new Dimension(0, 20)), southPanelConstraints);
    southPanel.add(loadingText, southPanelConstraints);
    southPanel.add(Box.createRigidArea(new Dimension(0, 50)), southPanelConstraints);
    southPanel.setMinimumSize(southPanel.getPreferredSize());
    return southPanel;
  }
  
  public static void showLogWindow() {
    logWindow.setVisible(true);
    Loader.ex.getContentPane().removeAll();
    Loader.ex.getContentPane().setBackground(Color.WHITE);
    Loader.ex.getContentPane().add(logWindow, "Center");
    Loader.ex.getContentPane().repaint();
    Loader.ex.getContentPane().revalidate();
  }
  
  public static void setLoadTextAndValue(int value) {
    if (value == 100) {
      loadingText.setText("Slaying the Spire!");
    } else {
      loadingText.setText(allLoadingPhrases.get((new Random()).nextInt(allLoadingPhrases.size())));
    } 
    progressBar.setValue(value);
  }
}
