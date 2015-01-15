/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetListener
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import java.awt.*
import javax.swing.*
import java.awt.dnd.*
import groovy.io.FileType
import java.util.List

// Create our frame
new DragDropTestFrame()
    
public class DragDropTestFrame extends JFrame {
    public DragDropTestFrame() {
        // Set the frame title
        super("Drag and drop test")
        // Set the size
        this.setSize(250, 150)
        // Create the label
        JLabel myLabel = new JLabel("Drag something here!", SwingConstants.CENTER)
        // Create the drag and drop listener
        MyDragDropListener1 myDragDropListener = new MyDragDropListener1()
        // Connect the label with a drag and drop listener
        new DropTarget(myLabel, myDragDropListener)
        // Add the label to the content
        this.getContentPane().add(BorderLayout.CENTER, myLabel)
//        JTextField textField = new JTextField()
//        // Connect the label with a drag and drop listener
//        new DropTarget(textField, myDragDropListener)
//        // Add the label to the content
//        this.getContentPane().add(BorderLayout.NORTH, textField)

        // Show the frame
        this.setVisible(true)
    }
}

    class MyDragDropListener1 implements DropTargetListener {
        Set files = []
        
        List<String> filterFilesOfType(List<File> paths, String fileType) {
            List<File> filesFilteredByType = []
            // TODO this should be implemented as parameter..
            def BAM_FILE_PATTERN = /.*.bam$/
    //        def BAM_FILE_PATTERN = /.${fileType}$/
            paths.each { File file ->
                if (file.isDirectory()) {
                    file.traverse(type: FileType.FILES, nameFilter: ~BAM_FILE_PATTERN) { it ->
                        filesFilteredByType << it
                    }
                } else {
                    if(file.name =~ BAM_FILE_PATTERN) {
                        println "detectou!! "
                        filesFilteredByType << file
                    } else {
                        println "nao detectou.............."
                    }
                }
            }
        return filesFilteredByType
    }

    @Override
    public void drop(DropTargetDropEvent event) {
        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY)
        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable()
        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        for (DataFlavor flavor : flavors) {
            try {
                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {
                    // Get all of the dropped files
                    def filesDragged = transferable.getTransferData(flavor)
                    files.addAll(filterFilesOfType(filesDragged, "TODO Pass this parameter properly.. it is being ignored at the moment.."))
                    println files
                }
            } catch (Exception e) {
                // Print out the error stack
                e.printStackTrace();
            }
        }
        // Inform that the drop is complete
        event.dropComplete(true);
    }
        
    @Override
    public void dragEnter(DropTargetDragEvent event) {
    }

    @Override
    public void dragExit(DropTargetEvent event) {
    }

    @Override
    public void dragOver(DropTargetDragEvent event) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) {
    }
}
