package picc;

import java.awt.Cursor;

public enum States
{
  NORTH_WEST(new Cursor(6)), 
  NORTH(new Cursor(8)), 
  NORTH_EAST(new Cursor(7)), 
  EAST(new Cursor(11)), 
  SOUTH_EAST(new Cursor(5)), 
  SOUTH(new Cursor(9)), 
  SOUTH_WEST(new Cursor(4)), 
  WEST(new Cursor(10)), 
  MOVE(new Cursor(13)), 
  DEFAULT(new Cursor(0));

  private Cursor cs;

  private States(Cursor cs) { this.cs = cs; }

  public Cursor getCursor() {
    return this.cs;
  }
}