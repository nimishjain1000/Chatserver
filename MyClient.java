import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class MyClient
implements ActionListener {
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    JButton sendButton;
    JButton logoutButton;
    JButton loginButton;
    JButton exitButton;
    JFrame chatWindow;
    JTextArea txtBroadcast;
    JTextArea txtMessage;
    JList usersList;

    public void displayGUI() {
        this.chatWindow = new JFrame();
        this.txtBroadcast = new JTextArea(5, 30);
        this.txtBroadcast.setEditable(false);
        this.txtMessage = new JTextArea(2, 20);
        this.usersList = new JList();
        this.sendButton = new JButton("Send");
        this.logoutButton = new JButton("Log out");
        this.loginButton = new JButton("Log in");
        this.exitButton = new JButton("Exit");
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add((Component)new JLabel("Broad Cast messages from all online users", 0), "North");
        jPanel.add((Component)new JScrollPane(this.txtBroadcast), "Center");
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new FlowLayout());
        jPanel2.add(new JScrollPane(this.txtMessage));
        jPanel2.add(this.sendButton);
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new FlowLayout());
        jPanel3.add(this.loginButton);
        jPanel3.add(this.logoutButton);
        jPanel3.add(this.exitButton);
        JPanel jPanel4 = new JPanel();
        jPanel4.setLayout(new GridLayout(2, 1));
        jPanel4.add(jPanel2);
        jPanel4.add(jPanel3);
        JPanel jPanel5 = new JPanel();
        jPanel5.setLayout(new BorderLayout());
        jPanel5.add((Component)new JLabel("Online Users", 0), "East");
        jPanel5.add((Component)new JScrollPane(this.usersList), "South");
        this.chatWindow.add((Component)jPanel5, "East");
        this.chatWindow.add((Component)jPanel, "Center");
        this.chatWindow.add((Component)jPanel4, "South");
        this.chatWindow.pack();
        this.chatWindow.setTitle("Login for Chat");
        this.chatWindow.setDefaultCloseOperation(0);
        this.chatWindow.setVisible(true);
        this.sendButton.addActionListener(this);
        this.logoutButton.addActionListener(this);
        this.loginButton.addActionListener(this);
        this.exitButton.addActionListener(this);
        this.logoutButton.setEnabled(false);
        this.loginButton.setEnabled(true);
        this.txtMessage.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent focusEvent) {
                MyClient.this.txtMessage.selectAll();
            }
        });
        this.chatWindow.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent windowEvent) {
                if (MyClient.this.s != null) {
                    JOptionPane.showMessageDialog(MyClient.this.chatWindow, "u r logged out right now. ", "Exit", 1);
                    MyClient.this.logoutSession();
                }
                System.exit(0);
            }
        });
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String string;
        JButton jButton = (JButton)actionEvent.getSource();
        if (jButton == this.sendButton) {
            if (this.s == null) {
                JOptionPane.showMessageDialog(this.chatWindow, "u r not logged in. plz login first");
                return;
            }
            try {
                this.dos.writeUTF(this.txtMessage.getText());
                this.txtMessage.setText("");
            }
            catch (Exception var3_3) {
                this.txtBroadcast.append("\nsend button click :" + var3_3);
            }
        }
        if (jButton == this.loginButton && (string = JOptionPane.showInputDialog(this.chatWindow, (Object)"Enter Your lovely nick name: ")) != null) {
            this.clientChat(string);
        }
        if (jButton == this.logoutButton && this.s != null) {
            this.logoutSession();
        }
        if (jButton == this.exitButton) {
            if (this.s != null) {
                JOptionPane.showMessageDialog(this.chatWindow, "u r logged out right now. ", "Exit", 1);
                this.logoutSession();
            }
            System.exit(0);
        }
    }

    public void logoutSession() {
        if (this.s == null) {
            return;
        }
        try {
            this.dos.writeUTF("@@logoutme@@:");
            Thread.sleep(500);
            this.s = null;
        }
        catch (Exception var1_1) {
            this.txtBroadcast.append("\n inside logoutSession Method" + var1_1);
        }
        this.logoutButton.setEnabled(false);
        this.loginButton.setEnabled(true);
        this.chatWindow.setTitle("Login for Chat");
    }

    public void clientChat(String string) {
        try {
            this.s = new Socket(InetAddress.getLocalHost(), 10);
            this.dis = new DataInputStream(this.s.getInputStream());
            this.dos = new DataOutputStream(this.s.getOutputStream());
            ClientThread clientThread = new ClientThread(this.dis, this);
            Thread thread = new Thread(clientThread);
            thread.start();
            this.dos.writeUTF(string);
            this.chatWindow.setTitle(string + " Chat Window");
        }
        catch (Exception var2_3) {
            this.txtBroadcast.append("\nClient Constructor " + var2_3);
        }
        this.logoutButton.setEnabled(true);
        this.loginButton.setEnabled(false);
    }

    public MyClient() {
        this.displayGUI();
    }

    public static void main(String[] arrstring) {
        new MyClient();
    }

}

