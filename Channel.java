import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Channel {
	String channelName;
	boolean isChannel=false;
	final JTextArea channelTextArea=new JTextArea();
	JScrollPane channelTextAreaScrollPane=new JScrollPane(channelTextArea);
	JPanel channelPanel=new JPanel();
	public Channel(){
		channelTextArea.setFont(new Font("MonoSpaced", Font.BOLD, 12));
		channelTextArea.setLineWrap(true);
		channelTextArea.setWrapStyleWord(true);
		channelTextArea.setEditable(false);
		channelPanel.setLayout(new BorderLayout());
		channelPanel.add(channelTextAreaScrollPane, BorderLayout.CENTER);
	}
	public void setChannelName(String n) {
		channelName=n;
	}
	public String getChannelName() {
		return(channelName);
	}
	public void setIsChannel(boolean b) {
		isChannel=b;
	}
	public boolean getIsChannel() {
		return(isChannel);
	}
	// sudedu teksta i teksto lauka
	public void addTextInTextArea(String n) {
		channelTextArea.append(n);
		channelTextArea.setCaretPosition(channelTextArea.getDocument().getLength());
	}
	public JPanel getChannelPanel() {
		return(channelPanel);
	}
	
}
