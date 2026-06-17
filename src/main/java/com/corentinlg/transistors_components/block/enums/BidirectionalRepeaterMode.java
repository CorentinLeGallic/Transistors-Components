package com.corentinlg.transistors_components.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BidirectionalRepeaterMode implements StringIdentifiable {
  DISABLED("0"),
  POWERED_A("1"),
  POWERED_B("2");

  private final String name;

  BidirectionalRepeaterMode(String name) {
    this.name = name;
  }

  @Override
  public String asString() {
    return this.name;
  }
}