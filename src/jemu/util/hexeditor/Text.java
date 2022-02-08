package jemu.util.hexeditor;

/**
 * Title:        JEMU by cpc.devilmarkus.de / jemu.winape.net
 * Description:  JAVA emulator for Amstrad Homecomputers
 * Copyright:    Copyright (c) 2002-2008
 * Company:
 * @author       Devilmarkus
 * @version 5.8
 * 
 * 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class Text extends JPanel
    implements ChangeListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{

    static final Color ACTIVE_CURSOR;
    static final Color ACTIVE_SELECT = new Color(151, 151, 255);
    static final int MAX_FONT_SIZE = 73;
    static final int MIN_FONT_SIZE = 10;
    static final Color PANEL_COLOR;
    static final int PANEL_MARGIN = 10;
    static final Color SHADOW_CURSOR = new Color(102, 102, 102);
    static final Color SHADOW_SELECT = new Color(151, 151, 151);
    static final Color TEXT_COLOR;
    int charShifts[];
    int charWidths[];
    int cursorDot;
    int cursorMark;
    boolean cursorOnText;
    int lineAscent;
    int lineHeight;
    int maxWidth;
    int mousePressNibble;
    boolean mousePressOnText;
    int mouseTempNibble;
    boolean mouseTempOnText;
    int panelColumns;
    int panelDumpWidth;
    Font panelFont;
    String panelFontName;
    int panelFontSize;
    int panelHeight;
    int panelWidth;
    int panelOffset;
    int panelRows;

    public Text()
    {
        cursorDot = 0;
        cursorMark = 0;
        cursorOnText = false;
        lineAscent = -1;
        lineHeight = -1;
        maxWidth = -1;
        panelColumns = -1;
        panelDumpWidth = -1;
        panelFont = null;
        panelFontName = "";
        panelFontSize = -1;
        panelHeight = -1;
        panelOffset = 0;
        panelRows = -1;
        panelWidth = -1;
        charShifts = new int[127];
        charWidths = new int[127];
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    void adjustScrollBar()
    {
        panelOffset = Math.max(0, Math.min(panelOffset, ((HexEditor.nibbleCount / (panelDumpWidth * 2) - panelRows) + 1) * panelDumpWidth));
        panelOffset -= panelOffset % panelDumpWidth;
        HexEditor.textScroll.setValues(panelOffset / panelDumpWidth, panelRows, 0, HexEditor.nibbleCount / (panelDumpWidth * 2) + 1);
        HexEditor.textScroll.setBlockIncrement(Math.max(1, panelRows - 1));
        HexEditor.textScroll.setUnitIncrement(1);
    }

    void beginFile()
    {
        cursorDot = 0;
        cursorMark = 0;
        cursorOnText = false;
        panelDumpWidth = -1;
        panelOffset = 0;
        repaint();
    }

    void convertMouse(MouseEvent event)
    {
        mouseTempOnText = event.getX() > 10 + (int)(((double)(8 + panelDumpWidth * 3) + 2.7999999999999998D) * (double)maxWidth);
        int row = (event.getY() - 10) / lineHeight;
        row = Math.max(0, Math.min(row, panelRows - 1));
        int column;
        if(mouseTempOnText)
        {
            int x = event.getX() - 10 - (int)(((double)(8 + panelDumpWidth * 3) + 3.7999999999999998D) * (double)maxWidth);
            column = (x / maxWidth) * 2;
        } else
        {
            int x = event.getX() - 10 - (int)(9.5D * (double)maxWidth);
            column = (x / (maxWidth * 3)) * 2;
            column += (double)(x % (maxWidth * 3)) <= (double)maxWidth * 1.3D ? 0 : 1;
        }
        column = Math.max(0, Math.min(column, panelDumpWidth * 2));
        mouseTempNibble = (panelOffset + row * panelDumpWidth) * 2 + column;
        mouseTempNibble = Math.min(mouseTempNibble, HexEditor.nibbleCount);
    }

    void finishArrowKey(KeyEvent event)
    {
        if(!event.isShiftDown())
        {
            cursorMark = cursorDot;
        }
        limitCursorRange();
        makeVisible(cursorDot);
        repaint();
    }

    public void keyPressed(KeyEvent keyevent)
    {
    }

    public void keyReleased(KeyEvent event)
    {
        int key = event.getKeyCode();
        switch(key)
        {
        default:
            break;

        case 40: // '('
        case 225: 
            if(cursorOnText)
            {
                cursorDot += cursorDot % 2;
                cursorMark -= cursorMark % 2;
            }
            cursorDot += panelDumpWidth * 2;
            finishArrowKey(event);
            break;

        case 35: // '#'
            if(cursorOnText)
            {
                cursorMark -= cursorMark % 2;
            }
            if(event.isControlDown())
            {
                cursorDot = HexEditor.nibbleCount;
            } else
            {
                cursorDot += panelDumpWidth * 2;
                cursorDot -= cursorDot % (panelDumpWidth * 2);
            }
            finishArrowKey(event);
            break;

        case 114: // 'r'
            HexEditor.searchFindNext();
            break;

        case 117: // 'u'
            if(event.isShiftDown())
            {
                cursorOnText = false;
            } else
            {
                cursorOnText = true;
            }
            limitCursorRange();
            repaint();
            break;

        case 36: // '$'
            if(cursorOnText)
            {
                cursorMark += cursorMark % 2;
            }
            if(event.isControlDown())
            {
                cursorDot = 0;
            } else
            {
                cursorDot--;
                cursorDot -= cursorDot % (panelDumpWidth * 2);
            }
            finishArrowKey(event);
            break;

        case 155: 
            HexEditor.overFlag = !HexEditor.overFlag;
            HexEditor.overDialog.setSelected(HexEditor.overFlag);
            repaint();
            break;

        case 37: // '%'
        case 226: 
            if(cursorOnText)
            {
                cursorDot--;
                cursorDot -= cursorDot % 2;
                cursorMark += cursorMark % 2;
            } else
            {
                cursorDot--;
            }
            finishArrowKey(event);
            break;

        case 34: // '"'
            if(cursorOnText)
            {
                cursorDot += cursorDot % 2;
                cursorMark -= cursorMark % 2;
            }
            cursorDot += panelDumpWidth * 2 * Math.max(1, panelRows - 1);
            finishArrowKey(event);
            break;

        case 33: // '!'
            if(cursorOnText)
            {
                cursorDot -= cursorDot % 2;
                cursorMark += cursorMark % 2;
            }
            cursorDot -= panelDumpWidth * 2 * Math.max(1, panelRows - 1);
            finishArrowKey(event);
            break;

        case 39: // '\''
        case 227: 
            if(cursorOnText)
            {
                cursorDot++;
                cursorDot += cursorDot % 2;
                cursorMark -= cursorMark % 2;
            } else
            {
                cursorDot++;
            }
            finishArrowKey(event);
            break;

        case 38: // '&'
        case 224: 
            if(cursorOnText)
            {
                cursorDot -= cursorDot % 2;
                cursorMark += cursorMark % 2;
            }
            cursorDot -= panelDumpWidth * 2;
            finishArrowKey(event);
            break;
        }
    }

    public void keyTyped(KeyEvent event)
    {
        char ch = event.getKeyChar();
        if(!event.isAltDown())
        {
            if(event.isControlDown())
            {
                switch(ch)
                {
                case 1: // '\001'
                    HexEditor.selectAll();
                    break;

                case 3: // '\003'
                    if(cursorOnText)
                    {
                        HexEditor.copyText();
                    } else
                    {
                        HexEditor.copyHex();
                    }
                    break;

                case 6: // '\006'
                    HexEditor.showSearchDialog();
                    break;

                case 14: // '\016'
                    HexEditor.searchFindNext();
                    break;

                case 18: // '\022'
                    HexEditor.searchReplaceThis();
                    break;

                case 22: // '\026'
                    if(cursorOnText)
                    {
                        HexEditor.pasteText();
                    } else
                    {
                        HexEditor.pasteHex();
                    }
                    break;

                case 24: // '\030'
                    if(cursorOnText)
                    {
                        HexEditor.copyText();
                    } else
                    {
                        HexEditor.copyHex();
                    }
                    HexEditor.deleteSelected();
                    break;
                }
            } else
            if(ch == '\b')
            {
                if(cursorDot != cursorMark)
                {
                    HexEditor.deleteSelected();
                } else
                if(cursorDot > 0)
                {
                    if(cursorOnText)
                    {
                        cursorDot--;
                        cursorDot -= cursorDot % 2;
                        if(cursorDot < HexEditor.nibbleCount - 1)
                        {
                            HexEditor.nibbleData.delete(cursorDot + 1);
                        }
                        HexEditor.nibbleData.delete(cursorDot);
                    } else
                    {
                        cursorDot--;
                        HexEditor.nibbleData.delete(cursorDot);
                    }
                    cursorMark = cursorDot;
                    limitCursorRange();
                    makeVisible(cursorDot);
                    adjustScrollBar();
                    repaint();
                }
            } else
            if(ch == '\033')
            {
                cursorDot = cursorMark;
                repaint();
            } else
            if(ch == '\177')
            {
                if(cursorDot != cursorMark)
                {
                    HexEditor.deleteSelected();
                } else
                if(cursorDot < HexEditor.nibbleCount)
                {
                    if(cursorOnText)
                    {
                        cursorDot -= cursorDot % 2;
                        if(cursorDot < HexEditor.nibbleCount - 1)
                        {
                            HexEditor.nibbleData.delete(cursorDot);
                        }
                        HexEditor.nibbleData.delete(cursorDot);
                    } else
                    {
                        HexEditor.nibbleData.delete(cursorDot);
                    }
                    cursorMark = cursorDot;
                    limitCursorRange();
                    makeVisible(cursorDot);
                    adjustScrollBar();
                    repaint();
                }
            } else
            if(!Character.isISOControl(ch))
            {
                if(cursorOnText)
                {
                    if(HexEditor.overFlag)
                    {
                        cursorDot = cursorMark = Math.min(cursorDot, cursorMark);
                    }
                    byte bytes[] = String.valueOf(ch).getBytes();
                    int nibbles[] = new int[bytes.length * 2];
                    int used = 0;
                    for(int i = 0; i < bytes.length; i++)
                    {
                        nibbles[used++] = bytes[i] >> 4 & 0xf;
                        nibbles[used++] = bytes[i] & 0xf;
                    }

                    HexEditor.pasteNibbles(nibbles, used);
                } else
                {
                    if(HexEditor.overFlag)
                    {
                        cursorDot = cursorMark = Math.min(cursorDot, cursorMark);
                    }
                    int hexValue = HexEditor.charHexValue(ch);
                    int nibbles[] = new int[1];
                    int used = 0;
                    if(hexValue >= 0)
                    {
                        nibbles[used++] = hexValue;
                    } else
                    if(hexValue != -1)
                    {
                        Toolkit.getDefaultToolkit().beep();
                    }
                    if(used > 0)
                    {
                        HexEditor.pasteNibbles(nibbles, used);
                    }
                }
            }
        }
    }

    void limitCursorRange()
    {
        HexEditor.refreshDataSize();
        cursorDot = Math.max(0, Math.min(cursorDot, HexEditor.nibbleCount));
        cursorMark = Math.max(0, Math.min(cursorMark, HexEditor.nibbleCount));
    }

    void makeVisible(int nibbleIndex)
    {
        if(nibbleIndex < panelOffset * 2)
        {
            panelOffset = (nibbleIndex / (2 * panelDumpWidth)) * panelDumpWidth;
            adjustScrollBar();
            repaint();
        } else
        if(nibbleIndex >= (panelOffset + panelRows * panelDumpWidth) * 2)
        {
            panelOffset = ((nibbleIndex / (2 * panelDumpWidth) - panelRows) + 1) * panelDumpWidth;
            adjustScrollBar();
            repaint();
        }
    }

    public void mouseClicked(MouseEvent event)
    {
        requestFocusInWindow();
        convertMouse(event);
        if(mouseTempNibble < 0)
        {
            cursorDot = cursorMark;
            repaint();
        } else
        if(event.isControlDown() || event.getButton() != 1)
        {
            HexEditor.showEditMenu(this, event.getX(), event.getY());
        } else
        if(event.isShiftDown())
        {
            cursorDot = mouseTempNibble;
            cursorOnText = mouseTempOnText;
            limitCursorRange();
            makeVisible(cursorDot);
            repaint();
        } else
        {
            cursorDot = cursorMark = mouseTempNibble;
            cursorOnText = mouseTempOnText;
            limitCursorRange();
            makeVisible(cursorDot);
            repaint();
        }
    }

    public void mouseDragged(MouseEvent event)
    {
        requestFocusInWindow();
        convertMouse(event);
        if(mousePressNibble < 0 || mouseTempNibble < 0 || mousePressOnText != mouseTempOnText)
        {
            if(cursorDot != cursorMark)
            {
                cursorDot = cursorMark;
                repaint();
            }
        } else
        {
            boolean updateFlag = false;
            if(cursorDot != mouseTempNibble)
            {
                cursorDot = mouseTempNibble;
                updateFlag = true;
            }
            if(cursorMark != mousePressNibble)
            {
                cursorMark = mousePressNibble;
                updateFlag = true;
            }
            if(cursorOnText != mouseTempOnText)
            {
                cursorOnText = mouseTempOnText;
                updateFlag = true;
            }
            if(updateFlag)
            {
                repaint();
            }
        }
    }

    public void mouseEntered(MouseEvent event)
    {
        requestFocusInWindow();
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mouseMoved(MouseEvent event)
    {
        requestFocusInWindow();
    }

    public void mousePressed(MouseEvent event)
    {
        requestFocusInWindow();
        convertMouse(event);
        mousePressNibble = mouseTempNibble;
        mousePressOnText = mouseTempOnText;
    }

    public void mouseReleased(MouseEvent event)
    {
        requestFocusInWindow();
    }

    public void mouseWheelMoved(MouseWheelEvent event)
    {
        switch(event.getScrollType())
        {
        case 1: // '\001'
            HexEditor.textScroll.setValue(HexEditor.textScroll.getValue() + event.getWheelRotation() * Math.max(1, panelRows - 1));
            break;

        case 0: // '\0'
            int i = Math.max(1, panelRows - 1);
            i = Math.max(-i, Math.min(i, event.getUnitsToScroll()));
            HexEditor.textScroll.setValue(HexEditor.textScroll.getValue() + i);
            break;
        }
    }

    protected void paintComponent(Graphics context)
    {
        context.setColor(PANEL_COLOR);
        context.fillRect(0, 0, getWidth(), getHeight());
        if(HexEditor.dumpWidth != panelDumpWidth || !HexEditor.fontName.equals(panelFontName) || getWidth() != panelWidth)
        {
            panelDumpWidth = HexEditor.dumpWidth;
            panelFontName = HexEditor.fontName;
            panelHeight = getHeight();
            panelWidth = getWidth();
            panelColumns = 9 + panelDumpWidth * 3 + 2 + 1 + panelDumpWidth + 1;
            int fontSizeLow = 10;
            int fontSizeHigh = 73;
            panelFontSize = -1;
            do
            {
                int testSize = (fontSizeLow + fontSizeHigh) / 2;
                if(panelFontSize == testSize)
                {
                    break;
                }
                panelFontSize = testSize;
                panelFont = new Font(panelFontName, 1, panelFontSize);
                FontMetrics fm = context.getFontMetrics(panelFont);
                lineAscent = fm.getAscent();
                lineHeight = fm.getHeight();
                maxWidth = -1;
                for(int i = 32; i <= 126; i++)
                {
                    charWidths[i] = fm.charWidth(i);
                    maxWidth = Math.max(charWidths[i], maxWidth);
                }

                if(panelColumns * maxWidth < panelWidth - 20)
                {
                    fontSizeLow = panelFontSize;
                } else
                {
                    fontSizeHigh = panelFontSize;
                }
            } while(true);
            for(int i = 32; i <= 126; i++)
            {
                charShifts[i] = (maxWidth - charWidths[i]) / 2;
            }

            panelRows = Math.max(1, (panelHeight - 20) / lineHeight);
            adjustScrollBar();
        } else
        if(getHeight() != panelHeight)
        {
            panelHeight = getHeight();
            panelRows = Math.max(1, (panelHeight - 20) / lineHeight);
            adjustScrollBar();
        }
        int panelNibbleBegin = panelOffset * 2;
        int panelNibbleEnd = panelNibbleBegin + panelRows * panelDumpWidth * 2;
        int selectBegin = Math.max(panelNibbleBegin, Math.min(cursorDot, cursorMark));
        int selectEnd = Math.min(panelNibbleEnd, Math.max(cursorDot, cursorMark));
        if(selectBegin < selectEnd)
        {
            context.setColor(cursorOnText ? SHADOW_SELECT : ACTIVE_SELECT);
            int row = (selectBegin / 2 - panelOffset) / panelDumpWidth;
            int thisRowY = 10 + row * lineHeight;
            int rowNibbleCount = panelDumpWidth * 2;
            int nibble = selectBegin % rowNibbleCount;
            int rowFirstDumpX = 10 + maxWidth * 10;
            int thisColumnX = rowFirstDumpX + maxWidth * (nibble + nibble / 2);
            int nibbleIndex = selectBegin;
            do
            {
                if(nibbleIndex >= selectEnd)
                {
                    break;
                }
                context.fillRect(thisColumnX, thisRowY, maxWidth, lineHeight);
                nibble++;
                nibbleIndex++;
                thisColumnX += maxWidth;
                if(nibble >= rowNibbleCount)
                {
                    nibble = 0;
                    thisColumnX = rowFirstDumpX;
                    thisRowY += lineHeight;
                } else
                if(nibbleIndex < selectEnd && nibbleIndex % 2 == 0)
                {
                    context.fillRect(thisColumnX, thisRowY, maxWidth, lineHeight);
                    thisColumnX += maxWidth;
                }
            } while(true);
            context.setColor(cursorOnText ? ACTIVE_SELECT : SHADOW_SELECT);
            row = (selectBegin / 2 - panelOffset) / panelDumpWidth;
            thisRowY = 10 + row * lineHeight;
            int column = (selectBegin % (panelDumpWidth * 2)) / 2;
            int rowFirstTextX = 10 + maxWidth * (8 + 3 * panelDumpWidth + 4);
            thisColumnX = rowFirstTextX + maxWidth * column;
            nibbleIndex = selectBegin - selectBegin % 2;
            do
            {
                if(nibbleIndex >= selectEnd)
                {
                    break;
                }
                context.fillRect(thisColumnX, thisRowY, maxWidth, lineHeight);
                column++;
                nibbleIndex += 2;
                thisColumnX += maxWidth;
                if(column >= panelDumpWidth)
                {
                    column = 0;
                    thisColumnX = rowFirstTextX;
                    thisRowY += lineHeight;
                }
            } while(true);
        }
        if(cursorDot >= panelNibbleBegin && cursorDot < panelNibbleEnd)
        {
            int cursorY = (cursorDot - panelNibbleBegin) / (panelDumpWidth * 2);
            cursorY = 10 + cursorY * lineHeight;
            int cursorX = (cursorDot % (panelDumpWidth * 2)) / 2;
            cursorX = 10 + maxWidth * (8 + 3 * cursorX + 2);
            if(cursorDot % 2 > 0)
            {
                cursorX += maxWidth;
            }
            context.setColor(cursorOnText ? SHADOW_CURSOR : ACTIVE_CURSOR);
            if(HexEditor.overFlag)
            {
                context.drawRect(cursorX - 1, cursorY, maxWidth + 1, lineHeight - 1);
                if(panelFontSize > 24)
                {
                    context.drawRect(cursorX, cursorY + 1, maxWidth - 1, lineHeight - 3);
                }
            } else
            {
                context.fillRect(cursorX - 1, cursorY, panelFontSize <= 24 ? 2 : 3, lineHeight);
            }
            cursorX = (cursorDot % (panelDumpWidth * 2)) / 2;
            cursorX = 10 + maxWidth * (8 + 3 * panelDumpWidth + cursorX + 4);
            context.setColor(cursorOnText ? ACTIVE_CURSOR : SHADOW_CURSOR);
            if(HexEditor.overFlag)
            {
                context.drawRect(cursorX - 1, cursorY, maxWidth + 1, lineHeight - 1);
                if(panelFontSize > 24)
                {
                    context.drawRect(cursorX, cursorY + 1, maxWidth - 1, lineHeight - 3);
                }
            } else
            {
                context.fillRect(cursorX - 1, cursorY, panelFontSize <= 24 ? 2 : 3, lineHeight);
            }
        }
        context.setColor(TEXT_COLOR);
        context.setFont(panelFont);
        int maxOffset = HexEditor.nibbleCount / 2;
        int rowLastDigitX = 10 + 7 * maxWidth;
        int rowLeftMarkerX = 10 + maxWidth * (11 + 3 * panelDumpWidth) + charShifts[124];
        int rowRightMarkerX = rowLeftMarkerX + maxWidth * (panelDumpWidth + 1);
        int rowY = 10 + lineAscent;
        int thisOffset = panelOffset;
        for(int row = 0; row < panelRows && thisOffset <= maxOffset; row++)
        {
            int shiftedOffset = thisOffset;
            int thisDigitX = rowLastDigitX;
            for(int i = 8; i > 0; i--)
            {
                char ch = HexEditor.HEX_DIGITS[shiftedOffset & 0xf];
                context.drawString(Character.toString(ch), thisDigitX + charShifts[ch], rowY);
                shiftedOffset >>= 4;
                thisDigitX -= maxWidth;
            }

            context.drawString(HexEditor.MARKER_STRING, rowLeftMarkerX, rowY);
            context.drawString(HexEditor.MARKER_STRING, rowRightMarkerX, rowY);
            rowY += lineHeight;
            thisOffset += panelDumpWidth;
        }

        int nibbleIndex = panelOffset * 2;
        int rowFirstDumpX = 10 + maxWidth * 10;
        int rowFirstTextX = rowFirstDumpX + maxWidth * (3 * panelDumpWidth + 2);
        rowY = 10 + lineAscent;
        for(int row = 0; row < panelRows; row++)
        {
            int thisDumpX = rowFirstDumpX;
            int thisTextX = rowFirstTextX;
            for(int column = 0; column < panelDumpWidth && nibbleIndex < HexEditor.nibbleCount; column++)
            {
                int thisNibble = HexEditor.nibbleData.get(nibbleIndex++);
                char ch = HexEditor.HEX_DIGITS[thisNibble & 0xf];
                context.drawString(Character.toString(ch), thisDumpX + charShifts[ch], rowY);
                thisDumpX += maxWidth;
                int byteValue = thisNibble << 4;
                if(nibbleIndex < HexEditor.nibbleCount)
                {
                    thisNibble = HexEditor.nibbleData.get(nibbleIndex++);
                    ch = HexEditor.HEX_DIGITS[thisNibble & 0xf];
                    context.drawString(Character.toString(ch), thisDumpX + charShifts[ch], rowY);
                    thisDumpX += maxWidth;
                    byteValue |= thisNibble;
                } else
                {
                    byteValue = -1;
                }
                thisDumpX += maxWidth;
                if(byteValue < 32 || byteValue > 126)
                {
                    byteValue = 46;
                }
                context.drawString(Character.toString((char)byteValue), thisTextX + charShifts[byteValue], rowY);
                thisTextX += maxWidth;
            }

            rowY += lineHeight;
        }

    }

    public void stateChanged(ChangeEvent event)
    {
        if(panelDumpWidth > 1)
        {
            int scroll = HexEditor.textScroll.getValue();
            int newOffset = scroll * panelDumpWidth;
            if(newOffset != panelOffset)
            {
                panelOffset = newOffset;
                repaint();
            }
        }
    }

    static 
    {
        ACTIVE_CURSOR = Color.BLUE;
        PANEL_COLOR = Color.WHITE;
        TEXT_COLOR = Color.BLACK;
    }
}
