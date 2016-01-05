//package edu.iisc.base.emulator;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//
//import javax.imageio.ImageIO;
//import javax.swing.JApplet;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//
//public class GUI extends JApplet {
//	// Attributes
//	private Emulator emu;
//
//	// Panels
//	private JScrollPane screenPane;
//
//	// Input/output devices
//	private JPanel screen; // Defines the screen of the emulator
//
//	// Frame and refresh properties
//	private int guiWidth;
//	private int guiHeight;
//
//	// Constants
//	protected final static String EMULATOR_NAME = "Emulator for Data preservation!";
//
//	// Dimension settings
//	private static final int GUI_X_LOCATION = 200;
//	private static final int GUI_Y_LOCATION = 200;
//
//	// GUI update activities
//	public static final int EMU_PROCESS_START = 0;
//	public static final int EMU_PROCESS_STOP = 1;
//	public static final int EMU_PROCESS_RESET = 2;
//
//	public static void main(String[] args) {
//		new GUI();
//	}
//
//	public GUI() {
////		// Print startup information of modular emulator
////		// Add handlers and listeners
////		// Window closing listener
//////		this.addWindowListener(new WindowAdapter() {
//////			public void windowClosing(WindowEvent event) {
//////				exitDioscuri();
//////			}
//////		});
////
////		// Create panel: screen (canvas)
////		screenPane = new JScrollPane();
////		screenPane.setBackground(Color.gray);
////		this.setScreen(this.getStartupScreen());
////
////		// Add panels to frame (arranged in borderlayout)
////		this.getContentPane().add(screenPane, BorderLayout.CENTER);
////
////		// Set dimensions
////		guiWidth = screenPane.getWidth() + 10; // screen width + a random extra
////												// value
////		guiHeight = screenPane.getHeight() + 2 * 38; // screen height + 2 * menu
////														// & statusbar height
////
////		// Build frame
////		this.setLocation(GUI_X_LOCATION, GUI_Y_LOCATION);
////		this.setSize(guiWidth, guiHeight);
//////		this.setTitle(EMULATOR_NAME);
//////		this.setResizable(false);
////
////		this.setVisible(true);
////		this.requestFocus();
////
////		// Start the emulator
////		emu = new Emulator(this);
////		new Thread(emu).start();
//	}
//	
//	public void init() {
//		// Print startup information of modular emulator
//		// Add handlers and listeners
//		// Window closing listener
////		this.addWindowListener(new WindowAdapter() {
////			public void windowClosing(WindowEvent event) {
////				exitDioscuri();
////			}
////		});
//
//		// Create panel: screen (canvas)
//		screenPane = new JScrollPane();
//		screenPane.setBackground(Color.gray);
//		this.setScreen(this.getStartupScreen());
//
//		// Add panels to frame (arranged in borderlayout)
//		this.getContentPane().add(screenPane, BorderLayout.CENTER);
//
//		// Set dimensions
//		guiWidth = screenPane.getWidth() + 10; // screen width + a random extra
//												// value
//		guiHeight = screenPane.getHeight() + 2 * 38; // screen height + 2 * menu
//														// & statusbar height
//
//		// Build frame
//		this.setLocation(GUI_X_LOCATION, GUI_Y_LOCATION);
//		this.setSize(guiWidth, guiHeight);
////		this.setTitle(EMULATOR_NAME);
////		this.setResizable(false);
//
//		this.setVisible(true);
//		this.requestFocus();
//
//		// Start the emulator
//		emu = new Emulator(this);
//		new Thread(emu).start();
//	}
//
//	private JPanel getStartupScreen() {
//		// Create startup screen
//		StartupPanel startup = new StartupPanel();
//		startup.setSize(720, 400);
////		startup.setImage(this.getImageFromFile(EMULATOR_SPLASHSCREEN_IMAGE));
//
//		return startup;
//	}
//
//	public void setScreen(JPanel screen) {
//		// Replace current canvas with new one
//		screenPane.removeAll();
//		screenPane.add(screen);
//
//		// Attach current screen to given screen
//		this.screen = screen;
//
//		// Update panel
//		this.updateScreenPanel();
//	}
//
//	private BufferedImage getImageFromFile(String path) {
//		BufferedImage image = null;
//		try {
//			image = ImageIO.read(new File(path));
//		} catch (IOException e) {
//			System.out.println("GUI error: problem during loading image.");
//		}
//		return image;
//	}
//
//	protected void updateScreenPanel() {
//		// Repaint canvas
//		screenPane.setSize(screen.getWidth(), screen.getHeight());
//		screen.repaint();
//	}
//
//	private void exitDioscuri() {
////		dispose();
//		System.exit(0);
//	}
//
//}
