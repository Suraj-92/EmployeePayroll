package javapractice;
import java.util.*;

public class EmployeePayrollService {

    public enum IOService {CONSOLE_IO, FILE_IO, DB_IO,REST_IO}
    private List<EmployeePayrollData> employeePayrollList;
    public EmployeePayrollService() {}
    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
        this.employeePayrollList = employeePayrollList;
    }
    public static void main(String[] args)
    {
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader = new Scanner(System.in);
        employeePayrollService.readEmployeePayrollData(consoleInputReader);
        employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
    }

    private void readEmployeePayrollData(Scanner consoleInputReader) {
        System.out.println("Enter Employee ID: ");
        int id = consoleInputReader.nextInt();
        System.out.println("Enter Employee Name :");
        String name = consoleInputReader.next();
        System.out.println("Enter Employee Salary: ");
        double salary = consoleInputReader.nextDouble();
        employeePayrollList.add(new EmployeePayrollData(id, name, salary));
    }

    public void writeEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
        System.out.println("\n Writing Employee Payroll Roaster to Console\n " +employeePayrollList);
        else if (ioService.equals(IOService.FILE_IO))
            new employeePayrollFileIOService().writeData(employeePayrollList);
    }
    public long readEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            this.employeePayrollList = new employeePayrollFileIOService().readData();
        return employeePayrollList.size();
    }

    public void printData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            new employeePayrollFileIOService().printData();
    }
    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return new employeePayrollFileIOService().countEntries();
        return 0;
    }
}
