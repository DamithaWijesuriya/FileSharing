import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.channels.FileChannel;


/**
 * Created by hsenid on 5/23/17.
 */
public class FileTransfer extends JFrame {
    private JPanel FileTransferPanal;
    private JButton stopButton;
    private JButton startButton;
    private JComboBox comboBox1;
    private JTextField DestinationTextfield;
    private JTextField SourceFileTextField;
    private JTextField TimetextField;
    private JButton sourceButton;
    private JButton destinationButton;
    //    private JProgressBar progressBar1;
    int progressBarSize;
    long expectedBytes;
    long totalBytesCopied;
    private JProgressBar fileCopyProgressBar;
    int bytesRead;

    public FileTransfer() {
        fileCopyProgressBar.setVisible(false);
        comboBox1.addItem("copy");
        comboBox1.addItem("move");
        Object comboboxItem = comboBox1.getSelectedItem();
        add(FileTransferPanal);

        startButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            public void actionPerformed(ActionEvent e) {
                String sourceTextPath = SourceFileTextField.getText();
                String destinationTextPath = DestinationTextfield.getText();


                //copy file conventional way using Stream
                long start = System.nanoTime();
                if (SourceFileTextField.equals("") || DestinationTextfield.equals("")) {

                    String messageForNull = (SourceFileTextField.equals("") && DestinationTextfield.equals("") ? "Both paths are empty" : (SourceFileTextField.equals("") ? "From path is Empty" : "To path empty"));
                    JOptionPane.showMessageDialog(null, messageForNull);
                }
                else if (!SourceFileTextField.equals("") || !DestinationTextfield.equals("")){
                    try {
                        if (comboBox1.getSelectedItem().toString().equals("copy")) {
                            copyFileUsingChannel(sourceTextPath, destinationTextPath);
                                showProgress();
                            TimetextField.setText(String.valueOf(System.nanoTime() - start));

                        } else if (comboBox1.getSelectedItem().equals("move")) {
                            fileMove();
                            TimetextField.setText(String.valueOf(System.nanoTime() - start));
                        }
                        //sourceTextPath.renameTo(new File(destinationTextPath + sourceTextPath.getName()); // Files.move(sourceTextPath, DestinationTextfield, true);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Can not copy or Move");
                    }
                    System.out.println("Time taken by Channel Copy = " + (System.nanoTime() - start));
                }
                else{

                    File src = new File(sourceTextPath);
                    File dst = new File(destinationTextPath);

                    fileCopyProgressBar.setVisible(true);


                }
            }
        });
        sourceButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            public void actionPerformed(ActionEvent e) {
                filChooser filChooser = new filChooser();
                SourceFileTextField.setText(filChooser.fileName);
            }
        });
        destinationButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            public void actionPerformed(ActionEvent e) {
                filChooser filChooser = new filChooser();
                DestinationTextfield.setText(filChooser.fileName);
            }
        });
        stopButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            public void actionPerformed(ActionEvent e) {
                System.out.println("Test stop");
                showProgress();
            }
        });
    }

    public void showProgress() {

        int progressBarSize = (int) ((totalBytesCopied / (float) expectedBytes) * 100);
        fileCopyProgressBar.setVisible(true);

        fileCopyProgressBar.setValue(progressBarSize);
    }

    public static void main(String[] args) {

        FileTransfer frame = new FileTransfer();
        frame.setSize(400, 300);
        frame.setLocation(200, 100);
        frame.setVisible(true);


    }

    private void copyFileUsingChannel(String source, String dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            showProgress();
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
    }

    public void fileMove() {

        String sourceTextPath = SourceFileTextField.getText();
        String destinationTextPath = DestinationTextfield.getText();
        InputStream in = null;
        OutputStream out = null;

        try {

            File oldFile = new File(sourceTextPath);
            File newFile = new File(destinationTextPath);

            in = new FileInputStream(oldFile);
            out = new FileOutputStream(newFile);
            expectedBytes = sourceTextPath.length();
            totalBytesCopied = 0;
            byte[] moveBuff = new byte[256];

            int bytesRead;
            fileCopyProgressBar.setVisible(true);

            while ((bytesRead = in.read(moveBuff)) > 0) {
                out.write(moveBuff, 0, bytesRead);
                totalBytesCopied += bytesRead;
                progressBarSize = (int) ((totalBytesCopied / (float)expectedBytes) * 100);
                fileCopyProgressBar.setVisible(true);
                fileCopyProgressBar.setValue(progressBarSize);
            }

            in.close();
            out.close();

            oldFile.delete();

            System.out.println("The File was successfully moved to the new folder");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can not move");
        }

    }

}

class filChooser extends JFrame {

    JFileChooser chooser;
    String fileName;

    public filChooser() {
        chooser = new JFileChooser();
        int r = chooser.showOpenDialog(new JFrame());
        if (r == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getPath();
            System.out.println(fileName);
        }
    }
}
