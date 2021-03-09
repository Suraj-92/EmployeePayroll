package javapractice;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static javapractice.EmployeePayrollService.IOService.DB_IO;
import static javapractice.EmployeePayrollService.IOService.FILE_IO;
import static javapractice.EmployeePayrollService.IOService.REST_IO;

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
        employeePayrollService.updateEmployeeSalary("Terisa", 3000000.00, DB_IO);
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
    @Test
    public void givenPayrollDataWhenAverageSalaryRetrieveByGenderShouldReturnProperValue() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeeData(DB_IO);
        Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(DB_IO);
        Assertions.assertTrue(averageSalaryByGender.get("M").equals(2000000.00) &&
                                        averageSalaryByGender.get("F").equals(3000000.00) );
    }

    @Test
    public void givenNewEmployeeWhenAddedShouldSyncWithDB() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeeData(DB_IO);
        employeePayrollService.addEmployeeToPayroll("Mark", 5000000.00, LocalDate.now(),"M");
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Mark");
        Assertions.assertTrue(result);
    }

    @Test
    public void given6EmployeeWhenAddedToDBShouldMatchEmployeeEntries() throws SQLException {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(0,"Jeff Bezos","M",100000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Bill Gates","M",200000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Mark Zuckerberg","M",300000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Sunder","M",600000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Mukesh","M",100000.0,LocalDate.now()),
                new EmployeePayrollData(0,"Anil","M",200000.0,LocalDate.now()),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeeData(DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration Without Thread " + Duration.between(start, end));
       // Assertions.assertEquals(7, employeePayrollService.countEntries(DB_IO));
        Instant threadStart = Instant.now();
        employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmps));
        Instant threadEnd = Instant.now();
        System.out.println("Duration Without Thread " + Duration.between(threadStart, threadEnd));
        employeePayrollService.printData(DB_IO);
        Assertions.assertEquals(16, employeePayrollService.countEntries(DB_IO));
    }

    @Before
    public void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }
    public EmployeePayrollData[] getEmployeeList(){
        Response response = RestAssured.get("/employee_payroll");
        System.out.println("Employee Payroll Entries in JSONServer:\n"+response.asString());
        EmployeePayrollData[] arrayOfEmps = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
        return arrayOfEmps;
    }

    private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
        String empJson = new Gson().toJson(employeePayrollData);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type","application/json");
        request.body(empJson);
        return  request.post("/employee_payroll");
    }

    @Test
    public void givenEmployeeDataInJsonServerWhenRetrievedShouldMatchTheCount(){
        EmployeePayrollData[] arrayOfEmps = getEmployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        long entries = employeePayrollService.countEntries(REST_IO);
        Assertions.assertEquals(2,entries);
    }
    @Test
    public void givenNewEmployee_whenAddedShouldMatch201ResponseAndCount(){
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] arrayOfEmps = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        EmployeePayrollData employeePayrollData = null;
        employeePayrollData = new EmployeePayrollData(0,"Mark HusenBerg","M",300000.0, LocalDate.now());
        Response response = addEmployeeToJsonServer(employeePayrollData);
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(201,statusCode);

        employeePayrollData = new Gson().fromJson(response.asString(),EmployeePayrollData.class);
        employeePayrollService.addEmployeeToPayroll(employeePayrollData, REST_IO);
        long entries = employeePayrollService.countEntries(REST_IO);
        Assertions.assertEquals(3, entries);
    }
@Test
public void givenListOfNewEmployeeWhenAddedShouldMatch201ResponseAndCount(){
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] arrayOfEmps = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));

        EmployeePayrollData[] arrayOfEmpPayrolls = {
                new EmployeePayrollData(0, "Sunder", "M",600000.0, LocalDate.now()),
                new EmployeePayrollData(0,"Mukesh", "M",100000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Anil", "M", 200000.0, LocalDate.now())
        };
        for (EmployeePayrollData employeePayrollData : arrayOfEmpPayrolls){
            Response response = addEmployeeToJsonServer(employeePayrollData);
            int statusCode = response.getStatusCode();
            Assertions.assertEquals(201, statusCode);

            employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
            employeePayrollService.addEmployeeToPayroll(employeePayrollData, REST_IO);
        }
        long entries = employeePayrollService.countEntries(REST_IO);
        Assertions.assertEquals(6,entries);
}
    @Test
    public void givenNewSalaryForEmployeeWhenUpdatedShouldMatch200Response(){
        EmployeePayrollService employeePayrollService;
        EmployeePayrollData[] arrayOfEmps = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));

        employeePayrollService.updateEmployeeSalary("Anil",3000000.0, REST_IO);
        EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayrollData("Anil");

        String empJson = new Gson().toJson(employeePayrollData);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(empJson);
        Response response = request.put("/employee_payroll/"+employeePayrollData.id);
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(200, statusCode);
    }

    @Test
    public void givenEmployeeToDeleteWhenDeletedShouldMatch200ResponseAndCount(){
        EmployeePayrollService employeePayrollService ;
        EmployeePayrollData[] arrayOfEmps = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));

        EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayrollData("Anil");
        RequestSpecification request = RestAssured.given();
        request.header("Content_Type", "application/json");
        Response response = request.delete("/employee_payroll/"+employeePayrollData.id);
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(200,statusCode);

        employeePayrollService.deleteEmployeePayroll(employeePayrollData.name,REST_IO);
        long entries = employeePayrollService.countEntries(REST_IO);
        Assertions.assertEquals(5, entries);
    }
}
