package demo

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.image.BufferedImage
import java.util.concurrent.ExecutionException
import javax.swing.JOptionPane
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.DefaultListModel
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JCheckBoxMenuItem
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JRadioButtonMenuItem
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.SwingWorker
import javax.swing.WindowConstants
import javax.swing.border.Border
import javax.swing.BorderFactory;
import javax.swing.Box
import javax.swing.border.EtchedBorder
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.Desktop
import javax.swing.KeyStroke
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
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
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.xssf.usermodel.*
import org.apache.poi.ss.usermodel.*

import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter

class MainWindow extends JFrame {
    MainWindow window
    JTextField sampleDirectoryTextField = new JTextField("")
    JButton sampleDirectoryButton = new JButton("Sample")
    JButton referenceDirectoryButton = new JButton("Reference")
    JTextField referenceDirectoryTextField = new JTextField("")
    JList referenceList
    JList sampleList
    JButton bedFilepathButton = new JButton("...")
    JTextField bedFilepathTextField = new JTextField("")
    JTextField bedGeneNameColumnNumber = new JTextField("6")
    JTextField outputDirTextField = new JTextField("")
    JButton okButton = new JButton("OK")
    JButton cancelButton = new JButton("Cancel")
    JButton outputDirectoryButton = new JButton("...")
    String R_SCRIPT = "script.R"
    ProgressMonitor progressMonitor
    JDialog dialog

    public MainWindow() {
        super()
        window = this
        init()
    }

    public void init() {
        setTitle("CNV panelizer")
        
        dialog = new JDialog(window, true) // modal
        dialog.setUndecorated(true)
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setStringPainted(true);
        bar.setString("Please wait");
        dialog.add(bar);
        dialog.pack();
        dialog.setLocationRelativeTo(window);


        ////Create the menu bar.
        //menuBar = new JMenuBar()
        //
        ////Build the first menu.
        //menu = new JMenu("A Menu")
        ////menu.setMnemonic(KeyEvent.VK_A)
        ////menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items")
        //menuBar.add(menu)

        ////a group of JMenuItems
        //menuItem = new JMenuItem("A text-only menu item", KeyEvent.VK_T);
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK))
        //menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything")
        //menu.add(menuItem);
        //
        //menuItem = new JMenuItem("Both text and icon", new ImageIcon("images/middle.gif"))
        //menuItem.setMnemonic(KeyEvent.VK_B);
        //menu.add(menuItem);
        //
        ////menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
        //menuItem = new JMenuItem("someimga");
        //menuItem.setMnemonic(KeyEvent.VK_D);
        //menu.add(menuItem);
        //
        ////a group of radio button menu items
        //menu.addSeparator();
        //ButtonGroup group = new ButtonGroup();
        //rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
        //rbMenuItem.setSelected(true);
        //rbMenuItem.setMnemonic(KeyEvent.VK_R);
        //group.add(rbMenuItem);
        //menu.add(rbMenuItem);
        //
        //rbMenuItem = new JRadioButtonMenuItem("Another one");
        //rbMenuItem.setMnemonic(KeyEvent.VK_O);
        //group.add(rbMenuItem);
        //menu.add(rbMenuItem);
        //
        ////a group of check box menu items
        //menu.addSeparator();
        //cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
        //cbMenuItem.setMnemonic(KeyEvent.VK_C);
        //menu.add(cbMenuItem);
        //
        //cbMenuItem = new JCheckBoxMenuItem("Another one");
        //cbMenuItem.setMnemonic(KeyEvent.VK_H);
        //menu.add(cbMenuItem);
        //
        ////a submenu
        //menu.addSeparator();
        //submenu = new JMenu("A submenu");
        //submenu.setMnemonic(KeyEvent.VK_S);
        //
        //menuItem = new JMenuItem("An item in the submenu");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK))
        //submenu.add(menuItem);
        //
        //menuItem = new JMenuItem("Another item");
        //submenu.add(menuItem);
        //menu.add(submenu);
        //
        ////Build second menu in the menu bar.
        //menu = new JMenu("Another Menu");
        //menu.setMnemonic(KeyEvent.VK_N);
        //menu.getAccessibleContext().setAccessibleDescription("This menu does nothing")
        //menuBar.add(menu)
        //
        //JMenu helpm = new JMenu("Help");
        //menuBar.add(Box.createHorizontalGlue());
        //menuBar.add(helpm);
        
        //window.setJMenuBar(menuBar)
        
        
        URL iconURL = getClass().getResource("/favicon.png")
        ImageIcon icon = new ImageIcon(iconURL)
        setIconImage(icon.getImage())
        
        JPanel mainPanel = new JPanel()
        mainPanel.setLayout(new BorderLayout())
        mainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED))

        JPanel panelSample = new JPanel()
        panelSample.setLayout(new BorderLayout())
        panelSample.setBorder(BorderFactory.createEmptyBorder(8,8,8,8))
        
        JLabel labelBED = new JLabel("BED : ") 
        labelBED.setBorder(BorderFactory.createEmptyBorder(0,7,0,2))
        panelSample.add(labelBED, BorderLayout.WEST)
        panelSample.add(bedFilepathTextField, BorderLayout.CENTER)
        JPanel panelBorder = new JPanel()
        panelBorder.setLayout(new BorderLayout())

        bedFilepathButton.setPreferredSize(new Dimension(93,25))
        panelBorder.add(bedFilepathButton)
        panelBorder.setBorder(BorderFactory.createEmptyBorder(0,7,0,2))

        panelSample.add(panelBorder, BorderLayout.EAST)

        mainPanel.add(panelSample,BorderLayout.NORTH)

        JPanel panelReferenceSet = new JPanel()
        panelReferenceSet.setLayout(new BorderLayout())
        panelReferenceSet.setBorder(BorderFactory.createEmptyBorder(8,8,8,8))
  
        panelReferenceSet.add(referenceDirectoryTextField, BorderLayout.CENTER)
        panelBorder = new JPanel()
        panelBorder.setLayout(new BorderLayout())
        panelBorder.setBorder(BorderFactory.createEmptyBorder(0,7,0,2))

        referenceDirectoryButton.setPreferredSize(new Dimension(93,25))
        panelBorder.add(referenceDirectoryButton)
        panelReferenceSet.add(panelBorder, BorderLayout.EAST)
        JPanel panelReference = new JPanel()
        panelReference.setLayout(new BorderLayout())
        panelReference.add(panelReferenceSet, BorderLayout.NORTH)

        JTextArea textAreaReference = new JTextArea("""\n\
ref1.bam\n\
ref2.bam\n\
ref3.bam\n\
ref4.bam\n\
ref5.bam\n\
ref6.bam\n\
ref7.bam\n\
ref8.bam\n\
ref9.bam\n\
ref10.bam\n""")
        
        // Create some items to add to the list
        String[] data = [
            //			"ref 1",
            //			"ref 2",
            //			"ref 3",
            //			"ref 4"
        ]
        
        referenceList = new JList(data); //data has type Object[]
        referenceList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        //list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        referenceList.setLayoutOrientation(JList.VERTICAL_WRAP);
        referenceList.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(referenceList)
        listScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
       
        JPanel panelTextAreaReference = new JPanel()
        panelTextAreaReference.setLayout(new BorderLayout())
        panelTextAreaReference.setBorder(BorderFactory.createEmptyBorder(8,8,8,8))
        panelTextAreaReference.add(listScroller, BorderLayout.CENTER)

        panelReference.add(panelTextAreaReference, BorderLayout.CENTER)

        JPanel panelBed = new JPanel()
        panelBed.setLayout(new BorderLayout())

        JPanel bedPanel = new JPanel()
        bedPanel.setLayout(new BorderLayout())

        JPanel outputDataPanel = new JPanel()
        outputDataPanel.setLayout(new BorderLayout())
        outputDataPanel.add(new JLabel("Output dir: "), BorderLayout.WEST)
        //        JTextField outputDir = new JTextField()

        outputDirTextField.setPreferredSize(new Dimension(75,25))
        outputDirTextField.setText(System.getProperty("user.dir") + File.separator + "output")
        outputDataPanel.add(outputDirTextField, BorderLayout.CENTER)
        outputDirectoryButton.setPreferredSize(new Dimension(93,25))
        panelBorder = new JPanel()
        panelBorder.setLayout(new BorderLayout())
        panelBorder.setBorder(BorderFactory.createEmptyBorder(0,7,0,2))
        panelBorder.add(outputDirectoryButton, BorderLayout.CENTER)
        outputDataPanel.add(panelBorder, BorderLayout.EAST)
        
        
        bedPanel.add(outputDataPanel)

        JPanel bedPanelBorder = new JPanel()
        bedPanelBorder.setBorder(BorderFactory.createEmptyBorder(0,8,0,8))
        bedPanelBorder.setLayout(new BorderLayout())
        bedPanelBorder.add(bedPanel)
       
        panelBed.add(bedPanelBorder, BorderLayout.SOUTH)
        
        JPanel painelSampleSet = new JPanel()
        painelSampleSet.setLayout(new BorderLayout())
        painelSampleSet.setBorder(BorderFactory.createEmptyBorder(8,8,8,8))
  
        painelSampleSet.add(sampleDirectoryTextField, BorderLayout.CENTER)
        panelBorder = new JPanel()
        panelBorder.setLayout(new BorderLayout())
        panelBorder.setBorder(BorderFactory.createEmptyBorder(0,7,0,2))

        sampleDirectoryButton.setPreferredSize(new Dimension(93,25))
        panelBorder.add(sampleDirectoryButton)
        painelSampleSet.add(panelBorder, BorderLayout.EAST)
        JPanel painelSample = new JPanel()
        painelSample.setLayout(new BorderLayout())
        painelSample.add(painelSampleSet, BorderLayout.NORTH)

        JTextArea textAreaSample = new JTextArea("""\n\
ref1.bam\n\
ref2.bam\n\
ref3.bam\n\
ref4.bam\n\
ref5.bam\n\
ref6.bam\n\
ref7.bam\n\
ref8.bam\n\
ref9.bam\n\
ref10.bam\n""")
        
        // Create some items to add to the list
        String[] dataSample = [
            //			"ref 1",
            //			"ref 2",
            //			"ref 3",
            //			"ref 4"
        ]

        sampleList = new JList(dataSample); //data has type Object[]
        sampleList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        //list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        sampleList.setLayoutOrientation(JList.VERTICAL_WRAP);
        sampleList.setVisibleRowCount(-1);
        JScrollPane sampleListScroller = new JScrollPane(sampleList)
        sampleListScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
       
        JPanel panelTextAreaSample = new JPanel()
        panelTextAreaSample.setLayout(new BorderLayout())
        panelTextAreaSample.setBorder(BorderFactory.createEmptyBorder(8,8,8,8))
        panelTextAreaSample.add(sampleListScroller, BorderLayout.CENTER)
        
        painelSample.add(panelTextAreaSample, BorderLayout.CENTER)
        
        // Create the drag and drop listener and Connect the label with a drag and drop listener
        MyDragDropListener myDragDropListener = new MyDragDropListener(sampleList, "TODO")
        new DropTarget(painelSample, myDragDropListener)
        MyDragDropListener myDragDropListenerReference = new MyDragDropListener(referenceList, "TODO")
        new DropTarget(panelReference, myDragDropListenerReference)
        
        
        
        sampleList.addMouseListener(new MyMouseListener(sampleList))
        referenceList.addMouseListener(new MyMouseListener(referenceList))
        
        
        
//    sampleList.addMouseListener(new MouseAdapter() {
//        public void mousePressed(MouseEvent e) {
//            println "estou aki1!!!"
////            if (e.isPopupTrigger()) {
//            if (SwingUtilities.isRightMouseButton(e)) {
//                JPopupMenu menu = new JPopupMenu();
//                JMenuItem item = new JMenuItem("Remove");
//                item.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent ae) {
//                        println "vou remover!!!!!!"
//                        
//                        DefaultListModel model = (DefaultListModel) sampleList.getModel();
//                        int selectedIndex = sampleList.getSelectedIndex();
//                        if (selectedIndex != -1) {
//                            model.remove(selectedIndex);
//                        }
//                        println "removi!!"
////                        sampleList.r sampleList.getSelectedValue()
////                        JOptionPane.showMessageDialog(this, "Hello " + sampleList.getSelectedValue());
//                    }
//                })
//                menu.add(item);
//
//                        
//        sampleList.setSelectedIndex(sampleList.locationToIndex(e.getPoint())); //select the item
//        menu.show(sampleList, e.getX(), e.getY()); //and show the menu
//                        
//                        
////                menu.show(this, 5, sampleList.getCellBounds(
////                        sampleList.getSelectedIndex() + 1,
////                        sampleList.getSelectedIndex() + 1).y);
//            } else {
//                println "poupu trigger false?!"
//            }
//        }
//    })
//        
//        
//        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        JPanel referenceSamplePanel = new JPanel()
        referenceSamplePanel.setLayout(new GridLayout(1,2))
        referenceSamplePanel.add(panelReference)
        referenceSamplePanel.add(painelSample)

        mainPanel.add(referenceSamplePanel, BorderLayout.CENTER)

        JPanel panelOkCancelButtons = new JPanel()
        Border paneEdge = BorderFactory.createEmptyBorder(8, 8, 8, 8)
        panelOkCancelButtons.setBorder(paneEdge)
        JPanel panelButtons = new JPanel()
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS))
        okButton.setPreferredSize(new Dimension(75,25))
        cancelButton.setPreferredSize(new Dimension(75,25))

        panelButtons.add(okButton)
        panelButtons.add(Box.createRigidArea(new Dimension(10, 0)))
        panelButtons.add(cancelButton)
        panelOkCancelButtons.setLayout(new BorderLayout())
        panelOkCancelButtons.add(panelButtons, BorderLayout.EAST)

        okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if ((referenceList.getModel().getSize()==0) || (sampleList.getModel().getSize()==0) ||
                    (bedFilepathTextField.getText()=="") || (outputDirTextField.getText()=="")) {
                        JOptionPane.showMessageDialog(window, "Please verify and complete all required fields", "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    execute()
                    dialog.setVisible(true);
                }
            })

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    System.exit(0)
                }   
            })

        JPanel panelBedOutput = new JPanel()
        panelBedOutput.setLayout(new BoxLayout(panelBedOutput, BoxLayout.Y_AXIS))
        panelBedOutput.add(panelBed)
        panelBedOutput.add(panelOkCancelButtons)
        mainPanel.add(panelBedOutput, BorderLayout.SOUTH)
        
        this.getContentPane().setLayout(new BorderLayout())
        this.getContentPane().setBorder(BorderFactory.createEmptyBorder(8,8,8,8))        
        this.getContentPane().add(mainPanel, BorderLayout.CENTER)
        
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    
        bedFilepathButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new java.io.File("."));
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Bed Files","bed")
                    chooser.setDialogTitle("Bed filepath");
                    chooser.setFileFilter(filter)

                    if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                        System.out.println("getCurrentDirectory(): "
                            + chooser.getCurrentDirectory());
                        bedFilepathTextField.setText(chooser.getSelectedFile().getPath())
                        //                        outputDirTextField.setText(chooser.getSelectedFile().getPath().substring(0, chooser.getSelectedFile().getPath().length()-4) + File.separator + "output")
                        //                        outputDirTextField.setText(chooser.getSelectedFile().getPath().substring(0, chooser.getSelectedFile().getPath().length()-4) + "_" + "output")
                    } else {
                        println("No Selection ")
                    }
                }
            });

        referenceDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JFileChooser chooser = new JFileChooser();
                    
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Bam Files", "bam")
                    chooser.setFileFilter(filter)
                    
                    chooser.setCurrentDirectory(new java.io.File("."));
                    chooser.setDialogTitle("Reference Directory path");
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);

                    if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                        System.out.println("getCurrentDirectory(): "
                            + chooser.getCurrentDirectory());
                        referenceDirectoryTextField.setText(chooser.getSelectedFile().getPath())
                        //                        referenceDirectoryTextField.setText(chooser.getCurrentDirectory().getPath())
                        //                        outputDirTextField.setText(referenceDirectoryTextField.getText() + File.separator + "output")
                        System.out.println("getSelectedFile() : "
                            + chooser.getSelectedFile());
                        
                        File folder = new File(chooser.getSelectedFile().getPath())
                        File[] listOfFiles = folder.listFiles()
                        
                        DefaultListModel model = new DefaultListModel();
                        listOfFiles.each {
                            if(it.name.endsWith('.bam')) {
                                model.addElement(it.getName())
                            }
                        }
                        referenceList.setModel(model)
                    } else {
                        System.out.println("No Selection ");
                    }
                }
            });
        
        
        sampleDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JFileChooser chooser = new JFileChooser();

                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Bam Files","bam")
                    chooser.setFileFilter(filter)

                    chooser.setCurrentDirectory(new java.io.File("."))
                    chooser.setDialogTitle("Sample Directory path")
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
                    chooser.setAcceptAllFileFilterUsed(false)

                    if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                        System.out.println("getCurrentDirectory(): "
                            + chooser.getCurrentDirectory())
                        sampleDirectoryTextField.setText(chooser.getSelectedFile().getPath())
                        //                        referenceDirectoryTextField.setText(chooser.getCurrentDirectory().getPath())
                        //                        outputDirTextField.setText(referenceDirectoryTextField.getText() + File.separator + "output")
                        System.out.println("getSelectedFile() : "
                            + chooser.getSelectedFile())
                        
                        File folder = new File(chooser.getSelectedFile().getPath())
                        File[] listOfFiles = folder.listFiles()
                        
                        DefaultListModel model = new DefaultListModel();
                        listOfFiles.each {
                            if(it.name.endsWith('.bam')) {
                                //                                model.addElement(it.getName())
                                model.addElement(it.getAbsolutePath()())
                            }
                        }

                        sampleList.setModel(model)
                    } else {
                        System.out.println("No Selection ")
                    }
                }
            })
        
        
        
        outputDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new java.io.File("."))
                    chooser.setDialogTitle("Output Directory path")
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
                    chooser.setAcceptAllFileFilterUsed(false)
                    
                    if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                        System.out.println("getCurrentDirectory(): "
                            + chooser.getCurrentDirectory())
                        outputDirTextField.setText(chooser.getSelectedFile().getPath())
                    }
                    
                }
            })
        
        setSize(new Dimension(700,500))
        //        pack()
        setLocationRelativeTo(null);
        setVisible(true)
        
        // Quick data to test..
        //        bedFilepathTextField.setText("D:\\repository\\mixedJavaAndGroovy\\testData\\bed\\LCPv1(CNV).bed")
        //        referenceDirectoryTextField.setText("D:\\repository\\mixedJavaAndGroovy\\testData\\reference")
        //        sampleDirectoryTextField.setText("D:\\repository\\mixedJavaAndGroovy\\testData\\samples")
        //        outputDirTextField.setText("D:\\repository\\mixedJavaAndGroovy\\output")

        // large dataset..
        //        bedFilepathTextField.setText("Z:\\Projekt Results\\Copy Numer Analysis\\bed\\LCPv1(CNV).bed")
        //        referenceDirectoryTextField.setText("Z:\\Projekt Results\\Copy Numer Analysis\\ReferenceBam\\LCPv1")
        //        sampleDirectoryTextField.setText("Z:\\Projekt Results\\Copy Numer Analysis\\EGFR-bam-Files\\Lung_LCPv1")
        //        outputDirTextField.setText("D:\\repository\\mixedJavaAndGroovy\\output")
    }

    private void execute() {
        SwingWorker<Boolean, Integer> worker = new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                //                def RscriptText = getClass().getResourceAsStream("/Rscript.R").text
                def RscriptText = getClass().getResourceAsStream("/RscriptUsingExcelInput.R").text

                String referenceDirectoryPath = referenceDirectoryTextField.getText()
                String sampleDirectoryPath = sampleDirectoryTextField.getText()
                String bedFilePath = bedFilepathTextField.getText()
                // TODO this parameters have to be passed properly..
                String ampliconNameColumnNumber = "4"
                String removePcrDuplicates = "TRUE"
                String numberOfBootstrapReplicates = "10"

                println "*********************************************************************************"
                println "bedFilePath : " + bedFilePath
                println "ampliconNameColumnNumber : " + ampliconNameColumnNumber
                println "referenceDirectoryPath : " + referenceDirectoryPath
                println "sampleDirectoryPath : " + sampleDirectoryPath
                println "removePcrDuplicates : " + removePcrDuplicates
                println "*********************************************************************************"

                String outputDirectoryPath = outputDirTextField.getText()
                def outputDirectory = new File(outputDirectoryPath)
                // TODO remove or comment next line.. want it to complain if the directory exists..
                //outputDirectory.deleteDir()
                if(outputDirectory.exists()) {
                    println("The output Directory already exists.")
                    JOptionPane.showMessageDialog(window, "The output Directory already exists. Please rename or remove it", "Information", JOptionPane.INFORMATION_MESSAGE);
                    return false
                }

                println("creating output directory ")
                outputDirectory.mkdirs()  // creates all directories existent in the path
                println("creating filepath ")
                //println System.getProperty("file.separator")
                println "___________________________________"
                String pathSeparator = System.getProperty("file.separator").toString()
                // write the file to the output directory..
                String scriptFilepath = outputDirectoryPath + pathSeparator + R_SCRIPT
                println("creating file " + scriptFilepath)
                def rscript = new File(scriptFilepath)
                assert !rscript.exists()
                rscript.createNewFile() //if it doesn't already exist
                println("file created : " + scriptFilepath)
                rscript << RscriptText
                
                
                String inputDatafilepath = "${outputDirectoryPath}/inputData.xlsx"

                FileOutputStream fileOut = new FileOutputStream(inputDatafilepath)

                HSSFWorkbook workbook = new HSSFWorkbook()
                HSSFSheet worksheet = workbook.createSheet("reference")
                // TODO why cannt make this work?!
                //                        XSSFworkbook = new XSSFWorkbook()
                //			XSSFSheet worksheet = workbook.createSheet("reference")

                int numberOfFiles = referenceList.getModel().getSize()
                for (i in 0..numberOfFiles-1) {
                    String filepath = referenceList.getModel().getElementAt(i)
                    Row row = worksheet.createRow((short)i)
                    row.createCell(0).setCellValue(filepath)
                }

                worksheet = workbook.createSheet("sample")
                numberOfFiles = sampleList.getModel().getSize()
                for (i in 0..numberOfFiles-1) {
                    String filepath = sampleList.getModel().getElementAt(i)
                    Row row = worksheet.createRow((short)i)
                    row.createCell(0).setCellValue(filepath)
                }

                workbook.write(fileOut)
                fileOut.close()

                // call the command to the created file using 

                println("" + RscriptText)

                String R_HOME = "C:\\programs\\R\\R-3.1.1\\bin\\i386"
                println("r home " + R_HOME)
                // TODO verifies at startup if R and the package is available in the system
                //String R_SCRIPT_COMMAND = """${R_HOME}${pathSeparator}Rscript"""
                String R_SCRIPT_COMMAND = """Rscript"""
                println("R_SCRIPT_COMMAND " + R_SCRIPT_COMMAND)

                // command that uses directories paths
                //def command = """${R_SCRIPT_COMMAND} "${scriptFilepath}" "${referenceDirectoryPath}" "${sampleDirectoryPath}" "${bedFilePath}" ${ampliconNameColumnNumber} ${removePcrDuplicates} "${outputDirectoryPath}" """
                def command = """${R_SCRIPT_COMMAND} "${scriptFilepath}" "${inputDatafilepath}" "${bedFilePath}" ${ampliconNameColumnNumber} ${removePcrDuplicates} "${outputDirectoryPath}" """

                println "command : " + command

                // Creates files to run the script with the right parameters already filled in (allows to run the R script with the right parameters without having to startup the GUI and is easier to debug..)
                // for windows
                String commandFilepath = outputDirectoryPath + pathSeparator + "run.bat"
                File commandFile = new File(commandFilepath )
                commandFile.createNewFile()
                commandFile << command.toString()
                // for linux
                commandFilepath = outputDirectoryPath + pathSeparator + "run.sh"
                commandFile = new File(commandFilepath )
                commandFile.createNewFile()
                commandFile << command.toString()
                
                Process proc = command.execute() // Call *execute* on the string
                println proc.text

                //                ProcessBuilder builder = new ProcessBuilder(command)
                //                builder.redirectErrorStream(true)
                //                Process proc = builder.start()
                //                
                //                InputStream stdout = proc.getInputStream()
                //                BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))
                //
                //                while((line = reader.readLine ()) != null) {
                //                   println ("Stdout: " + line)
                //                }

                

                //                proc.waitForOrKill(5000)
                //                proc.waitFor() // Wait for the command to finish
                
                // Obtain status and output
                println "Process finished"
                println "return code: ${ proc.exitValue()}"
                
                if (proc.exitValue() == 0) {
                    println "opening output directory: " + outputDirectoryPath
                    File outputDirectoryFolder = new File(outputDirectoryPath)
                    Desktop.getDesktop().open(outputDirectoryFolder)
//                    outputDirectoryFolder.close()
                } else {
                    println "some error occured!!!"
                    println "Error: " + proc
                }
                
                // Here we can return some object of whatever type
                // we specified for the first template parameter.
                // (in this case we're auto-boxing 'true').
                return true;
            }

            // Can safely update the GUI from this method.
            protected void done() {
                dialog.dispose()
                java.awt.Toolkit.getDefaultToolkit().beep();
                boolean status;
                status = get();
                if (status) {
                    JOptionPane.showMessageDialog(window, "Processing completed", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
                try {
                    // Retrieve the return value of doInBackground.
                    
                    // TODO IMPROVE THIS FOR WHEN THERE ARE ERRORS!!!!!!
                    if (Desktop.isDesktopSupported()) {
                        try {
                        } catch (IOException ex) {
                        }
                    }
                } catch (InterruptedException e) {
                    // This is thrown if the thread's interrupted.
                } catch (ExecutionException e) {
                    // This is thrown if we throw an exception
                    // from doInBackground.
                }
            }

            @Override
            // TODO Can safely update the GUI from this method. (I wish..)
            protected void process(List<Integer> chunks) {
                // Here we receive the values that we publish().
                // They may come grouped in chunks.
                int mostRecentValue = chunks.get(chunks.size() - 1)

                //                countLabel1.setText(Integer.toString(mostRecentValue));
            }
        }
        worker.execute()
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    class MyMouseListener extends MouseAdapter {
        
        JList list
        
        public MyMouseListener(JList list) {
            this.list = list
        }
        
        

        public void mousePressed(MouseEvent e) {
            println "estou aki1!!!"
//            if (e.isPopupTrigger()) {
            if (SwingUtilities.isRightMouseButton(e)) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem item = new JMenuItem("Remove");
                menu.add(item);
                println "e.getPOoint : " + e.getPoint()
                println " list " + list
                list.setSelectedIndex(list.locationToIndex(e.getPoint())); //select the item
                menu.show(list, e.getX(), e.getY()); //and show the menu
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        println "vou remover!!!!!!"

                        DefaultListModel model = (DefaultListModel) list.getModel();
                        println "____________________________________________________________"
                        model.each {
                            println it
                        }
                        int selectedIndex = list.getSelectedIndex();
                        println "selectedIndex " + selectedIndex
                        if (selectedIndex != -1) {
                            model.remove(selectedIndex);
                        }
                        println "--------------------------------------------------------------"
                        
                        model.each {
                            println it
                        }   

                            
                        println "removi!!"
                        
                        println "____________________________________________________________"
                            
//                        list.r list.getSelectedValue()
//                        JOptionPane.showMessageDialog(this, "Hello " + list.getSelectedValue());
                    }
                })
                

                        

                        
                        
//                menu.show(this, 5, list.getCellBounds(
//                        list.getSelectedIndex() + 1,
//                        list.getSelectedIndex() + 1).y);
            } else {
                println "poupu trigger false?!"
            }
        }
    }
        
    
    
    
    
    
    
    
    // TODO implement this class properly.. no hacks!
    class MyDragDropListener implements DropTargetListener {
        Set files = []
        
        JList list
        String fileType
        
        MyDragDropListener(JList list , String fileType) {
            this.list = list
            this.fileType = fileType
        }
        
        
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
                        files = []
                        list.getModel().each {
                            files << it
                        }

                        files.addAll(filterFilesOfType(filesDragged, "TODO Pass this parameter properly.. it is being ignored at the moment.."))

                        
                        println files
                        println "()()()"
                        

//                        DefaultListModel model = list.getModel()
                        DefaultListModel model = new DefaultListModel();
                        files.each {
                            println "files class : " +  it.class
//                            model.addElement(it.getAbsolutePath())
                            model.addElement(it)
                        }
                        list.setModel(model)

//                        DefaultListModel model = new DefaultListModel();
//                        files.each {
//                            if(it.name.endsWith('.bam')) {
//                                //                                model.addElement(it.getName())
//                                model.addElement(it.getAbsolutePath())
//                            }
//                        }
//                        list.setModel(model)
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
}

new MainWindow()
