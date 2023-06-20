package com.vladimirpandurov.invoiceManager01B.repository;

import com.vladimirpandurov.invoiceManager01B.domain.Invoice;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long>, ListCrudRepository<Invoice, Long> {
}
