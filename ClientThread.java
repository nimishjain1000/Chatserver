import java.io.DataInputStream;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.JTextArea;

class ClientThread
implements Runnable {
    DataInputStream dis;
    MyClient client;

    ClientThread(DataInputStream dataInputStream, MyClient myClient) {
        this.dis = dataInputStream;
        this.client = myClient;
    }

    public void run() {
        String string = "";
        block2 : do {
            try {
                do {
                    if ((string = this.dis.readUTF()).startsWith("updateuserslist:")) {
                        this.updateUsersList(string);
                    } else {
                        if (string.equals("@@logoutme@@:")) break block2;
                        this.client.txtBroadcast.append("\n" + string);
                    }
                    int n = this.client.txtBroadcast.getLineStartOffset(this.client.txtBroadcast.getLineCount() - 1);
                    this.client.txtBroadcast.setCaretPosition(n);
                } while (true);
            }
            catch (Exception var2_3) {
                this.client.txtBroadcast.append("\nClientThread run : " + var2_3);
                continue;
            }
            break;
        } while (true);
    }

    public void updateUsersList(String string) {
        Vector<String> vector = new Vector<String>();
        string = string.replace("[", "");
        string = string.replace("]", "");
        string = string.replace("updateuserslist:", "");
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            vector.add(string2);
        }
        this.client.usersList.setListData(vector);
    }
}