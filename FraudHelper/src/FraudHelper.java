import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import os_utils.OsUtils;
import os_utils.file_filters.ExcelFilter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;


public class FraudHelper {

	private JFrame frmFraudHelper;
	private JTextField textField;
	private JFileChooser fc;
	private File inputFile;
	private Preferences userPrefs;
	
	private JSpinner spinPersent;
	private JSpinner spinMinutes;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FraudHelper window = new FraudHelper();
					window.frmFraudHelper.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FraudHelper() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		userPrefs = Preferences.userRoot().node("fraudHelper");
		
		frmFraudHelper = new JFrame();
		frmFraudHelper.setTitle("Fraud Helper");
		frmFraudHelper.setBounds(100, 100, 605, 451);
		frmFraudHelper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFraudHelper.getContentPane().setLayout(null);
		frmFraudHelper.setResizable(false);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(5, 5, 591, 412);
		frmFraudHelper.getContentPane().add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Fraud", null, panel, null);
		panel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(173, 51, 211, 25);
		panel.add(textField);
		textField.setColumns(10);
		textField.setEditable(false);
		
		
		final JTextPane textPane = new JTextPane();
		textPane.setBounds(43, 307, 487, 50);
		panel.add(textPane);
		
		
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 2, true), "Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(43, 102, 487, 196);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		spinMinutes = new JSpinner();
		spinMinutes.setModel(new SpinnerNumberModel(new Integer(100), new Integer(0), null, new Integer(1)));
		spinMinutes.setBounds(288, 59, 47, 20);
		panel_1.add(spinMinutes);
		
		JLabel lblLargerThan = new JLabel("Larger than ( %)");
		lblLargerThan.setBounds(65, 30, 138, 17);
		panel_1.add(lblLargerThan);
		
		JLabel lblLargerThanmin = new JLabel("Larger than (min)");
		lblLargerThanmin.setBounds(65, 60, 138, 17);
		panel_1.add(lblLargerThanmin);
		
		spinPersent = new JSpinner();
		spinPersent.setModel(new SpinnerNumberModel(10, 0, 100, 1));
		spinPersent.setBounds(288, 29, 47, 20);
		panel_1.add(spinPersent);
		
		
		
		JLabel lblInputFile = new JLabel("Input file:");
		lblInputFile.setBounds(43, 55, 92, 17);
		panel.add(lblInputFile);
		
		
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Billing Studio", null, panel_2, null);
		
		JButton btnBum = new JButton("Browse");
		btnBum.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fc = new JFileChooser();
				
				String dirPath = "";
				try {
					dirPath = userPrefs.get("currDirPath",
							OsUtils.getCurrentOsDirectory().getCanonicalPath());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// set to directory where program is
				fc.setCurrentDirectory(new File(dirPath));
				
				
				//Add a custom file filter and disable the default
			    //(Accept All) file filter.
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter(new ExcelFilter());
				
				int returnVal = fc.showDialog(frmFraudHelper, "Attach");
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						// store file directory to config file
						userPrefs.put("currDirPath", 
								fc.getCurrentDirectory().getCanonicalPath());
						textField.setText(
								fc.getSelectedFile().getCanonicalPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
		            inputFile = fc.getSelectedFile();		            
		            textPane.setText(inputFile.getPath());		            
		        }
			}
		});
		btnBum.setBounds(396, 51, 91, 25);
		panel.add(btnBum);
		
		JButton btnProceed = new JButton("Proceed");
		btnProceed.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SettingsData opts = new SettingsData();
	            opts.setInputFile(inputFile);
	            opts.setCritPersent((Integer) spinPersent.getValue());
	            opts.setMoreThan((Integer) spinMinutes.getValue());
	            FraudAnalyzer solver = new FraudAnalyzer(opts);
	            Thread analyze = new Thread(solver);
	            analyze.start();
			}
		});
		btnProceed.setBounds(65, 119, 117, 25);
		panel_1.add(btnProceed);
	}

}
