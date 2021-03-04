    package javapractice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
    private  static  EmployeePayrollDBService employeePayrollDBService;
    private PreparedStatement employeePayrollDataStatement;
    private  EmployeePayrollDBService() {}
    public static EmployeePayrollDBService getInstance(){
        if (employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }
    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll?useSSL=false";
        String userName = "root";
        String password = "root";
        Connection con;
        System.out.println("Connecting to database" +jdbcURL);
        con = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection is successful" +con);
        return con;
    }

    public List<EmployeePayrollData> readData() {
        String sql = "select *from employee_payroll";
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try (Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
//            while (resultSet.next())
//            {
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("name");
//                double salary = resultSet.getDouble("salary");
//                LocalDate startDate = resultSet.getDate("start").toLocalDate();
//                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }
    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if (this.employeePayrollDataStatement == null)
            this.preparedStatementForEmployeeData();
        try{
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try{
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private void preparedStatementForEmployeeData() {
        try{
            Connection connection = this.getConnection();
            String sql = "select * from employee_payroll where name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDataUsingPreparedStatement(name, salary);
    }

    private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
        String sql = String.format("update employee_payroll set salary = %.2f where name = '%s';", salary, name);
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
