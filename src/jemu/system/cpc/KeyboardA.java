package jemu.system.cpc;

import java.awt.event.*;

/**
* Title:        JavaCPC
* Description:  The Java CPC Emulator
* Copyright:    Copyright (c) 2006-2008
* Company:
* @author:      Markus, Richard (JEMU)
* @version 1.3
*/

public class KeyboardA extends Keyboard {

  protected static final int[] KEY_MAP = {
    // Row 0
    KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_F9,
    KeyEvent.VK_F6, KeyEvent.VK_F3, KeyEvent.VK_END, KeyEvent.VK_DECIMAL,

    // Row 1
    KeyEvent.VK_LEFT, KeyEvent.VK_INSERT, KeyEvent.VK_F7, KeyEvent.VK_F8,
    KeyEvent.VK_F5, KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F12,

    // Row 2
    KeyEvent.VK_DELETE, KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_ENTER,
    KeyEvent.VK_CLOSE_BRACKET, KeyEvent.VK_F4, KeyEvent.VK_SHIFT,
    KeyEvent.VK_BACK_SLASH, KeyEvent.VK_CONTROL,

    // Row 3
    KeyEvent.VK_EQUALS, KeyEvent.VK_MINUS, KeyEvent.VK_OPEN_BRACKET, KeyEvent.VK_P,
    KeyEvent.VK_QUOTE, KeyEvent.VK_SEMICOLON, KeyEvent.VK_SLASH, KeyEvent.VK_PERIOD,

    // Row 4
    KeyEvent.VK_0, KeyEvent.VK_9, KeyEvent.VK_O, KeyEvent.VK_I,
    KeyEvent.VK_L, KeyEvent.VK_K, KeyEvent.VK_M, KeyEvent.VK_COMMA,

    // Row 5
    KeyEvent.VK_8, KeyEvent.VK_7, KeyEvent.VK_U, KeyEvent.VK_Y,
    KeyEvent.VK_H, KeyEvent.VK_J, KeyEvent.VK_N, KeyEvent.VK_SPACE,

    // Row 6
    KeyEvent.VK_6, KeyEvent.VK_5, KeyEvent.VK_R, KeyEvent.VK_T,
    KeyEvent.VK_G, KeyEvent.VK_F, KeyEvent.VK_B, KeyEvent.VK_V,

    // Row 7
    KeyEvent.VK_4, KeyEvent.VK_3, KeyEvent.VK_E, KeyEvent.VK_W,
    KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_C, KeyEvent.VK_X,

    // Row 8
    KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_ESCAPE, KeyEvent.VK_Q,
    KeyEvent.VK_TAB, KeyEvent.VK_A, KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_Z,

    // Row 9
    KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD6,
    KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD0, -1, KeyEvent.VK_BACK_SPACE
  };
 
  public void setKeyMappings() {
    addKeyMappings(KEY_MAP);

    // For MS JVM, these keys have different codes
    addKeyMapping(0xba,5,3);  // VK_SEMICOLON = 0xba
    addKeyMapping(0xbc,7,4);  // VK_COMMA = 0xbc
    addKeyMapping(0xbd,1,3);  // VK_MINUS = 0xbd
    addKeyMapping(0xbe,7,3);  // VK_PERIOD = 0xbe
    addKeyMapping(0xdb,2,3);  // VK_OPEN_BRACKET = 0xdb
    addKeyMapping(0xdd,3,2);  // VK_CLOSE_BRACKET = 0xdd
  }

}