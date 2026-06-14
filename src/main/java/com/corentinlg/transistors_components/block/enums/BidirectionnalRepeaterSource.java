package com.corentinlg.transistors_components.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BidirectionnalRepeaterSource implements StringIdentifiable {
  A("a"),
  B("b");

  private final String name;

  BidirectionnalRepeaterSource(String name) {
    this.name = name;
  }

  @Override
  public String asString() {
    return this.name;
  }
}