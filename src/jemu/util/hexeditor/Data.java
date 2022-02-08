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

import java.text.NumberFormat;
import javax.swing.JOptionPane;

class Data
{

    private static final int PadSIZE = 4096;
    private byte leftArray[];
    private int leftUsed;
    private byte rightArray[];
    private int rightBegin;
    private int rightEnd;
    private int totalSize;

    public Data(int capacity)
    {
        if(capacity >= 0)
        {
            leftArray = new byte[4096];
            rightArray = new byte[capacity + 4096];
            leftUsed = rightBegin = rightEnd = totalSize = 0;
        } else
        {
            throw new IllegalArgumentException("Data capacity " + capacity + " can't be negative");
        }
    }

    void append(int value)
    {
        put(size(), value);
    }

    void clear()
    {
        leftUsed = rightBegin = rightEnd = totalSize = 0;
    }

    void delete(int position)
    {
        refreshSize();
        if(position < 0 || position >= totalSize)
        {
            error(position);
        } else
        if(position == leftUsed - 1)
        {
            leftUsed--;
        } else
        if(position == leftUsed)
        {
            rightBegin++;
        } else
        if(position == totalSize - 1)
        {
            rightEnd--;
        } else
        if(split(position))
        {
            rightBegin++;
        }
    }

    private void error(int position)
    {
        throw new ArrayIndexOutOfBoundsException("Data index " + position + " is not from 0 to " + size());
    }

    int get(int position)
    {
        int result;
        if(position < 0 || position >= size())
        {
            error(position);
            result = -1;
        } else
        if(position < leftUsed)
        {
            result = leftArray[position] & 0xff;
        } else
        {
            result = rightArray[(position - leftUsed) + rightBegin] & 0xff;
        }
        return result;
    }

    void insert(int position, int value)
    {
        refreshSize();
        if(position < 0 || position > totalSize)
        {
            error(position);
        } else
        if(position == leftUsed && leftUsed < leftArray.length)
        {
            leftArray[leftUsed++] = (byte)value;
        } else
        if(position == totalSize && rightEnd < rightArray.length)
        {
            rightArray[rightEnd++] = (byte)value;
        } else
        if(split(position))
        {
            leftArray[leftUsed++] = (byte)value;
        }
    }

    void put(int position, int value)
    {
        refreshSize();
        if(position < 0 || position > totalSize)
        {
            error(position);
        } else
        if(position < leftUsed)
        {
            leftArray[position] = (byte)value;
        } else
        if(position < totalSize)
        {
            rightArray[(position - leftUsed) + rightBegin] = (byte)value;
        } else
        if(rightEnd < rightArray.length)
        {
            rightArray[rightEnd++] = (byte)value;
        } else
        if(split(position))
        {
            leftArray[leftUsed++] = (byte)value;
        }
    }

    private void refreshSize()
    {
        totalSize = (leftUsed + rightEnd) - rightBegin;
    }

    int size()
    {
        refreshSize();
        return totalSize;
    }

    private boolean split(int position)
    {
        refreshSize();
        boolean result = false;
        if(position < 0 || position > totalSize)
        {
            error(position);
        } else
        {
            try
            {
                byte newLeftArray[] = new byte[position + 4096];
                int count = Math.min(leftUsed, position);
                int newLeftUsed = 0;
                for(int i = 0; i < count; i++)
                {
                    newLeftArray[newLeftUsed++] = leftArray[i];
                }

                count = position - leftUsed;
                int from = rightBegin;
                for(int i = 0; i < count; i++)
                {
                    newLeftArray[newLeftUsed++] = rightArray[from++];
                }

                byte newRightArray[] = new byte[(totalSize - position) + 4096];
                count = leftUsed - position;
                from = position;
                int newRightUsed = 0;
                for(int i = 0; i < count; i++)
                {
                    newRightArray[newRightUsed++] = leftArray[from++];
                }

                count = totalSize - Math.max(leftUsed, position);
                from = rightEnd - count;
                for(int i = 0; i < count; i++)
                {
                    newRightArray[newRightUsed++] = rightArray[from++];
                }

                leftArray = newLeftArray;
                leftUsed = newLeftUsed;
                rightArray = newRightArray;
                rightBegin = 0;
                rightEnd = newRightUsed;
                result = true;
            }
            catch(OutOfMemoryError oome)
            {
                byte newRightArray[];
                byte newLeftArray[] = newRightArray = null;
                JOptionPane.showMessageDialog(HexEditor.mainFrame, "Not enough memory to edit this file (" + HexEditor.formatComma.format((size() + 1) / 2) + " bytes).\n" + "Try increasing the Java heap size with the -Xmx option.");
            }
        }
        return result;
    }
}
