package com.corentinlg.transistors_components.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum ChannelMode implements StringIdentifiable {
  NMOS("nmos"),
  PMOS("pmos");

  private final String name;

  ChannelMode(String name) {
    this.name = name;
  }

  @Override
  public String asString() {
    return this.name;
  }
}