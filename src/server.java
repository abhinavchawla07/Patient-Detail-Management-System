
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hp
 */
public class server {

    public static void main(String[] args) {
        new server();
    }
    DateTimeFormatter dtf;
    LocalDate ld;
    String date;
    String doc;
    ArrayList<pat_doc> al=new ArrayList<>();
    public server() {
        this.dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.ld = LocalDate.now();
        date = dtf.format(ld);
        try {
            ServerSocket sersock = new ServerSocket(8000);

//            System.out.println("Connection accepted");
            while (true) {
                Socket sock = sersock.accept();

                new Thread(new clienthandler(sock)).start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class clienthandler implements Runnable {

        Socket sock;

        clienthandler(Socket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            try {
                DataOutputStream dos;
                dos = new DataOutputStream(sock.getOutputStream());

                DataInputStream dis;
                dis = new DataInputStream(sock.getInputStream());

//                dos.writeUTF("hello client");
                while (true) {
                    String s = dis.readUTF();
                    System.out.println("" + s);
                    if (s.equals("rlogin request")) {
                        String username = dis.readUTF();
                        String password = dis.readUTF();

                        //JDBC
                        Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
//            System.out.println("Statement Created");

                        ResultSet rs = stmt.executeQuery("select * from receptionist_db where binary USERNAME='" + username + "' and binary PASSWORD='" + password + "'");
                        dos.writeUTF("rlogin response");
                        if (rs.next()) {
//                                System.out.println("hello in if");
                            dos.writeUTF("success");

                        } else {
//                                System.out.println("hello on else");
                            dos.writeUTF("failed");
                        }

                        rs.close();
                        stmt.close();
                        conn.close();

                    } else if (s.equals("dlogin request")) {
                        String username = dis.readUTF();
                        String password = dis.readUTF();

                        //JDBC
                        Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
//            System.out.println("Statement Created");

                        ResultSet rs = stmt.executeQuery("select * from doctor_db where binary USERNAME='" + username + "' and binary PASSWORD='" + password + "'");
                        dos.writeUTF("dlogin response");
                        if (rs.next()) {
//                                System.out.println("hello in if");
                            dos.writeUTF("success");
                            dos.writeUTF(rs.getString("NAME"));

                        } else {
//                                System.out.println("hello on else");
                            dos.writeUTF("failed");
                        }

                        rs.close();
                        stmt.close();
                        conn.close();

                    } else if (s.equals("pdetails add")) {
                        String name = dis.readUTF();
                        int age = Integer.parseInt(dis.readUTF());
                        String gender = dis.readUTF();
                        String phone = dis.readUTF();
                        String address = dis.readUTF();
                        doc=freedoctor();

                        try {
                            Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                    ResultSet.CONCUR_UPDATABLE);
//            System.out.println("Statement Created");
//
                                ResultSet rs = stmt.executeQuery("select * from patient_db where PHONE_NUMBER='"+phone+"'");
                            Statement stmt2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                    ResultSet.CONCUR_UPDATABLE);
                            ResultSet c = stmt2.executeQuery("select count(*) as abc from patient_db");
                            c.next();
                            int rowcount = c.getInt("abc") + 1;
                                        System.out.println(rowcount);
                            if (rs.next()) {
                                dos.writeUTF("already exists");
                            } else {
                                rs.moveToInsertRow();
                                rs.updateInt("REGISTRATION_NO", rowcount);
                                rs.updateString("NAME", name);
                                rs.updateInt("AGE", age);
                                rs.updateString("GENDER",gender);
                                rs.updateString("PHONE_NUMBER", phone);
                                rs.updateString("ADDRESS", address);
                                rs.updateString("DOCTOR", doc);
                                rs.updateString("CURRENT_DATE", date);
                                rs.updateString("CHIEF_COMPLAINT", "");
                                rs.updateString("INVESTIGATION", "");
                                rs.updateString("DIAGNOSIS", "");
                                rs.updateString("PRESCRIPTION", "");
                                rs.updateString("NEXT_APPOINTMENT_DATE", "");
                                rs.insertRow();

                                dos.writeUTF("added successfully");
                                dos.writeUTF(rowcount+"");
                                dos.writeUTF(doc);
                                
                            }

                        } catch (Exception e) {
                        }
                    } else if (s.equals("rsearch patient")) {
                        int reg = Integer.parseInt(dis.readUTF());
                        Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
//            System.out.println("Statement Created");

                        ResultSet rs = stmt.executeQuery("select * from patient_db where REGISTRATION_NO=" + reg + "");

                        if (rs.next()) {
                            dos.writeUTF("Patient Found");
                            dos.writeUTF(rs.getInt("REGISTRATION_NO")+"");
                            dos.writeUTF(rs.getString("NAME"));
                            dos.writeUTF(rs.getInt("AGE") + "");
                            dos.writeUTF(rs.getString("GENDER"));
                            dos.writeUTF(rs.getString("PHONE_NUMBER"));
                            dos.writeUTF(rs.getString("ADDRESS"));
                            dos.writeUTF(rs.getString("CURRENT_DATE"));
                            dos.writeUTF(rs.getString("DOCTOR"));
                            dos.writeUTF(rs.getString("CHIEF_COMPLAINT"));
                            dos.writeUTF(rs.getString("INVESTIGATION") );
                            dos.writeUTF(rs.getString("DIAGNOSIS") );
                            dos.writeUTF(rs.getString("PRESCRIPTION"));
                            dos.writeUTF(rs.getString("NEXT_APPOINTMENT_DATE"));
                        } else {
                            dos.writeUTF("not found");
                        }

                    }
                    else if (s.equals("dsearch patient")) {
                        
                        String uname=dis.readUTF();
                        int reg = Integer.parseInt(dis.readUTF());
                        Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
//            System.out.println("Statement Created");

                        ResultSet rs = stmt.executeQuery("select * from patient_db where REGISTRATION_NO=" + reg );
                        if (rs.next()) {
                            if(rs.getString("DOCTOR").equals(uname)){
                            dos.writeUTF("patient found");
                            dos.writeUTF(rs.getInt("REGISTRATION_NO") + "");
                            dos.writeUTF(rs.getString("NAME"));
                            dos.writeUTF(rs.getInt("AGE") + "");
                            dos.writeUTF(rs.getString("GENDER"));
                            dos.writeUTF(rs.getString("PHONE_NUMBER"));
                            dos.writeUTF(rs.getString("ADDRESS"));
                            dos.writeUTF(rs.getString("CURRENT_DATE"));
                            dos.writeUTF(rs.getString("DOCTOR"));
                            dos.writeUTF(rs.getString("CHIEF_COMPLAINT"));
                            dos.writeUTF(rs.getString("INVESTIGATION"));
                            dos.writeUTF(rs.getString("DIAGNOSIS"));
                            dos.writeUTF(rs.getString("PRESCRIPTION"));
                            dos.writeUTF(rs.getString("NEXT_APPOINTMENT_DATE"));
                            }
                            else
                                dos.writeUTF("not allowed");
                        } else {
                            dos.writeUTF("not found");
                        }

                    }
                    else if(s.equals("rchangepassword"))
                    {
                        String username = dis.readUTF();
                        String old = dis.readUTF();
                        String newp = dis.readUTF();
                        
                        Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
//            System.out.println("Statement Created");

                        ResultSet rs = stmt.executeQuery("select * from receptionist_db where USERNAME = '" +username+ "' and PASSWORD ='" + old + "'");
//                        System.out.println("here");
//                        int x= stmt.executeUpdate("update receptionist_db set PASSWORD ='"+newp+"' where USERNAME = '" +username+ "' and  PASSWORD = '" + old + "' ");
//                        System.out.println("Updated "+x+" rows");
                       
                        if(rs.next())
                        {
                            rs.updateString("PASSWORD", newp);
                            rs.updateRow();
                            dos.writeUTF("rpwdchgsuccess");
                            
                            System.out.println("here");
                        }else
                        {
                            dos.writeUTF("invalid credentials");
                            System.out.println("falldown");
                        }
                    }
                    else if(s.equals("dchangepassword"))
                    {
                        String username = dis.readUTF();
                        String old = dis.readUTF();
                        String newp = dis.readUTF();
                        
                        Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
//            System.out.println("Statement Created");

                        ResultSet rs = stmt.executeQuery("select * from doctor_db where USERNAME = '" +username+ "' and  PASSWORD ='" + old + "'");
//                        System.out.println("here");
                       
                        if(rs.next())
                        {
                            rs.updateString("PASSWORD", newp);
                            rs.updateRow();
                            dos.writeUTF("dpwdchgsuccess");
                        }else
                        {
                            System.out.println("fail");
                            dos.writeUTF("invalid credentials");
                        }
                    }
                    else if(s.equals("r_update"))
                    {
                        String reg=dis.readUTF();
                        String name=dis.readUTF();
                        int age=Integer.parseInt(dis.readUTF());
                        String gender=dis.readUTF();
                        String phone=dis.readUTF();
                        String address=dis.readUTF();
                        
                        Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs=stmt.executeQuery("select * from patient_db where REGISTRATION_NO="+reg);
                        if(rs.next())
                        {
                            rs.updateString("NAME", name);
                            rs.updateInt("AGE", age);
                            rs.updateString("GENDER", gender);
                            rs.updateString("PHONE_NUMBER", phone);
                            rs.updateString("ADDRESS", address);
                            rs.updateRow();
                        }
                    }else if(s.equals("d_update"))
                    {
                        String reg=dis.readUTF();
                        String chiefc=dis.readUTF();
                        String inves=dis.readUTF();
                        String dia=dis.readUTF();
                        String pres=dis.readUTF();
                        String nextdate=dis.readUTF();
                        
                        Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs=stmt.executeQuery("select * from patient_db where REGISTRATION_NO="+reg);
                        if(rs.next())
                        {
                            rs.updateString("CHIEF_COMPLAINT", chiefc);
                            rs.updateString("INVESTIGATION", inves);
                            rs.updateString("DIAGNOSIS", dia);
                            rs.updateString("PRESCRIPTION", pres);
                            rs.updateString("NEXT_APPOINTMENT_DATE", nextdate);
                            rs.updateRow();
                        }
                        
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public String freedoctor()
    {
        String doc = null;
        al.clear();
        try {                        Class.forName("com.mysql.jdbc.Driver");
//            System.out.println("Driver Loading done");

                        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/patient_detail_management_system", "root", "");
//            System.out.println("Connection Created");

                        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
//            System.out.println("Statement Created");
                           ResultSet rs1=stmt.executeQuery("select USERNAME from doctor_db");
                           while(rs1.next())
                           {
                               al.add(new pat_doc(rs1.getString("USERNAME"), 0));
                           }
                           ResultSet rs2=stmt.executeQuery("select doctor from patient_db where NEXT_APPOINTMENT_DATE='"+date+"'");
                           while(rs2.next())
                           {
                               String md=rs2.getString("DOCTOR");
                               for(int i=0;i<al.size();i++)
                                   if(md.equals(al.get(i).doc))
                                       al.get(i).pat++;
                           }
                           int min=0;
                           for(int i=0;i<al.size();i++)
                           {
                               if(min>=al.get(i).pat)
                                   doc=al.get(i).doc;
                           }
                           System.out.println("Hello "+doc);

            
        } catch (Exception e) {
        }
        return doc;
    }
}
