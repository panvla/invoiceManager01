package com.vladimirpandurov.invoiceManager01B.repository;

import com.vladimirpandurov.invoiceManager01B.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, ListCrudRepository<Customer, Long> {

    Page<Customer> findByNameContaining(String name, Pageable pageable);
}
