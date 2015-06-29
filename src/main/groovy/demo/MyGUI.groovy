package demo

import java.awt.dnd.*
import javax.swing.SwingUtilities
import javax.swing.JMenuItem
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JDialog
import javax.swing.JPopupMenu
import javax.swing.ProgressMonitor
import javax.swing.ListSelectionModel
import javax.swing.JProgressBar
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
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
import javax.swing.KeyStroke
import javax.swing.border.EtchedBorder
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.image.BufferedImage
import java.awt.Desktop
import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter
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
import java.util.concurrent.ExecutionException
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.xssf.usermodel.*
import org.apache.poi.ss.usermodel.*
import groovy.io.FileType
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

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
    //    JTextField bedGeneNameColumnNumber = new JTextField("6")
    JTextField outputDirTextField = new JTextField("")
    JButton okButton = new JButton("Run")
    JButton cancelButton = new JButton("Cancel")
    JButton outputDirectoryButton = new JButton("...")
    String R_SCRIPT = "script.R"
    ProgressMonitor progressMonitor
    JDialog dialog
    JMenuItem menuItemImport, menuItemExport

    String ampliconNameColumnNumber = "6"
    String removePcrDuplicates = "TRUE"
    String numberOfBootstrapReplicates = "10000"
    String specificityLevel = "1.75"
    //    boolean overwriteResults = true

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
        JMenuBar menuBar = new JMenuBar()

        //Build the first menu.
        JMenu menu = new JMenu("Preferences")
        menuBar.add(menu)

        ////a group of JMenuItems
        menuItemExport = new JMenuItem("Export parameters", KeyEvent.VK_T)
        menuItemExport.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    DefaultListModel sListModel = (DefaultListModel)sampleList.getModel()
                    String[]  sListModelArray = sListModel.toArray()
                    List sList = Arrays.asList(sListModelArray)

                    DefaultListModel rListModel = (DefaultListModel)referenceList.getModel()
                    String[] rListModelArray = rListModel.toArray()
                    List rList = Arrays.asList(rListModelArray)
                    println "bed " + bedFilepathTextField.getText()
                    println "ampliconNameColumnNumber " + ampliconNameColumnNumber
                    println "removePcrDuplicates "+  removePcrDuplicates
                    println "specificityLevel " + specificityLevel
                    println "numberOfBootstrapReplicates " + numberOfBootstrapReplicates
                    println "outputDir " + outputDirTextField.getText()
                    //                        println "overwriteResults " + overwriteResults
                    println "sampleSet " + sList
                    println "referenceSet " + rList
                    println "--------------------"

                    def json = JsonOutput.toJson(
                            "params": [
                                "bed": bedFilepathTextField.getText(),
                                "ampliconNameColumnNumber": ampliconNameColumnNumber,
                                "removePcrDuplicates": removePcrDuplicates,
                                "specificityLevel": specificityLevel,
                                "numberOfBootstrapReplicates": numberOfBootstrapReplicates,
                                "outputDir": outputDirTextField.getText(),
                            //                                "overwriteResults": overwriteResults,
                                "sampleSet": sList,
                                "referenceSet": rList
                        ])
                    String prettyJson = JsonOutput.prettyPrint(json)
                    println  prettyJson
                    
                    JFileChooser fileChooser = new JFileChooser()
                    fileChooser.setCurrentDirectory(new java.io.File("."));
                    fileChooser.setDialogTitle("Specify a file to save the parameters")  
                    int userSelection = fileChooser.showSaveDialog(window)
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile()                        

                        fileToSave.text = prettyJson
                    }
                }
            })
        
        menu.add(menuItemExport);
        window.setJMenuBar(menuBar)
        
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

        //        panelReferenceSet.add(referenceDirectoryTextField, BorderLayout.CENTER)
        panelReferenceSet.add(new JLabel("Reference Set: "), BorderLayout.WEST)
        panelBorder = new JPanel()
        panelBorder.setLayout(new BorderLayout())
        panelBorder.setBorder(BorderFactory.createEmptyBorder(0,7,0,2))

        referenceDirectoryButton.setPreferredSize(new Dimension(93,25))
        //        panelBorder.add(referenceDirectoryButton)
        panelReferenceSet.add(panelBorder, BorderLayout.EAST)
        JPanel panelReference = new JPanel()
        panelReference.setLayout(new BorderLayout())
        panelReference.add(panelReferenceSet, BorderLayout.NORTH)
        
        referenceList = new JList()
        referenceList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        //        referenceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
  
        //        painelSampleSet.add(sampleDirectoryTextField, BorderLayout.CENTER)
        painelSampleSet.add(new JLabel("Sample Set: "), BorderLayout.WEST)
        panelBorder = new JPanel()
        panelBorder.setLayout(new BorderLayout())
        panelBorder.setBorder(BorderFactory.createEmptyBorder(0,7,0,2))

        sampleDirectoryButton.setPreferredSize(new Dimension(93,25))
        //        panelBorder.add(sampleDirectoryButton)
        painelSampleSet.add(panelBorder, BorderLayout.EAST)
        JPanel painelSample = new JPanel()
        painelSample.setLayout(new BorderLayout())
        painelSample.add(painelSampleSet, BorderLayout.NORTH)

        sampleList = new JList()
        sampleList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        //        sampleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
        
        PropertiesDragDropListener pddl = new PropertiesDragDropListener()
        new DropTarget(window, pddl)
        sampleList.addMouseListener(new MyMouseListener(sampleList))
        referenceList.addMouseListener(new MyMouseListener(referenceList))        

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
                        referenceDirectoryTextField.setText(chooser.getSelectedFile().getPath())
                        //                        referenceDirectoryTextField.setText(chooser.getCurrentDirectory().getPath())
                        //                        outputDirTextField.setText(referenceDirectoryTextField.getText() + File.separator + "output")
                        
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
                        println("No Selection ");
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
                        sampleDirectoryTextField.setText(chooser.getSelectedFile().getPath())
                        //                        referenceDirectoryTextField.setText(chooser.getCurrentDirectory().getPath())
                        //                        outputDirTextField.setText(referenceDirectoryTextField.getText() + File.separator + "output")
                        
                        File folder = new File(chooser.getSelectedFile().getPath())
                        File[] listOfFiles = folder.listFiles()
                        
                        DefaultListModel model = new DefaultListModel();
                        listOfFiles.each {
                            if(it.name.endsWith('.bam')) {
                                model.addElement(it.getAbsolutePath()())
                            }
                        }
                        sampleList.setModel(model)
                    } else {
                        println("No Selection ")
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
                        outputDirTextField.setText(chooser.getSelectedFile().getPath())
                    }
                }
            })

        setSize(new Dimension(700,500))
        setLocationRelativeTo(null)
        String defaultPropertiesFilepath = "${System.getProperty("user.dir")}/properties.json"
        fillWithParametersIfExist(defaultPropertiesFilepath)
        setVisible(true)
    }

    private void fillWithParametersIfExist(String propertiesFilepath) {
        File properties = new File(propertiesFilepath)
        
        if (properties.exists()) {
            
            def slurper = new JsonSlurper()
            def result = slurper.parseText(properties.text)
            println JsonOutput.prettyPrint(properties.text)
            
            ampliconNameColumnNumber = result.params.ampliconNameColumnNumber
            removePcrDuplicates = result.params.removePcrDuplicates
            numberOfBootstrapReplicates = result.params.numberOfBootstrapReplicates
            //            overwriteResults = result.params.overwriteResults
            numberOfBootstrapReplicates = result.params.numberOfBootstrapReplicates
            specificityLevel = result.params.specificityLevel
            
            def referenceSetList = result.params.referenceSet
            def sampleSetList = result.params.sampleSet

            DefaultListModel model = new DefaultListModel()
            referenceSetList.each {
                model.addElement(it)
            }
            referenceList.setModel(model)
            
            model = new DefaultListModel()
            sampleSetList.each {
                model.addElement(it)
            }
            sampleList.setModel(model)
            bedFilepathTextField.setText(result.params.bed)
            outputDirTextField.setText(result.params.outputDir)
        }
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
                    JOptionPane.showMessageDialog(window, "The output Directory already exists. Please rename or remove it", "Information", JOptionPane.INFORMATION_MESSAGE);
                    return false
                }

                println("creating output directory ")
                outputDirectory.mkdirs()  // creates all directories existent in the path
                String pathSeparator = System.getProperty("file.separator").toString()
                // write the file to the output directory..
                String scriptFilepath = outputDirectoryPath + pathSeparator + R_SCRIPT
                println("creating file " + scriptFilepath)
                def rscript = new File(scriptFilepath)
                assert !rscript.exists()
                rscript.createNewFile() //if it doesn't already exist
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

                String R_SCRIPT_COMMAND = """Rscript"""
                // command that uses directories paths
                def command = """${R_SCRIPT_COMMAND} "${scriptFilepath}" "${inputDatafilepath}" "${bedFilePath}" ${ampliconNameColumnNumber} ${removePcrDuplicates} ${numberOfBootstrapReplicates} ${specificityLevel} "${outputDirectoryPath}" """
                // TODO why this command does not work?
                //                def command = """${R_SCRIPT_COMMAND} "${scriptFilepath}" "${inputDatafilepath}"
                //                                                                        "${bedFilePath}"
                //                                                                        ${ampliconNameColumnNumber}
                //                                                                        ${removePcrDuplicates}
                //                                                                        ${numberOfBootstrapReplicates}
                //                                                                        ${specificityLevel}
                //                                                                        "${outputDirectoryPath}" """

                println "command : " + command

                // Creates files to run the script with the right parameters already filled in (allows to run the R script with the right parameters without having to startup the GUI and is easier to debug..)
                // for windows
                String commandFilepath = outputDirectoryPath + pathSeparator + "run.bat"
                // Check if is not windows
                String operatingSystemName = System.getProperty("os.name")
                if (!operatingSystemName.contains("windows")) {
                    commandFilepath = outputDirectoryPath + pathSeparator + "run.sh"
                }
                File commandFile = new File(commandFilepath)
                commandFile.createNewFile()
                commandFile.setExecutable(true, true)
                commandFile << command.toString()
                Process proc = "${commandFilepath}".execute()
                println proc.text
                
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
            if (SwingUtilities.isRightMouseButton(e)) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem item = new JMenuItem("Remove");
                menu.add(item);
                list.setSelectedIndex(list.locationToIndex(e.getPoint())); //select the item
                menu.show(list, e.getX(), e.getY()); //and show the menu
                item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            DefaultListModel model = (DefaultListModel) list.getModel();
                            model.each {
                                println it
                            }
                            int selectedIndex = list.getSelectedIndex();
                            if (selectedIndex != -1) {
                                model.remove(selectedIndex);
                            }
                            model.each {
                                println it
                            }
                        }
                    })
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
                        println "Found bam file"
                        filesFilteredByType << file
                    } else {
                        println "Not a bam file"
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
                        DefaultListModel model = new DefaultListModel();
                        files.each {
                            model.addElement(it)
                        }
                        list.setModel(model)
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

    class PropertiesDragDropListener implements DropTargetListener {

        String fileType

        PropertiesDragDropListener() {
        }
        
        boolean isJsonPropertiesFile(List<File> paths, String fileType) {
            return true
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
                        if (isJsonPropertiesFile(filesDragged, "TODO Pass this parameter properly.. it is being ignored at the moment..")) {
                            fillWithParametersIfExist(filesDragged.first().path)   
                        } else {
                            println "Not a valid CNVPanelizer property file"
                        }
                    }
                } catch (Exception e) {
                    // Print out the error stack
                    e.printStackTrace();
                }
            }
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
