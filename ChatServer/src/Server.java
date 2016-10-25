
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ICKelin
 */
public class Server extends javax.swing.JFrame {

    private final static int DEFAULT_PORT = 7898;
    private final static int MAX_PACKET_SIZE = 65507;
    private static List<User> users = new ArrayList<User>();

    class RecvThread extends Thread {

        private Server parent;
        //服务器线程
        public RecvThread(Server parent) {
            this.parent = parent;
        }

        public void run() {
            int port = DEFAULT_PORT;
            byte[] buffer = new byte[MAX_PACKET_SIZE];

            try {
                DatagramSocket server = new DatagramSocket(port);
                this.parent.jTextArea1.append("服务器启动，监听端口" + port + "\n");

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    server.receive(packet);
                    String s = new String(packet.getData(), packet.getOffset(), packet.getLength(), "UTF-8");
                    Msg msg = new Msg();
                    String spls[] = s.split("\n");

                    //通用协议
                    msg.username = spls[0].substring(spls[0].indexOf(":") + 1);
                    msg.type = spls[1].substring(spls[1].indexOf(":") + 1);
                    msg.msg = spls[2].substring(spls[2].indexOf(":") + 1);
                    msg.to = spls[3].substring(spls[3].indexOf(":") + 1);

                    //登录T_LOGIN
		            //登录处理，记录登录用户，向在线用户广播一条消息，给当前登录用户发送确认信息
                    if (msg.type.equals("T_LOGIN")) {
                        User u = new User();
                        u.username = msg.username;
                        u.client = packet;
                        users.add(u);
			
                        this.parent.jTextArea1.append("用户:" + u.username + "登录\n");
                        for (int i = 0; i < users.size(); i++) {
                            String inline;
                            DatagramPacket client = users.get(i).client;
                            //不是当前用户
                            if (!users.get(i).username.equals(msg.username)) {
                                inline = "username:" + msg.username + "\ntype:T_LOGIN\nMSG:\nto:\n";
                                byte[] data = inline.getBytes("UTF-8");
                                DatagramPacket thepacket = new DatagramPacket(data, data.length, client.getAddress(), client.getPort());
                                server.send(thepacket);
                            }
                            inline = "username:" + users.get(i).username + "\ntype:T_ACK\nMSG:\nto:\n";
                            //发给当前用户在线账户信息
                            byte[] data = inline.getBytes("UTF-8");
                            DatagramPacket thepacket = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
                            server.send(thepacket);
                        }
                    }
		    //发消息T_MSG
                    //向在线用户广播一条消息
                    else if (msg.type.equals("T_MSG")) {
                        String inline = "username:" + msg.username + "\ntype:T_MSG\nMSG:" + msg.msg + "\nto:\n";
                        this.parent.jTextArea1.append("用户:" + msg.username + "说:" + msg.username + "\n");
                        for (int i = 0; i < users.size(); i++) {
                            DatagramPacket client = users.get(i).client;
                            byte[] data = inline.getBytes("UTF-8");
                            DatagramPacket thepacket = new DatagramPacket(data, data.length, client.getAddress(), client.getPort());
                            server.send(thepacket);
                        }
                    } 
        		    //退出T_QUIT
        		    //删除在线用户，广播用户退出消息
                    else if (msg.type.equals("T_QUIT")) {
                        String inline = "username:" + msg.username + "\ntype:T_QUIT\nMSG:" + msg.msg + "\nto:\n";
                        System.out.println(msg.username + "下线\n");
                        this.parent.jTextArea1.append("用户:" + msg.username + "下线\n");
                        for (int i = 0; i < users.size(); i++) {
                            if (users.get(i).username.equals(msg.username)) {
                                users.remove(i);
                                break;
                            }
                        }
                        for (int i = 0; i < users.size(); i++) {
                            DatagramPacket client = users.get(i).client;
                            byte[] data = inline.getBytes("UTF-8");
                            DatagramPacket thepacket = new DatagramPacket(data, data.length, client.getAddress(), client.getPort());
                            server.send(thepacket);
                        }
                    }
                }
            } catch (SocketException e) {
               e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Server() {
        initComponents();
        this.setVisible(true);
        Server.RecvThread recv = new RecvThread(this);
        recv.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("服务器");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
