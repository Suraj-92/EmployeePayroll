    package javapractice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return this.getEmployeePayrollDataUsingDB(sql);
//        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
//        try (Connection connection = this.getConnection()){
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(sql);
//            employeePayrollList = this.getEmployeePayrollData(resultSet);
////            while (resultSet.next())
////            {
////                int id = resultSet.getInt("id");
////                String name = resultSet.getString("name");
////                double salary = resultSet.getDouble("salary");
////                LocalDate startDate = resultSet.getDate("start").toLocalDate();
////                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
////            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return employeePayrollList;
    }
        public Map<String, Double> getAverageSalaryByGender() {
        String sql = " select gender , avg(salary)  as avg_salary from employee_payroll group by gender;";
        Map<String ,Double> genderToAverageSalaryMap = new HashMap<>();
        try (Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next())
            {
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("avg_salary");
                genderToAverageSalaryMap.put(gender, salary);
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return genderToAverageSalaryMap;
        }

    public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("select *from employee_payroll where START BETWEEN '%s' AND '%s';", Date.valueOf(startDate), Date.valueOf(endDate));
//        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
//        try (Connection connection = this.getConnection()){
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(sql);
//            employeePayrollList = this.getEmployeePayrollData(resultSet);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return employeePayrollList;
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try (Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
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

        public EmployeePayrollData addEmployeeToPayrollUC7(String name, double salary, LocalDate startDate, String gender) {
            int employeeId = -1;
            EmployeePayrollData employeePayrollData = null;
            String sql = String.format("insert into employee_payroll (name, gender, salary, start) values ('%s', '%s', %s, '%s' )", name, gender, salary, Date.valueOf(startDate));
            try (Connection connection = this.getConnection()){
                Statement statement = connection.createStatement();
                int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
                if (rowAffected == 1) {
                    ResultSet resultSet = statement.getGeneratedKeys();
                    if (resultSet.next()) employeeId = resultSet.getInt(1);
                }
                employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
            }catch (SQLException e)
            {
                e.printStackTrace();
            }
            return employeePayrollData;
        }

        public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
            int employeeId = -1;
            Connection connection = null;
            EmployeePayrollData employeePayrollData = null;
            try{
                connection = this.getConnection();
                connection.setAutoCommit(false);
            }catch (SQLException e)
            {
                e.printStackTrace();
            }
            try (Statement statement = connection.createStatement()){
                String sql = String.format("insert into employee_payroll(name, gender, salary, start) values ('%s', '%s', %s, '%s' )", name, gender, salary, Date.valueOf(startDate));
                int rowAffected  = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
                if (rowAffected == 1)
                {
                    ResultSet resultSet = statement.getGeneratedKeys();
                    if (resultSet.next()) employeeId = resultSet.getInt(1);
                }
            }catch (SQLException e)
            {
                e.printStackTrace();
                try {
                    connection.rollback();
                    return employeePayrollData;
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            try (Statement statement = connection.createStatement()){
                double deductions  = salary * 0.2;
                double taxablePay = salary - deductions;
                double tax = taxablePay * 0.1;
                double netPay = salary - tax;
                String sql = String.format("insert into payroll_details (employee_id, basi_pay, " +
                        " deductions, taxable_pay, tax, net_pay) values (%s, %s, %s, %s, %s, %S)",employeeId, salary, deductions, taxablePay, tax, netPay);
                int rowAffected  = statement.executeUpdate(sql);
                if (rowAffected == 1)
                {
                    employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
                }
            }catch (SQLException e)
            {
                e.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return employeePayrollData;
        }
    }
