package SalaryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class Conn { // 创建类Conn
    public static Connection con; // 声明Connection对象
    public static String user;
    public static  String password;
    public Connection getConnection() { // 建立返回值为Connection的方法
        try { // 加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //System.out.println("数据库驱动加载成功");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        user = "root";//数据库登录名
        password = "hehey666";//密码
        try { // 通过访问数据库的URL获取数据库连接对象
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root", user, password);
            //System.out.println("数据库连接成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con; // 按方法要求返回一个Connection对象
    }
}
public class salaryManager {
    public static void main(String[] args) {
        LoginFrame lf=new LoginFrame();
        lf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

//登录界面
class LoginFrame extends JFrame implements ActionListener{
    private JLabel l_user,l_pwd;  //用户名标签，密码标签
    private JTextField t_user; //用户名文本框
    private JPasswordField t_pwd; //密码文本框
    private JButton b_ok,b_cancel; //登录按钮，退出按钮

    public LoginFrame(){
        super("欢迎使用工资管理系统!");
        l_user=new JLabel("用户名：",JLabel.RIGHT);
        l_pwd=new JLabel(" 密码：",JLabel.RIGHT);
        t_user=new JTextField(16);
        t_pwd=new JPasswordField(17);
        b_ok=new JButton("登录");
        b_cancel=new JButton("退出");
        //布局方式FlowLayout，一行排满排下一行
        Container c=this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(l_user);
        c.add(t_user);
        c.add(l_pwd);
        c.add(t_pwd);
        c.add(b_ok);
        c.add(b_cancel);
        //为按钮添加监听事件
        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);
        //界面大小不可调整
        this.setResizable(false);
        this.setSize(250,150);
        //界面显示居中
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(b_cancel==e.getSource()){
            //添加退出代码
            System.exit(0);
        }else if(b_ok==e.getSource()){
            boolean find=false;
            try{
                Conn c = new Conn(); // 创建conn类对象
                Connection connection=c.getConnection(); // 调用连接数据库的方法
                Statement statement=c.con.createStatement();
                statement.execute("use salaryManager");
                ResultSet resultSet=statement.executeQuery("select * from loginInfo");
                //System.out.println("读取成功");
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
            //添加代码，验证身份成功后显示主界面
            if(find){
                new MainFrame(t_user.getText().trim());
            }else{
                JOptionPane.showMessageDialog(
                        null,"用户名不存在或密码错误","输入错误提示",JOptionPane.WARNING_MESSAGE);
                System.out.println("用户名不存在或密码错误");
            }
        }
    }
}

//主界面
class MainFrame extends JFrame implements ActionListener{
    private JMenuBar mb=new JMenuBar();
    private JMenu m_system=new JMenu("系统管理");
    private JMenu m_gongzi=new JMenu("工资管理");
    private JMenuItem mI[]={new JMenuItem("密码重置"),new JMenuItem("退出系统")};
    private JMenuItem m_FMEdit=new JMenuItem("工资编辑");
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
        super(username+"欢迎使用企业员工工资管理系统!");
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

        l_ps = new JLabel("注意：时间格式均为(年月)，例如：201502");
        l_empid = new JLabel("员工工号");
        t_empid = new JTextField(8);
        l_fromdate=new JLabel("起始时间");
        l_todate=new JLabel("终止时间");
        t_fromdate=new JTextField(8);
        t_todate=new JTextField(8);
        b_byempID=new JButton("查询");
        b_all=new JButton("查询所有");
        b_bydate=new JButton("查询");

        p_condition=new JPanel();
        p_condition.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("输入查询条件"),
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

        //添加3个按钮的监听事件
        b_byempID.addActionListener(this);
        b_all.addActionListener(this);
        b_bydate.addActionListener(this);

        p_detail=new JPanel();
        p_detail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("工资明细信息"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        String[] cloum = { "年月信息","员工号", "姓名", "月基本工资", "津贴","月薪"};
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

        //添加代码，将所有员工工资信息显示在界面表格中
        try{
            Conn c1 = new Conn(); // 创建本类对象
            Connection connection=c1.getConnection(); // 调用连接数据库的方法
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
            //添加退出系统的代码
            this.dispose();
        }else if(temp==m_FMEdit){
            SalaryEditFrame bef=new SalaryEditFrame();
        }else if(temp==b_byempID)  //根据员工工号查询
        {
            //添加代码
            this.clear();
            try{
                Conn c1 = new Conn(); // 创建本类对象
                Connection connection=c1.getConnection(); // 调用连接数据库的方法
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
        else if(temp==b_all)  //查询所有员工工资
        {
            //添加代码
            try{
                Conn c1 = new Conn(); // 创建本类对象
                Connection connection=c1.getConnection(); // 调用连接数据库的方法
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
        else if(temp==b_bydate)     //根据起始时间来查询
        {
            //添加代码
            try{
                Conn c1 = new Conn(); // 创建本类对象
                Connection connection=c1.getConnection(); // 调用连接数据库的方法
                Statement statement=c1.con.createStatement();
                statement.execute("use salaryManager");
                String in_fromdate=t_fromdate.getText();
                String in_todate=t_todate.getText();
                int int_fromdate=Integer.parseInt(in_fromdate);
                int int_todate=Integer.parseInt(in_todate);
                if(!date_check(in_fromdate,in_todate)){
                    JOptionPane.showMessageDialog(
                            null,"月份输入错误，请重新输入","输入错误提示",JOptionPane.WARNING_MESSAGE);
                    System.out.println("日期格式错误");
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

//修改密码界面
class ModifyPwdFrame extends JFrame implements ActionListener{
    private JLabel l_oldPWD,l_newPWD,l_newPWDAgain;
    private JPasswordField t_oldPWD,t_newPWD,t_newPWDAgain;
    private JButton b_ok,b_cancel;
    public String username;

    public ModifyPwdFrame(String username)
    {
        super("修改密码");
        this.username=username;
        l_oldPWD=new JLabel("  旧密码:       ");
        l_newPWD=new JLabel("  新密码：      ");
        l_newPWDAgain=new JLabel("确认新密码：");
        t_oldPWD=new JPasswordField(15);
        t_newPWD=new JPasswordField(15);
        t_newPWDAgain=new JPasswordField(15);
        b_ok=new JButton("确定");
        b_cancel=new JButton("取消");
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
            //添加代码
            this.dispose();
        }else if(b_ok==e.getSource())		//修改密码
        {
            //添加代码
            try{
                Conn c1 = new Conn(); // 创建本类对象
                Connection connection=c1.getConnection(); // 调用连接数据库的方法
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
                                null,"新密码不能和原密码一致","输入错误提示",JOptionPane.WARNING_MESSAGE);
                    }
                    else if(!in_newpwd.equals("")&&in_newpwd.equals(in_newagain)){
                        statement.executeUpdate("update loginInfo set upw="+in_newpwd+" where uid="+"'"+username+"'");
                        System.out.println("修改成功");
                        this.dispose();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(
                            null,"原密码输入错误","输入错误提示",JOptionPane.WARNING_MESSAGE);
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

//工资编辑界面
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
        super("工资管理");
        Container c=this.getContentPane();
        l_date = new JLabel("年月信息",JLabel.CENTER);
        l_empID = new JLabel("员工号",JLabel.CENTER);
        l_empName = new JLabel("员工姓名",JLabel.CENTER);
        l_gongzi = new JLabel("基本工资",JLabel.CENTER);
        l_jintie = new JLabel("津贴",JLabel.CENTER);
        b_new = new JButton("录入");
        b_update = new JButton("修改");
        b_delete = new JButton("删除");
        b_clear = new JButton("清空");
        b_select = new JButton("查询");
        t_date = new JTextField(8);
        t_empID = new JTextField(8);
        t_empName = new JTextField(8);
        t_gongzi = new JTextField(8);
        t_jintie = new JTextField(8);

        c.setLayout(new BorderLayout());
        p1=new JPanel();
        p1.setLayout(new GridLayout(5,2,10,10));
        p1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("编辑工资信息"),
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
                BorderFactory.createTitledBorder("显示工资信息"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        String[] cloum = { "年月信息","员工号", "姓名", "月基本工资", "津贴","月薪"};
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

        //添加代码，为table添加鼠标点击事件监听addMouseListener

        this.setResizable(false);
        this.setSize(800,300);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);

        this.setResizable(true);// 可以调整界面大小
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
            Conn c1 = new Conn(); // 创建本类对象
            Connection connection=c1.getConnection(); // 调用连接数据库的方法
            Statement statement=c1.con.createStatement();
            statement.execute("use salaryManager");
            if(b_select==e.getSource())			//查询所有的工资信息
            {
                //添加代码
                clear();
                String in_empID=t_empID.getText();
                if(in_empID.equals("")){
                    ResultSet resultSet=statement.executeQuery("select * from salary");
                    System.out.println("读取成功");
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
                    System.out.println("读取成功");
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
            }else if(b_update==e.getSource())		//修改某条工资记录
            {
                //添加代码
                clear();
                String in_empID=t_empID.getText();
                String in_jintie=t_jintie.getText();
                String in_gongzi=t_gongzi.getText();
                if(!in_jintie.equals("")&&in_gongzi.equals("")){
                    double dou_jintie=Double.parseDouble(in_jintie);
                    String in_date=t_date.getText();
                    if(dou_jintie>=0){
                        if(JOptionPane.showConfirmDialog(
                                null,"是否确认修改","修改前确认",JOptionPane.OK_CANCEL_OPTION)
                                ==JOptionPane.YES_OPTION){
                            statement.executeUpdate("update salary set jintie="+dou_jintie+
                                    " where sid="+in_empID+" and paydate="+in_date);
                            System.out.println("修改成功");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(
                                null,"津贴不允许输入负数","输入错误提示",JOptionPane.WARNING_MESSAGE);
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
                            null,"只允许修改津贴","输入错误提示",JOptionPane.WARNING_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(
                            null,"请输入要修改的员工号和津贴金额","输入错误提示",JOptionPane.PLAIN_MESSAGE);
                }
            }else if(b_delete==e.getSource())		//删除某条工资记录
            {
                //添加代码
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
                        System.out.println("没有此ID");
                    }
                    else{
                        if(JOptionPane.showConfirmDialog(
                                null,"是否确认删除","删除前确认",JOptionPane.OK_CANCEL_OPTION)
                                ==JOptionPane.YES_OPTION){
                            statement.executeUpdate("delete from salary where sid="+in_empID+" and paydate="+in_date);
                            resultSet=statement.executeQuery("select * from salary");
                            System.out.println("读取成功");
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
                            System.out.println("删除成功");
                        }
                    }
                }
            }else if(b_new==e.getSource())      		//添加新的工资记录
            {
                //添加代码
                String in_date=t_date.getText();
                String in_empID=t_empID.getText();
                String in_empName=t_empName.getText();
                String in_gongzi=t_gongzi.getText();
                String in_jintie=t_jintie.getText();
                double dou_gongzi=Double.parseDouble(in_gongzi);
                double dou_jintie=Double.parseDouble(in_jintie);
                if(!date_check(in_date)||dou_gongzi<0||dou_jintie<0){
                    JOptionPane.showMessageDialog(
                            null,"输入出现错误，请重新输入","输入错误提示",JOptionPane.WARNING_MESSAGE);
                }
                else{
                    String value=in_date+","+in_empID+","+"'"+in_empName+"'"+","+in_gongzi+","+in_jintie;
                    if(!in_empID.equals("")){
                        statement.executeUpdate("insert into salary values("+value+")");
                        System.out.println("插入成功");
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
            else if(b_clear==e.getSource())			//清空输入框
            {
                //添加代码
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