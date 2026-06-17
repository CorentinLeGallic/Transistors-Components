package com.corentinlg.transistors_components.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BidirectionalRepeaterSource implements StringIdentifiable {
  A("a"),
  B("b");

  private final String name;

  BidirectionalRepeaterSource(String name) {
    this.name = name;
  }

  @Override
  public String asString() {
    return this.name;
  }
}