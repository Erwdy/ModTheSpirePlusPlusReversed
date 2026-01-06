package com.evacipated.cardcrawl.modthespire.draco.util;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class StacktraceHelpers {
  public static ArrayList<ModInfo> getModsFromStacktrace(Throwable exception) {
    ArrayList<String> stacktraceClasses = recursivelyGetStacktraceClasses(exception);
    ArrayList<ModInfo> modsInStacktrace = new ArrayList<>();
    for (String className : stacktraceClasses) {
      try {
        Class<?> cls = Class.forName(className);
        URL modUrl = cls.getProtectionDomain().getCodeSource().getLocation();
        if (modUrl != null)
          Arrays.<ModInfo>stream(Loader.MODINFOS).filter(m -> m.jarURL.equals(modUrl)).findFirst().ifPresent(modInfo -> {
                if (!modInfo.ID.equals("basemod"))
                  modsInStacktrace.add(modInfo); 
              }); 
      } catch (Exception exception1) {}
    } 
    return modsInStacktrace;
  }
  
  public static ArrayList<String> recursivelyGetStacktraceClasses(Throwable exception) {
    ArrayList<String> toReturn = new ArrayList<>();
    for (StackTraceElement stackTraceElement : exception.getStackTrace())
      toReturn.add(stackTraceElement.getClassName()); 
    for (Throwable suppressed : exception.getSuppressed()) {
      if (suppressed != null)
        toReturn.addAll(recursivelyGetStacktraceClasses(suppressed)); 
    } 
    Throwable cause = exception.getCause();
    if (cause != null)
      toReturn.addAll(recursivelyGetStacktraceClasses(cause)); 
    return toReturn;
  }
}
