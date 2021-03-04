package javapractice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static javapractice.EmployeePayrollService.IOService.DB_IO;
import static javapractice.EmployeePayrollService.IOService.FILE_IO;

public class EmployeePayrollServiceTest {
    @Test
    public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries(){
        EmployeePayrollData[] arrayOfEmps = {
          new EmployeePayrollData(1,"Jeff Bezos", 10000.00),
                new EmployeePayrollData(2,"Bill Gates", 25000.00),
                new EmployeePayrollData(3,"Mark ZukerBerg", 50000.00),
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(FILE_IO);
        employeePayrollService.printData(FILE_IO);
        long entries = employeePayrollService.countEntries(FILE_IO);
        Assertions.assertEquals(3, entries);
    }

    @Test
    public void givenFileOnReadingFromFileShouldMatchEmployeeCount(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        long entries = employeePayrollService.readEmployeePayrollData(FILE_IO);
        Assertions.assertEquals(3, entries);
    }

    @Test
    public void givenEmployeePayrollInDBWhenRetrievedShouldMatchEmployeeCount() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeeData(DB_IO);
        Assertions.assertEquals(3, employeePayrollData.size());
    }

    @Test
    public void givenNewSalaryForEmployeeWhenUpdatedShouldSyncWithDB() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeeData(DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa", 3000000.00);
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Terisa");
        Assertions.assertTrue(result);
    }

    @Test
    public void givenDateRangeWhenRetrievedShouldMatchEmployeeCount() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeeData(DB_IO);
        LocalDate startDate = LocalDate.of(2018, 01, 01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollForDateRange(DB_IO, startDate, endDate);
        Assertions.assertEquals(3, employeePayrollData.size());
    }
}
