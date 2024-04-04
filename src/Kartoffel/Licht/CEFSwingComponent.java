package Kartoffel.Licht;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class CEFSwingComponent extends JComponent{
	
	public static double SCROLL_AMP = 200;
	
	
	
	private static final long serialVersionUID = 1L;

	private volatile BufferedImage toDraw;
	private int browserID = 0;
	
	private double lts = 0;
	private int mouseX, mouseY;
	
	Rectangle pbounds = new Rectangle();
	JFrame parent;
	
	boolean isFullscreen = false;
	
	public CEFSwingComponent() {
		this("https://www.google.com/", null);
	}
	
	public CEFSwingComponent(String url) {
		this(url, null);
	}
	
	public CEFSwingComponent(JFrame parent) {
		this("https://www.google.com/", parent);
	}
	
	protected void onProgressChanged(double d) {
		
	}
	
	protected void onURLChanged(String newURL) {
		
	}
	
	
	private void setFullscreen(boolean fullscreen) {
		if(isFullscreen == fullscreen)
			return;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		if(parent != null) {
			if(fullscreen) {
				pbounds.setBounds(parent.getBounds());
				parent.setBounds(0, 0, width, height);
			}else {
				parent.setBounds(pbounds);
			}
		}
		isFullscreen = fullscreen;
	}
	
	public CEFSwingComponent(String url, JFrame parent) {
		this.parent = parent;
		CEFInstance.register(browserID = CEFFunk.createBrowser(url), new CEFFunk() {
			
			@Override
			public void paint(int browserID, long buffer, int width, int height) {
				ByteBuffer bb = createNew(buffer, width*height*4);
				toDraw = fromNativeByteBuffer(bb, width, height);
			}
			
			@Override
			public int[] windowSize(int browserID) {
				return new int[] {getWidth(), getHeight()};
			}
			@Override
			public void titleChanged(int browserID, String title) {
				if(parent != null)
					parent.setTitle(title);
			}
			@Override
			public void faviconChanged(int browserID, String[] urls) {
				
			}
			
			@Override
			public void fullscreenModeChanged(int browserID, boolean fullscreen) {
				setFullscreen(fullscreen);
			}
			@Override
			public void cursorChange(int browserID, int cursor) {
				setCursor(Cursor.getPredefinedCursor(CEFFunk.CURSORS.toSwingCursor(cursor)));
			}
			@Override
			public void tooltip(int browserID, String tooltip) {
				setToolTipText(tooltip);
			}
			@Override
			public void loadingProcessChanged(int browserID, double d) {
				onProgressChanged(d);
			}
			
			@Override
			public void addressChanged(int browserID, String url) {
				onURLChanged(url);
			}
			
		});
		this.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				CEFFunk.setFocus(browserID, false);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				CEFFunk.setFocus(browserID, true);
			}
		});
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				CEFFunk.sendMouseClickEvent(browserID, e.getX(), e.getY(), 0, e.getButton() == 1 ? CEFFunk.MBT_LEFT : e.getButton() == 2 ? CEFFunk.MBT_MIDDLE : CEFFunk.MBT_RIGHT, true, 1);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				grabFocus();
				mouseX = e.getX();
				mouseY = e.getY();
				CEFFunk.sendMouseClickEvent(browserID, e.getX(), e.getY(), 0, e.getButton() == 1 ? CEFFunk.MBT_LEFT : e.getButton() == 2 ? CEFFunk.MBT_MIDDLE : CEFFunk.MBT_RIGHT, false, 1);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				lts += -e.getPreciseWheelRotation()*SCROLL_AMP;
			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				CEFFunk.sendMouseMoveEvent(browserID, e.getX(), e.getY(), 0, false);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				CEFFunk.sendMouseMoveEvent(browserID, e.getX(), e.getY(), 0, false);
			}
		});
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) { //Keycode not available here
				if(e.getKeyChar() != '')
					CEFFunk.sendKeyEvent(browserID, e.getKeyChar(), e.getModifiersEx(), e.getKeyChar(), CEFFunk.KEYEVENT_CHAR);
			}
			
			@Override
			public void keyReleased(KeyEvent e) { //Keychar not available here
				CEFFunk.sendKeyEvent(browserID, '\0', e.getModifiersEx(), e.getKeyCode(), CEFFunk.KEYEVENT_KEYUP);
			}
			
			@Override
			public void keyPressed(KeyEvent e) { //Keychar not available here
				if(e.getKeyCode() != '.' && e.getKeyCode() != 525) //Bug where '.' deletes it self
					CEFFunk.sendKeyEvent(browserID, '\0', e.getModifiersEx(), e.getKeyCode(), CEFFunk.KEYEVENT_KEYDOWN);
				if(e.getKeyCode() == 10)
					CEFFunk.sendKeyEvent(browserID, '\0', e.getModifiersEx(), 525, CEFFunk.KEYEVENT_KEYDOWN);
//				if(e.getKe)
				if(e.getKeyChar() == '')
					CEFFunk.selectall(browserID);
				if(e.getKeyChar() == '')
					CEFFunk.copy(browserID);
				if(e.getKeyChar() == '')
					CEFFunk.paste(browserID);
				if(e.getKeyChar() == '')
					CEFFunk.delete(browserID);
				if(e.getKeyChar() == '')
					CEFFunk.undo(browserID);
				if(e.getKeyChar() == '' && e.getModifiersEx() == 192)
					CEFFunk.redo(browserID);
				if(e.getKeyCode() == KeyEvent.VK_F11)
					setFullscreen(!isFullscreen);
			}
		});
		CEFFunk.setFocus(browserID, true);
	}
	private Rectangle pb = new Rectangle();
	private double delta;
	private double a;
	private boolean hadfocus = false;
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.RED);
		g.drawLine(0, 0, getWidth(), getHeight());
		if(toDraw != null)
			g.drawImage(toDraw, 0, 0, null);
		
		if(pb.width != getWidth() || pb.height != getHeight())
			CEFFunk.wasResized(browserID);
		
		pb.setBounds(getBounds());
		
		if(Math.abs(lts) > 0.01) {
			double a = lts;
			lts *= Math.pow(0.98, delta);
			CEFFunk.sendMouseScrollEvent(browserID, mouseX, mouseY, 0, 0, (int) (a-lts));
		}
		delta = System.currentTimeMillis()-a;
		a = System.currentTimeMillis();
		
		if(!hadfocus && !CEFFunk.isLoading(browserID)) {
			CEFFunk.setFocus(browserID, true);
			hadfocus = true;
		}
		
	}
	
	public static BufferedImage fromNativeByteBuffer(ByteBuffer buff, int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] i = new int[width*height];
		buff.asIntBuffer().get(i);
		for(int l = 0; l < i.length; l++)
			i[l] = Integer.reverseBytes(i[l]);
		bi.setRGB(0, 0, width, height, i, 0, width);
		return bi;
	}
	
	public int getBrowserID() {
		return browserID;
	}

}
