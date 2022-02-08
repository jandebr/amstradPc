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

import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

class User
    implements ActionListener, Transferable
{

    public User()
    {
    }

    public void actionPerformed(ActionEvent event)
    {
        HexEditor.userButton(event);
    }

    public Object getTransferData(DataFlavor flavor)
        throws IOException, UnsupportedFlavorException
    {
        if(HexEditor.clipString == null)
        {
            throw new IOException("no clipboard string created");
        }
        if(flavor.equals(DataFlavor.stringFlavor))
        {
            return HexEditor.clipString;
        } else
        {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public DataFlavor[] getTransferDataFlavors()
    {
        DataFlavor result[] = {
            DataFlavor.stringFlavor
        };
        return result;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return flavor.equals(DataFlavor.stringFlavor);
    }
}
