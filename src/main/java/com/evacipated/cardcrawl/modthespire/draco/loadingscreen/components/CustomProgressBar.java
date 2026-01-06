package com.evacipated.cardcrawl.modthespire.draco.loadingscreen.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;

public class CustomProgressBar extends JProgressBar {
  public CustomProgressBar() {
    super(0, 100);
    setOpaque(false);
    setBorder(BorderFactory.createLineBorder(Color.WHITE));
    setAlignmentX(0.5F);
  }
  
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int width = getWidth();
    int height = getHeight();
    int inset = 1;
    int barWidth = (int)(getValue() / getMaximum() * (width - inset * 2));
    g2d.setColor(Color.WHITE);
    g2d.fillRect(inset, inset, barWidth, height - inset * 2);
  }
}
