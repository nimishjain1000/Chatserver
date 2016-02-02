import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

class MyThread
implements Runnable {
    Socket s;
    ArrayList al;
    ArrayList users;
    String username;

    MyThread(Socket socket, ArrayList arrayList, ArrayList arrayList2) {
        this.s = socket;
        this.al = arrayList;
        this.users = arrayList2;
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            this.username = dataInputStream.readUTF();
            arrayList.add(socket);
            arrayList2.add(this.username);
            this.tellEveryOne("****** " + this.username + " Logged in at " + new Date() + " ******");
            this.sendNewUserList();
        }
        catch (Exception var4_5) {
            System.err.println("MyThread constructor  " + var4_5);
        }
    }

    public void run() {
        try {
            String string;
            DataInputStream dataInputStream = new DataInputStream(this.s.getInputStream());
            while (!(string = dataInputStream.readUTF()).toLowerCase().equals("@@logoutme@@:")) {
                this.tellEveryOne(this.username + " said: " + " : " + string);
            }
            DataOutputStream dataOutputStream = new DataOutputStream(this.s.getOutputStream());
            dataOutputStream.writeUTF("@@logoutme@@:");
            dataOutputStream.flush();
            this.users.remove(this.username);
            this.tellEveryOne("****** " + this.username + " Logged out at " + new Date() + " ******");
            this.sendNewUserList();
            this.al.remove(this.s);
            this.s.close();
        }
        catch (Exception var2_2) {
            System.out.println("MyThread Run" + var2_2);
        }
    }

    public void sendNewUserList() {
        this.tellEveryOne("updateuserslist:" + this.users.toString());
    }

    public void tellEveryOne(String string) {
        Iterator iterator = this.al.iterator();
        while (iterator.hasNext()) {
            try {
                Socket socket = (Socket)iterator.next();
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(string);
                dataOutputStream.flush();
            }
            catch (Exception var3_4) {
                System.err.println("TellEveryOne " + var3_4);
            }
        }
    }
}