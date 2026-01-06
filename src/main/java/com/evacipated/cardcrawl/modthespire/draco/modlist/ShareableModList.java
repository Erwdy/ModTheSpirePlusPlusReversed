package com.evacipated.cardcrawl.modthespire.draco.modlist;

import java.io.Serializable;

public abstract class ShareableModList implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String displayName;
  
  private String id;
  
  public void SetNameAndId(String displayName, String id) {
    this.displayName = displayName;
    this.id = id;
  }
  
  public String GetDisplayName() {
    return this.displayName;
  }
  
  public String GetId() {
    return this.id;
  }
}
