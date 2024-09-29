package BRM;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Bookframe {
    Connection con = null;

    PreparedStatement ps;

    JFrame frame = new JFrame("Book Recording");
    JPanel insertpanel, viewpanel;
    JTabbedPane tabbedpane = new JTabbedPane();
    JLabel l1, l2, l3, l4, l5;
    JTextField t1, t2, t3, t4, t5;
    JButton savebutton, updatebutton, deletebutton;
    JTable table;
    JScrollPane scrollpane;
    DefaultTableModel tm;
    String[] colname = {"Book Id", "Title", "Price", "Author", "Publisher"};

    public Bookframe() {
        getconnectionmysql();
        intitcomponent();
    }

    public void getconnectionmysql() {
        String url = "jdbc:mysql://localhost:3306/library";
        try {
            con = DriverManager.getConnection(url, "root", "Aashu@123");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (con != null)
            System.out.println("successfully connected to surver");
    }

    public void intitcomponent() {
        l1 = new JLabel();
        l1.setText("Bookid");

        l2 = new JLabel();
        l2.setText("Title");
        l3 = new JLabel();
        l3.setText("Price");
        l4 = new JLabel();
        l4.setText("Author");
        l5 = new JLabel();
        l5.setText("Publicer");

        t1 = new JTextField();
        t2 = new JTextField();
        t3 = new JTextField();
        t4 = new JTextField();
        t5 = new JTextField();

        savebutton = new JButton("save");
        savebutton.addActionListener(new insertbookrecord());

        l1.setBounds(100, 100, 100, 20);
        l2.setBounds(100, 150, 100, 20);
        l3.setBounds(100, 200, 100, 20);
        l4.setBounds(100, 250, 100, 20);
        l5.setBounds(100, 300, 100, 20);

        insertpanel = new JPanel();
        insertpanel.add(l1);
        insertpanel.add(l2);
        insertpanel.add(l3);
        insertpanel.add(l4);
        insertpanel.add(l5);

        t1.setBounds(250, 100, 100, 20);
        t2.setBounds(250, 150, 100, 20);
        t3.setBounds(250, 200, 100, 20);
        t4.setBounds(250, 250, 100, 20);
        t5.setBounds(250, 300, 100, 20);

        insertpanel.add(t1);
        insertpanel.add(t2);
        insertpanel.add(t3);
        insertpanel.add(t4);
        insertpanel.add(t5);

        savebutton.setBounds(190, 370, 100, 30);
        insertpanel.add(savebutton);

        ArrayList<Book> booklist=fetchbookrecord();
        setdataontable(booklist);
        updatebutton=new JButton("update book");
        updatebutton.addActionListener(new updatebookrecord());
        deletebutton=new JButton("delete book");
        deletebutton.addActionListener(new deletebookrecord());
        viewpanel =new JPanel();
        viewpanel.add(updatebutton);
        viewpanel.add(deletebutton);
        scrollpane=new JScrollPane(table);
        viewpanel.add(scrollpane);

        tabbedpane.addChangeListener(new tabchangehandler());


        insertpanel.setLayout(null);
        tabbedpane.add(insertpanel);
        tabbedpane.add(viewpanel);
        frame.add(tabbedpane);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
    }

    void setdataontable(ArrayList<Book> booklist){
        Object [][] obj=new Object[booklist.size()][5];
        for(int i=0;i<booklist.size();i++){
            obj[i][0]=booklist.get(i).getBookId();
            obj[i][1]=booklist.get(i).getTittle();
            obj[i][2]=booklist.get(i).getPrice();
            obj[i][3]=booklist.get(i).getAuthor();
            obj[i][4]=booklist.get(i).getPublisher();
        }
        table=new JTable();
        tm=new DefaultTableModel();
        tm.setColumnCount(5);
        tm.setRowCount(booklist.size());
        tm.setColumnIdentifiers(colname);
        for(int i=0;i<booklist.size();i++){
            tm.setValueAt(obj[i][0],i,0);
            tm.setValueAt(obj[i][1],i,1);
            tm.setValueAt(obj[i][2],i,2);
            tm.setValueAt(obj[i][3],i,3);
            tm.setValueAt(obj[i][4],i,4);
        }
        table.setModel(tm);
    }
    void updatetable(ArrayList<Book> booklist){
        Object [][] obj=new Object[booklist.size()][5];
        for(int i=0;i<booklist.size();i++){
            obj[i][0]=booklist.get(i).getBookId();
            obj[i][1]=booklist.get(i).getTittle();
            obj[i][2]=booklist.get(i).getPrice();
            obj[i][3]=booklist.get(i).getAuthor();
            obj[i][4]=booklist.get(i).getPublisher();
        }

        tm.setRowCount(booklist.size());
        for(int i=0;i<booklist.size();i++){
            tm.setValueAt(obj[i][0],i,0);
            tm.setValueAt(obj[i][1],i,1);
            tm.setValueAt(obj[i][2],i,2);
            tm.setValueAt(obj[i][3],i,3);
            tm.setValueAt(obj[i][4],i,4);
        }
        table.setModel(tm);
    }
    ArrayList<Book> fetchbookrecord(){
        ArrayList<Book>booklist=new ArrayList<Book>();
        String q="select * from book";
        try{
            ps=con.prepareStatement(q);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                Book b=new Book();
                b.setBookId(rs.getInt(1));
                b.setTittle(rs.getString(2));
                b.setPrice(rs.getInt(3));
                b.setAuthor(rs.getString(4));
                b.setPublisher(rs.getString(5));
                booklist.add(b);

            }

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return booklist;

    }
    class insertbookrecord implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Book b1=readfromdata();
            String q="insert into book(Bookid,Tittle,Price,Author,Publisher) values(?,?,?,?,?)";
            try{

                ps=con.prepareStatement(q);
                ps.setInt(1,b1.getBookId());
                ps.setString(2,b1.getTittle());
                ps.setDouble(3,b1.getPrice());
                ps.setString(4,b1.getAuthor());
                ps.setString(5,b1.getPublisher());
                ps.execute();
                t1.setText(" ");
                t2.setText(" ");
                t3.setText(" ");
                t4.setText(" ");
                t5.setText(" ");


            }
            catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
        Book readfromdata(){
            Book b1=new Book();
            b1.setBookId(Integer.parseInt(t1.getText()));
            b1.setTittle(t2.getText());
            b1.setPrice(Integer.parseInt(t3.getText()));
            b1.setAuthor(t4.getText());
            b1.setPublisher(t5.getText());
            return b1;
        }
    }
    class tabchangehandler implements ChangeListener{

        @Override
        public void stateChanged(ChangeEvent e) {
            int index=tabbedpane.getSelectedIndex();
            if(index==0){
                System.out.println("index");
            }
            if(index==1){
                ArrayList<Book> booklist=fetchbookrecord();
                updatetable(booklist);
            }
        }
    }
    class updatebookrecord implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Book> updatedbooklist=readtabledata();
            String q="update book set tittle=?,price=?,author=?,publisher=? where bookid=?";
            try{
                ps=con.prepareStatement(q);
                for(int i=0;i<updatedbooklist.size();i++){
                    ps.setString(1,updatedbooklist.get(i).getTittle());
                    ps.setDouble(2,updatedbooklist.get(i).getPrice());
                    ps.setString(3,updatedbooklist.get(i).getAuthor());
                    ps.setString(4,updatedbooklist.get(i).getPublisher());
                    ps.setInt(5,updatedbooklist.get(i).getBookId());
                    ps.executeUpdate();
                }
            }
            catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
        ArrayList<Book>readtabledata(){
            ArrayList<Book>updadtebooklist=new ArrayList<Book>();
            for(int i=0;i<table.getRowCount();i++){
                Book b=new Book();
                b.setBookId(Integer.parseInt(table.getValueAt(i,0).toString()));
                b.setTittle(table.getValueAt(i,1).toString());
                b.setPrice(Double.parseDouble(table.getValueAt(i,2).toString()));
                b.setAuthor(table.getValueAt(i,3).toString());
                b.setPublisher(table.getValueAt(i,4).toString());
                updadtebooklist.add(b);

            }
            return updadtebooklist;
        }
    }
    class deletebookrecord implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            int rowNo=table.getSelectedRow();
            if(rowNo!=-1){
                int id=(int)table.getValueAt(rowNo,0);
                String q="delete from book where bookid=?";
                try {
                    ps = con.prepareStatement(q);
                    ps.setInt(1,id);
                    ps.execute();
                }
                catch (SQLException ex){
                    System.out.println(ex.getMessage());
                }
                finally {
                    ArrayList<Book>booklist=fetchbookrecord();
                    updatetable(booklist);
                }

            }
        }
    }
    }
