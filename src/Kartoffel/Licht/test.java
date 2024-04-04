package Kartoffel.Licht;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
/**
 * For testing
 */
public class test {

	static CEFSwingComponent ssc = null;
	static JTextField url;
	static JComponent table;
	static JButton back, forward, reload;
	static JProgressBar bar;
	public static void main(String[] args) throws Exception {
//		String ptj = new File(test.class.getProtectionDomain().getCodeSource().getLocation()
//			    .toURI()).getPath();
		CEFInstance.init("C:\\Users\\Anwender\\eclipse-workspace\\Games\\CEFJB\\tempcache\\", "cefclient.exe");
		JFrame f = new JFrame();
		f.setLayout(new BorderLayout());
		
		ssc = new CEFSwingComponent(f) {
			private static final long serialVersionUID = 1234L;
			@Override
			protected void onURLChanged(String newURL) {
				url.setText(newURL);
			}
			@Override
			protected void onProgressChanged(double d) {
				back.setEnabled(CEFFunk.canGoBack(ssc.getBrowserID()));
				forward.setEnabled(CEFFunk.canGoForward(ssc.getBrowserID()));
				bar.setValue((int) (d*100));
			}
		};
		url = new JTextField();
		url.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CEFFunk.loadURL(ssc.getBrowserID(), url.getText());
			}
		});
		
		bar = new JProgressBar(0, 100);
		
		table = new JComponent() {private static final long serialVersionUID = 1L;};
		table.setLayout(new BoxLayout(table, 0));
		reload = new JButton("R");
		reload.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CEFFunk.reload(ssc.getBrowserID(), false);
			}
		});
		back = new JButton("B");
		back.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CEFFunk.goBack(ssc.getBrowserID());
			}
		});
		forward = new JButton("F");
		forward.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CEFFunk.goForward(ssc.getBrowserID());
			}
		});
		table.add(back);
		back.setFocusable(false);
		table.add(forward);
		forward.setFocusable(false);
		table.add(reload);
		reload.setFocusable(false);
		table.add(url);
		f.add(ssc, BorderLayout.CENTER);
		f.add(table, BorderLayout.NORTH);
		f.add(bar, BorderLayout.SOUTH);
		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int width = (int) screenSize.getWidth();
			int height = (int) screenSize.getHeight();
			f.setBounds(width/2-800/2, height/2-600/2, 800, 600);
		}
		f.setVisible(true);
		ssc.setFocusable(true);
		while(f.isVisible()) {
			CEFInstance.doMessageLoopWork();
			f.repaint();
		}
		CEFInstance.free();
		f.dispose();
	}
	

}
