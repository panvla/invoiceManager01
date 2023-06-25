package com.vladimirpandurov.invoiceManager01B.report;

import com.vladimirpandurov.invoiceManager01B.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

@Slf4j
public class CustomerReport {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Customer> customers;

    public CustomerReport(List<Customer> customers) {
        this.customers = customers;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Customers");
    }
}
