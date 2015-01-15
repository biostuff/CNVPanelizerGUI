
import javax.swing.*

JFrame parentFrame = new JFrame()
parentFrame.setLocationRelativeTo(null)
parentFrame.setVisible(true)

parentFrame.setTitle("Parent Frame")



final JDialog dialog = new JDialog(parentFrame, true); // modal
dialog.setUndecorated(true);

JProgressBar bar = new JProgressBar();
bar.setIndeterminate(true);
bar.setStringPainted(true);
bar.setString("Please wait");
dialog.add(bar);
dialog.pack();
dialog.setLocationRelativeTo(parentFrame);
//dialog.setResizable(false);
println "start"
sleep 3000
dialog.setVisible(true);
sleep 3000
println "end"
dialog.setVisible(false);
dialog.dispose()

