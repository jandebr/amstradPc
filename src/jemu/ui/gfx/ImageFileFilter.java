/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jemu.ui.gfx;

/**
 *
 * @author Markus
 */

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ImageFileFilter
        extends FileFilter {
    
    public boolean accept(File file) {
        String fileName = file.getName();
        fileName = fileName.toLowerCase();
        
        return (file.isDirectory() ||
                fileName.endsWith(".png") ||
                fileName.endsWith(".gif") ||
                fileName.endsWith(".jpg") ||
                fileName.endsWith(".bmp")
                );
    }
    
    public String getDescription() {
        return "Supported graphic formats (GIF, PNG, JPG, BMP)";
    }
    
}