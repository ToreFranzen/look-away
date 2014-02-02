import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

/**
 * @author Tore
 *
 */
public class LookAway extends JFrame{

	private static final long serialVersionUID = 1L;
	private JLabel infoL;
	private JLabel timeL;
	private JLabel picL;
	private JButton continueB;
	private JSpinner spinner;
	private SpinnerModel spinnerModel;
	private SystemTray sysTray;
	private TrayIcon trayIcon;
	private PopupMenu menu;
	private MenuItem exitItem;
	private MenuItem showItem;
	private MenuItem timeLeftItem;
	private Date popUpTime = new Date(System.currentTimeMillis());
	private static final String bigPic ="crazy_eyes.jpg";
	private static final String smallPic ="SB.jpg";
	private static String timeLeftText;
	
	private Timer timer;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new LookAway();
			}
		});
	}
	
	public LookAway()
	{
		InitializeGui();
		InitializeTray();
	}

	private void InitializeGui() {
		infoL = new JLabel("Press Continue to Restart Timer", SwingConstants.CENTER);
		timeL = new JLabel("Minutes", SwingConstants.LEFT);
		spinnerModel = new SpinnerNumberModel(30, //initial value
					                                1, //min
					                                1000, //max
					                                1); 
		spinner = new JSpinner(spinnerModel);
		
		continueB = new JButton("Continue");
		continueB.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {        		
        		StartTimerAndHide();
        	}
        }); 		
		
		picL = new JLabel("", new ImageIcon(this.getClass().getResource(bigPic)), JLabel.CENTER);	
		
		JPanel panel = new JPanel();
		panel.add(infoL);
		panel.add(spinner);
		panel.add(timeL);
		panel.add(continueB);
		panel.add(picL);
		this.add(panel);
		
		this.setTitle("Look Away Foo!");
		this.setSize(230, 300);
		this.setVisible(true);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE); 
		this.setIconImage((new ImageIcon(this.getClass().getResource(smallPic))).getImage());
		
		addWindowListener(new WindowListener() {
			@Override
			public void windowIconified(WindowEvent e) {
				StartTimerAndHide();
			}
								
			@Override
			public void windowClosing(WindowEvent e) {
				StartTimerAndHide();				
			}
			
			public void windowOpened(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}			
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
	}
	
	private void InitializeTray(){
		
		if (SystemTray.isSupported()) {
            sysTray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon((new ImageIcon(this.getClass().getResource(smallPic))).getImage());

            menu = new PopupMenu();
            exitItem = new MenuItem("Exit");
            showItem = new MenuItem("Show");
            timeLeftItem = new MenuItem("Time Left: 0 sec");
            
            menu.add(timeLeftItem);
            menu.add(showItem);
            menu.addSeparator();
            menu.add(exitItem);
            
            trayIcon.setPopupMenu(menu);
            trayIcon.setImageAutoSize(true);
            
            try {
            	sysTray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
                return;
            }

            //add actionListener to second menu item
            showItem.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		ShowGui();
            	}
            });

            //add action listener to the item in the popup menu
            exitItem.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   System.exit(0);
               }
            });
            
            trayIcon.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                	SetTimeLeft();
                }
            });                       
        }
	}

	private void StartTimerAndHide()
	{		
		long breakTime = ((Integer)spinnerModel.getValue()).longValue() * 60000;
		popUpTime = new Date(System.currentTimeMillis() + breakTime);
		setVisible(false);			
		timer = new Timer();
		timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	ShowGui();
            }
        }, popUpTime);
	}

	private void ShowGui() {
		setVisible(true);
		setState(Frame.NORMAL);
		popUpTime.setTime(0);
		timer.cancel();		
	}

	private void SetTimeLeft(){
		long timeLeft = (popUpTime.getTime() - System.currentTimeMillis())/1000;
		
		if(timeLeft < 0)
		{
			timeLeftText = "Time Left: 0 sec";
		}
		else if(timeLeft < 60)
		{
			timeLeftText = "Time Left: "+timeLeft+" sec";			
		}
		else
		{
			timeLeftText = "Time Left: "+timeLeft/60+" min";
		}
		
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				timeLeftItem.setLabel(timeLeftText);
			}
		});
	}
}
