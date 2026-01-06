package com.evacipated.cardcrawl.modthespire.draco.metrics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PerformanceMonitor {
  static LinkedHashMap<String, Long> trackedData = new LinkedHashMap<>();
  
  static String currentTracker;
  
  static long trackStartTime;
  
  public static void setTracker(String tracker) {
    if (Objects.equals(currentTracker, tracker))
      return; 
    if (currentTracker != null) {
      long executionDuration = System.nanoTime() - trackStartTime;
      if (trackedData.containsKey(currentTracker))
        executionDuration += ((Long)trackedData.get(currentTracker)).longValue(); 
      trackedData.put(currentTracker, Long.valueOf(executionDuration));
    } 
    currentTracker = tracker;
    trackStartTime = System.nanoTime();
  }
  
  public static void print() {
    System.out.println("Printing out performance logs.");
    for (Map.Entry<String, Long> entry : trackedData.entrySet())
      System.out.println((String)entry.getKey() + " took " + TimeUnit.MILLISECONDS.convert(((Long)entry.getValue()).longValue(), TimeUnit.NANOSECONDS) + " milliseconds to complete."); 
  }
  
  public static void reset() {
    trackedData.clear();
  }
}
