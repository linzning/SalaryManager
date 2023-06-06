package SalaryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class Conn { // ������Conn
    public static Connection con; // ����Connection����
    public static String user;
    public static  String password;
    public Connection getConnection() { // ��������ֵΪConnection�ķ���
        try { // �������ݿ�����
            Class.forName("com.mysql.cj.jdbc.Driver");
            //System.out.println("���ݿ��������سɹ�");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        user = "root";//���ݿ��¼��
        password = "hehey666";//����
        try { // ͨ���������ݿ��URL��ȡ���ݿ����Ӷ���
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root", user, password);
            //System.out.println("���ݿ����ӳɹ�");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con; // ������Ҫ�󷵻�һ��Connection����
    }
}
public class salaryManager {
    public static void main(String[] args) {
        LoginFrame lf=new LoginFrame();
        lf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

//��¼����
class LoginFrame extends JFrame implements ActionListener{
    private JLabel l_user,l_pwd;  //�û�����ǩ�������ǩ
    private JTextField t_user; //�û����ı���
    private JPasswordField t_pwd; //�����ı���
    private JButton b_ok,b_cancel; //��¼��ť���˳���ť

    public LoginFrame(){
        super("��ӭʹ�ù��ʹ���ϵͳ!");
        l_user=new JLabel("�û�����",JLabel.RIGHT);
        l_pwd=new JLabel(" ���룺",JLabel.RIGHT);
        t_user=new JTextField(16);
        t_pwd=new JPasswordField(17);
        b_ok=new JButton("��¼");
        b_cancel=new JButton("�˳�");
        //���ַ�ʽFlowLayout��һ����������һ��
        Container c=this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(l_user);
        c.add(t_user);
        c.add(l_pwd);
        c.add(t_pwd);
        c.add(b_ok);
        c.add(b_cancel);
        //Ϊ��ť��Ӽ����¼�
        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);
        //�����С���ɵ���
        this.setResizable(false);
        this.setSize(250,150);
        //������ʾ����
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(b_cancel==e.getSource()){
            //����˳�����
            System.exit(0);
        }else if(b_ok==e.getSource()){
            boolean find=false;
            try{
                Conn c = new Conn(); // ����conn�����
                Connection connection=c.getConnection(); // �����������ݿ�ķ���
                Statement statement=c.con.createStatement();
                statement.execute("use salaryManager");
                ResultSet resultSet=statement.executeQuery("select * from loginInfo");
                //System.out.println("��ȡ�ɹ�");
                String inuid=t_user.getText();
                String inupw=new String(t_pwd.getPassword());
                while(resultSet.next()&&!find){
                    /*if(inuid.equals("")&&inupw.equals("")){
                        find=true;
                    }*/
                    if(inuid.equals(resultSet.getString("uid"))
                            &&inupw.equals(resultSet.getString("upw"))) {
                        find = true;
                    }
                }
                resultSet.close();
                statement.close();
                connection.close();
            }
            catch (SQLException sqle){
                sqle.printStackTrace();
            }
            //��Ӵ��룬��֤��ݳɹ�����ʾ������
            if(find){
                new MainFrame(t_user.getText().trim());
            }else{
                JOptionPane.showMessageDialog(
                        null,"�û��������ڻ��������","���������ʾ",JOptionPane.WARNING_MESSAGE);
                System.out.println("�û��������ڻ��������");
            }
        }
    }
}

//������
class MainFrame extends JFrame implements ActionListener{
    private JMenuBar mb=new JMenuBar();
    private JMenu m_system=new JMenu("ϵͳ����");
    private JMenu m_gongzi=new JMenu("���ʹ���");
    private JMenuItem mI[]={new JMenuItem("��������"),new JMenuItem("�˳�ϵͳ")};
    private JMenuItem m_FMEdit=new JMenuItem("���ʱ༭");
    private JLabel l_ps,l_empid,l_fromdate,l_todate;
    private JTextField t_empid,t_fromdate,t_todate;
    private JButton b_byempID,b_bydate,b_all;
    private JPanel p_condition,p_detail;
    private DefaultTableModel dtm;
    private JTable table;
    public  static int count=0;
    public String username;

    public MainFrame(String username)
    {
        super(username+"��ӭʹ����ҵԱ�����ʹ���ϵͳ!");
        this.username=username;
        Container c=this.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(mb,BorderLayout.NORTH);
        mb.add(m_system);
        mb.add(m_gongzi);
        m_system.add(mI[0]);
        m_system.add(mI[1]);
        m_gongzi.add(m_FMEdit);
        m_FMEdit.addActionListener(this);
        mI[0].addActionListener(this);
        mI[1].addActionListener(this);

        l_ps = new JLabel("ע�⣺ʱ���ʽ��Ϊ(����)�����磺201502");
        l_empid = new JLabel("Ա������");
        t_empid = new JTextField(8);
        l_fromdate=new JLabel("��ʼʱ��");
        l_todate=new JLabel("��ֹʱ��");
        t_fromdate=new JTextField(8);
        t_todate=new JTextField(8);
        b_byempID=new JButton("��ѯ");
        b_all=new JButton("��ѯ����");
        b_bydate=new JButton("��ѯ");

        p_condition=new JPanel();
        p_condition.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("�����ѯ����"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        p_condition.setLayout(new GridLayout(3,1));
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.add(l_empid);
        p1.add(t_empid);
        p1.add(b_byempID);
        p1.add(b_all);
        p2.add(l_fromdate);
        p2.add(t_fromdate);
        p2.add(l_todate);
        p2.add(t_todate);
        p2.add(b_bydate);
        p3.add(l_ps);
        p_condition.add(p1);
        p_condition.add(p2);
        p_condition.add(p3);
        c.add(p_condition,BorderLayout.CENTER);

        //���3����ť�ļ����¼�
        b_byempID.addActionListener(this);
        b_all.addActionListener(this);
        b_bydate.addActionListener(this);

        p_detail=new JPanel();
        p_detail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("������ϸ��Ϣ"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        String[] cloum = { "������Ϣ","Ա����", "����", "�»�������", "����","��н"};
        Object[][] row = new Object[50][6];
        dtm = new DefaultTableModel(row,cloum);
        table = new JTable(dtm);
        //table = new JTable(row, cloum);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(580,350));
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setViewportView(table);
        p_detail.add(scrollpane);

        c.add(p_detail,BorderLayout.SOUTH);

        //��Ӵ��룬������Ա��������Ϣ��ʾ�ڽ�������
        try{
            Conn c1 = new Conn(); // �����������
            Connection connection=c1.getConnection(); // �����������ݿ�ķ���
            Statement statement=c1.con.createStatement();
            statement.execute("use salaryManager");
            ResultSet resultSet=statement.executeQuery("select * from salary");
            int i=0;
            while(resultSet.next()){
                dtm.setValueAt(resultSet.getString("paydate"),i,0);
                dtm.setValueAt(resultSet.getString("sid"),i,1);
                dtm.setValueAt(resultSet.getString("sname"),i,2);
                /*row[i][0]=resultSet.getString("paydate");
                row[i][1]=resultSet.getString("sid");
                row[i][2]=resultSet.getString("sname");*/
                double a=resultSet.getDouble("gongzi");
                double b=resultSet.getDouble("jintie");
                dtm.setValueAt(a,i,3);
                dtm.setValueAt(b,i,4);
                dtm.setValueAt(a+b,i,5);
                /*row[i][3]=a;
                row[i][4]=b;
                row[i][5]=a+b;*/
                i++;
                count++;
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }

        this.setResizable(true);
        this.setSize(600,620);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
        this.setVisible(true);
    }

    public void clear(){
        for(int i=0;i<count;i++){
            for(int j=0;j<6;j++){
                dtm.setValueAt("",i,j);
            }
        }
    }

    public boolean date_check(String fromd,String tod){
        int fromy=Integer.parseInt(fromd.substring(0,4));
        int fromm=Integer.parseInt(fromd.substring(4));
        int toy=Integer.parseInt(tod.substring(0,4));
        int tom=Integer.parseInt(tod.substring(4));
        if(fromy<=toy&&fromm<=tom&&fromm>=0&&fromm<=12&&tom>=0&&tom<=12){
            return true;
        }
        return false;
    }


    public void actionPerformed(ActionEvent e) {
        Object temp=e.getSource();
        if(temp==mI[0]){
            ModifyPwdFrame mf=new ModifyPwdFrame(this.username);
        }else if(temp==mI[1]){
            //����˳�ϵͳ�Ĵ���
            this.dispose();
        }else if(temp==m_FMEdit){
            SalaryEditFrame bef=new SalaryEditFrame();
        }else if(temp==b_byempID)  //����Ա�����Ų�ѯ
        {
            //��Ӵ���
            this.clear();
            try{
                Conn c1 = new Conn(); // �����������
                Connection connection=c1.getConnection(); // �����������ݿ�ķ���
                Statement statement=c1.con.createStatement();
                statement.execute("use salaryManager");
                String in_empid=t_empid.getText();
                ResultSet resultSet=statement.executeQuery("select * from salary where sid="+in_empid);
                int i=0;
                while(resultSet.next()){
                    dtm.setValueAt(resultSet.getString("paydate"),i,0);
                    dtm.setValueAt(resultSet.getString("sid"),i,1);
                    dtm.setValueAt(resultSet.getString("sname"),i,2);
                    double a=resultSet.getDouble("gongzi");
                    double b=resultSet.getDouble("jintie");
                    dtm.setValueAt(a,i,3);
                    dtm.setValueAt(b,i,4);
                    dtm.setValueAt(a+b,i,5);
                    i++;
                }
                resultSet.close();
                statement.close();
                connection.close();
            }
            catch (SQLException sqle){
                sqle.printStackTrace();
            }
        }
        else if(temp==b_all)  //��ѯ����Ա������
        {
            //��Ӵ���
            try{
                Conn c1 = new Conn(); // �����������
                Connection connection=c1.getConnection(); // �����������ݿ�ķ���
                Statement statement=c1.con.createStatement();
                statement.execute("use salaryManager");
                ResultSet resultSet=statement.executeQuery("select * from salary");
                int i=0;
                while(resultSet.next()){
                    dtm.setValueAt(resultSet.getString("paydate"),i,0);
                    dtm.setValueAt(resultSet.getString("sid"),i,1);
                    dtm.setValueAt(resultSet.getString("sname"),i,2);
                    double a=resultSet.getDouble("gongzi");
                    double b=resultSet.getDouble("jintie");
                    dtm.setValueAt(a,i,3);
                    dtm.setValueAt(b,i,4);
                    dtm.setValueAt(a+b,i,5);
                    i++;
                    count++;
                }
                resultSet.close();
                statement.close();
                connection.close();
            }
            catch (SQLException sqle){
                sqle.printStackTrace();
            }
        }
        else if(temp==b_bydate)     //������ʼʱ������ѯ
        {
            //��Ӵ���
            try{
                Conn c1 = new Conn(); // �����������
                Connection connection=c1.getConnection(); // �����������ݿ�ķ���
                Statement statement=c1.con.createStatement();
                statement.execute("use salaryManager");
                String in_fromdate=t_fromdate.getText();
                String in_todate=t_todate.getText();
                int int_fromdate=Integer.parseInt(in_fromdate);
                int int_todate=Integer.parseInt(in_todate);
                if(!date_check(in_fromdate,in_todate)){
                    JOptionPane.showMessageDialog(
                            null,"�·������������������","���������ʾ",JOptionPane.WARNING_MESSAGE);
                    System.out.println("���ڸ�ʽ����");
                }
                else{
                    this.clear();
                    ResultSet resultSet=statement.executeQuery(
                            "select * from salary where paydate>="+in_fromdate+" and paydate<="+in_todate);
                    int i=0;
                    while(resultSet.next()){
                        dtm.setValueAt(resultSet.getString("paydate"),i,0);
                        dtm.setValueAt(resultSet.getString("sid"),i,1);
                        dtm.setValueAt(resultSet.getString("sname"),i,2);
                        double a=resultSet.getDouble("gongzi");
                        double b=resultSet.getDouble("jintie");
                        dtm.setValueAt(a,i,3);
                        dtm.setValueAt(b,i,4);
                        dtm.setValueAt(a+b,i,5);
                        i++;
                    }
                    resultSet.close();
                }
                statement.close();
                connection.close();
            }
            catch (SQLException sqle){
                sqle.printStackTrace();
            }
        }
    }
}

//�޸��������
class ModifyPwdFrame extends JFrame implements ActionListener{
    private JLabel l_oldPWD,l_newPWD,l_newPWDAgain;
    private JPasswordField t_oldPWD,t_newPWD,t_newPWDAgain;
    private JButton b_ok,b_cancel;
    public String username;

    public ModifyPwdFrame(String username)
    {
        super("�޸�����");
        this.username=username;
        l_oldPWD=new JLabel("  ������:       ");
        l_newPWD=new JLabel("  �����룺      ");
        l_newPWDAgain=new JLabel("ȷ�������룺");
        t_oldPWD=new JPasswordField(15);
        t_newPWD=new JPasswordField(15);
        t_newPWDAgain=new JPasswordField(15);
        b_ok=new JButton("ȷ��");
        b_cancel=new JButton("ȡ��");
        Container c=this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(l_oldPWD);
        c.add(t_oldPWD);
        c.add(l_newPWD);
        c.add(t_newPWD);
        c.add(l_newPWDAgain);
        c.add(t_newPWDAgain);
        c.add(b_ok);
        c.add(b_cancel);

        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);

        this.setResizable(false);
        this.setSize(280,160);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if(b_cancel==e.getSource()){
            //��Ӵ���
            this.dispose();
        }else if(b_ok==e.getSource())		//�޸�����
        {
            //��Ӵ���
            try{
                Conn c1 = new Conn(); // �����������
                Connection connection=c1.getConnection(); // �����������ݿ�ķ���
                Statement statement=c1.con.createStatement();
                statement.execute("use salaryManager");
                String in_oldpwd=new String(t_oldPWD.getPassword());
                ResultSet resultSet=statement.executeQuery("select upw from loginInfo where uid="+"'"+username+"'");
                resultSet.next();
                if(in_oldpwd.equals(resultSet.getString("upw"))){
                    String in_newpwd=new String(t_newPWD.getPassword());
                    String in_newagain=new String(t_newPWDAgain.getPassword());
                    if(in_newpwd.equals(in_oldpwd)){
                        JOptionPane.showMessageDialog(
                                null,"�����벻�ܺ�ԭ����һ��","���������ʾ",JOptionPane.WARNING_MESSAGE);
                    }
                    else if(!in_newpwd.equals("")&&in_newpwd.equals(in_newagain)){
                        statement.executeUpdate("update loginInfo set upw="+in_newpwd+" where uid="+"'"+username+"'");
                        System.out.println("�޸ĳɹ�");
                        this.dispose();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(
                            null,"ԭ�����������","���������ʾ",JOptionPane.WARNING_MESSAGE);
                }
                resultSet.close();
                statement.close();
                connection.close();
            }
            catch (SQLException sqle){
                sqle.printStackTrace();
            }
        }
    }
}

//���ʱ༭����
class SalaryEditFrame extends JFrame implements ActionListener{
    private JLabel l_ps1,l_ps2,l_date,l_empID,l_empName,l_gongzi,l_jintie;
    private JTextField t_date,t_empID,t_empName,t_gongzi,t_jintie;
    private JButton b_update,b_delete,b_select,b_new,b_clear;
    private JPanel p1,p2,p3;
    private JScrollPane scrollpane;
    private DefaultTableModel dtm;
    private JTable table;

    public SalaryEditFrame()
    {
        super("���ʹ���");
        Container c=this.getContentPane();
        l_date = new JLabel("������Ϣ",JLabel.CENTER);
        l_empID = new JLabel("Ա����",JLabel.CENTER);
        l_empName = new JLabel("Ա������",JLabel.CENTER);
        l_gongzi = new JLabel("��������",JLabel.CENTER);
        l_jintie = new JLabel("����",JLabel.CENTER);
        b_new = new JButton("¼��");
        b_update = new JButton("�޸�");
        b_delete = new JButton("ɾ��");
        b_clear = new JButton("���");
        b_select = new JButton("��ѯ");
        t_date = new JTextField(8);
        t_empID = new JTextField(8);
        t_empName = new JTextField(8);
        t_gongzi = new JTextField(8);
        t_jintie = new JTextField(8);

        c.setLayout(new BorderLayout());
        p1=new JPanel();
        p1.setLayout(new GridLayout(5,2,10,10));
        p1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("�༭������Ϣ"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        p1.add(l_date);
        p1.add(t_date);
        p1.add(l_empID);
        p1.add(t_empID);
        p1.add(l_empName);
        p1.add(t_empName);
        p1.add(l_gongzi);
        p1.add(t_gongzi);
        p1.add(l_jintie);
        p1.add(t_jintie);

        c.add(p1, BorderLayout.WEST);

        p2=new JPanel();
        p2.setLayout(new GridLayout(5,1,10,10));
        p2.add(b_new);
        p2.add(b_update);
        p2.add(b_delete);
        p2.add(b_select);
        p2.add(b_clear);
        c.add(p2,BorderLayout.CENTER);


        p3 = new JPanel();
        p3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("��ʾ������Ϣ"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        String[] cloum = { "������Ϣ","Ա����", "����", "�»�������", "����","��н"};
        Object[][] row = new Object[50][6];
        dtm = new DefaultTableModel(row,cloum);
        table = new JTable(dtm);
        scrollpane = new JScrollPane(table);
        scrollpane.setViewportView(table);
        p3.add(scrollpane);
        c.add(p3,BorderLayout.EAST);

        b_new.addActionListener(this);
        b_update.addActionListener(this);
        b_delete.addActionListener(this);
        b_select.addActionListener(this);
        b_clear.addActionListener(this);

        //��Ӵ��룬Ϊtable���������¼�����addMouseListener

        this.setResizable(false);
        this.setSize(800,300);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);

        this.setResizable(true);// ���Ե��������С
        this.setVisible(true);

    }
    public void clear(){
        for(int i=0;i<MainFrame.count;i++){
            for(int j=0;j<6;j++){
                dtm.setValueAt("",i,j);
            }
        }
    }

    public boolean date_check(String d){
        int y=Integer.parseInt(d.substring(0,4));
        int m=Integer.parseInt(d.substring(4));
        if(y>=0&&m>=0&&m<=12){
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            Conn c1 = new Conn(); // �����������
            Connection connection=c1.getConnection(); // �����������ݿ�ķ���
            Statement statement=c1.con.createStatement();
            statement.execute("use salaryManager");
            if(b_select==e.getSource())			//��ѯ���еĹ�����Ϣ
            {
                //��Ӵ���
                clear();
                String in_empID=t_empID.getText();
                if(in_empID.equals("")){
                    ResultSet resultSet=statement.executeQuery("select * from salary");
                    System.out.println("��ȡ�ɹ�");
                    int i=0;
                    while(resultSet.next()){
                        dtm.setValueAt(resultSet.getString("paydate"),i,0);
                        dtm.setValueAt(resultSet.getString("sid"),i,1);
                        dtm.setValueAt(resultSet.getString("sname"),i,2);
                        double a=resultSet.getDouble("gongzi");
                        double b=resultSet.getDouble("jintie");
                        dtm.setValueAt(a,i,3);
                        dtm.setValueAt(b,i,4);
                        dtm.setValueAt(a+b,i,5);
                        i++;
                    }
                    resultSet.close();
                }
                else{
                    clear();
                    ResultSet resultSet=statement.executeQuery("select * from salary where sid="+in_empID);
                    System.out.println("��ȡ�ɹ�");
                    int i=0;
                    while(resultSet.next()){
                        dtm.setValueAt(resultSet.getString("paydate"),i,0);
                        dtm.setValueAt(resultSet.getString("sid"),i,1);
                        dtm.setValueAt(resultSet.getString("sname"),i,2);
                        double a=resultSet.getDouble("gongzi");
                        double b=resultSet.getDouble("jintie");
                        dtm.setValueAt(a,i,3);
                        dtm.setValueAt(b,i,4);
                        dtm.setValueAt(a+b,i,5);
                        i++;
                    }
                    resultSet.close();
                }
            }else if(b_update==e.getSource())		//�޸�ĳ�����ʼ�¼
            {
                //��Ӵ���
                clear();
                String in_empID=t_empID.getText();
                String in_jintie=t_jintie.getText();
                String in_gongzi=t_gongzi.getText();
                if(!in_jintie.equals("")&&in_gongzi.equals("")){
                    double dou_jintie=Double.parseDouble(in_jintie);
                    String in_date=t_date.getText();
                    if(dou_jintie>=0){
                        if(JOptionPane.showConfirmDialog(
                                null,"�Ƿ�ȷ���޸�","�޸�ǰȷ��",JOptionPane.OK_CANCEL_OPTION)
                                ==JOptionPane.YES_OPTION){
                            statement.executeUpdate("update salary set jintie="+dou_jintie+
                                    " where sid="+in_empID+" and paydate="+in_date);
                            System.out.println("�޸ĳɹ�");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(
                                null,"�������������븺��","���������ʾ",JOptionPane.WARNING_MESSAGE);
                    }
                    clear();
                    ResultSet resultSet=statement.executeQuery(
                            "select * from salary where sid="+in_empID+" and paydate ="+in_date);
                    int i=0;
                    resultSet.next();
                    dtm.setValueAt(resultSet.getString("paydate"),i,0);
                    dtm.setValueAt(resultSet.getString("sid"),i,1);
                    dtm.setValueAt(resultSet.getString("sname"),i,2);
                    double a=resultSet.getDouble("gongzi");
                    double b=resultSet.getDouble("jintie");
                    dtm.setValueAt(a,i,3);
                    dtm.setValueAt(b,i,4);
                    dtm.setValueAt(a+b,i,5);
                    resultSet.close();
                }
                else if(!in_gongzi.equals("")){
                    JOptionPane.showMessageDialog(
                            null,"ֻ�����޸Ľ���","���������ʾ",JOptionPane.WARNING_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(
                            null,"������Ҫ�޸ĵ�Ա���źͽ������","���������ʾ",JOptionPane.PLAIN_MESSAGE);
                }
            }else if(b_delete==e.getSource())		//ɾ��ĳ�����ʼ�¼
            {
                //��Ӵ���
                String in_empID=t_empID.getText();
                String in_date=t_date.getText();
                if(!in_empID.equals("")){
                    ResultSet resultSet=statement.executeQuery(
                            "select * from salary where sid="+in_empID);
                    int i=0;
                    while(resultSet.next()){
                        i++;
                    }
                    resultSet.close();
                    if(i==0){
                        System.out.println("û�д�ID");
                    }
                    else{
                        if(JOptionPane.showConfirmDialog(
                                null,"�Ƿ�ȷ��ɾ��","ɾ��ǰȷ��",JOptionPane.OK_CANCEL_OPTION)
                                ==JOptionPane.YES_OPTION){
                            statement.executeUpdate("delete from salary where sid="+in_empID+" and paydate="+in_date);
                            resultSet=statement.executeQuery("select * from salary");
                            System.out.println("��ȡ�ɹ�");
                            i=0;
                            while(resultSet.next()){
                                dtm.setValueAt(resultSet.getString("paydate"),i,0);
                                dtm.setValueAt(resultSet.getString("sid"),i,1);
                                dtm.setValueAt(resultSet.getString("sname"),i,2);
                                double a=resultSet.getDouble("gongzi");
                                double b=resultSet.getDouble("jintie");
                                dtm.setValueAt(a,i,3);
                                dtm.setValueAt(b,i,4);
                                dtm.setValueAt(a+b,i,5);
                                i++;
                            }
                            resultSet.close();
                            System.out.println("ɾ���ɹ�");
                        }
                    }
                }
            }else if(b_new==e.getSource())      		//����µĹ��ʼ�¼
            {
                //��Ӵ���
                String in_date=t_date.getText();
                String in_empID=t_empID.getText();
                String in_empName=t_empName.getText();
                String in_gongzi=t_gongzi.getText();
                String in_jintie=t_jintie.getText();
                double dou_gongzi=Double.parseDouble(in_gongzi);
                double dou_jintie=Double.parseDouble(in_jintie);
                if(!date_check(in_date)||dou_gongzi<0||dou_jintie<0){
                    JOptionPane.showMessageDialog(
                            null,"������ִ�������������","���������ʾ",JOptionPane.WARNING_MESSAGE);
                }
                else{
                    String value=in_date+","+in_empID+","+"'"+in_empName+"'"+","+in_gongzi+","+in_jintie;
                    if(!in_empID.equals("")){
                        statement.executeUpdate("insert into salary values("+value+")");
                        System.out.println("����ɹ�");
                    }
                    clear();
                    ResultSet resultSet=statement.executeQuery(
                            "select * from salary where sid="+in_empID+" and paydate ="+in_date);
                    int i=0;
                    resultSet.next();
                    dtm.setValueAt(resultSet.getString("paydate"),i,0);
                    dtm.setValueAt(resultSet.getString("sid"),i,1);
                    dtm.setValueAt(resultSet.getString("sname"),i,2);
                    double a=resultSet.getDouble("gongzi");
                    double b=resultSet.getDouble("jintie");
                    dtm.setValueAt(a,i,3);
                    dtm.setValueAt(b,i,4);
                    dtm.setValueAt(a+b,i,5);
                    resultSet.close();
                }
            }
            else if(b_clear==e.getSource())			//��������
            {
                //��Ӵ���
                t_date.setText("");
                t_empID.setText("");
                t_empName.setText("");
                t_gongzi.setText("");
                t_jintie.setText("");
            }
            statement.close();
            connection.close();
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }
}