package com.vladimirpandurov.invoiceManager01B.resource;

import com.vladimirpandurov.invoiceManager01B.domain.Customer;
import com.vladimirpandurov.invoiceManager01B.domain.HttpResponse;
import com.vladimirpandurov.invoiceManager01B.domain.Invoice;
import com.vladimirpandurov.invoiceManager01B.dto.UserDTO;
import com.vladimirpandurov.invoiceManager01B.service.CustomerService;
import com.vladimirpandurov.invoiceManager01B.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path="/customer")
@RequiredArgsConstructor
public class CustomerResource {

    private final CustomerService customerService;
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<HttpResponse> getCustomers(@AuthenticationPrincipal UserDTO user , @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "customers", customerService.getCustomers(page.orElse(0), size.orElse(10)),
                        "stats", customerService.getStats()
                ))
                .message("Customers retrieved")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    @PostMapping("/create")
    public ResponseEntity<HttpResponse> createCustomer(@AuthenticationPrincipal UserDTO user, @RequestBody Customer customer) {
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "customer", customerService.createCustomer(customer)))
                .message("Customer created")
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .build()
        );
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getCustomer(@AuthenticationPrincipal UserDTO user, @PathVariable("id") Long id) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "customer", customerService.getCustomer(id)))
                .message("Customer retrieved")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchCustomer(@AuthenticationPrincipal UserDTO user,@RequestParam Optional<String> name,@RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size ) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "customers", customerService.searchCustomers(name.orElse(""), page.orElse(0), size.orElse(10))))
                .message("Customers retrieved")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    @PutMapping("/update")
    public ResponseEntity<HttpResponse> updateCustomer(@AuthenticationPrincipal UserDTO user, @RequestBody Customer customer) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "customer", customerService.updateCustomer(customer)))
                .message("Customers updated")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build()
        );
    }
    @PostMapping("/invoice/create")
    public ResponseEntity<HttpResponse> createInvoice(@AuthenticationPrincipal UserDTO user, @RequestBody Invoice invoice) {
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "invoice", customerService.createInvoice(invoice)))
                .message("Invoice created")
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .build()
        );
    }
    @PostMapping("/invoice/new")
    public ResponseEntity<HttpResponse> newInvoice(@AuthenticationPrincipal UserDTO user) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "customers", customerService.getCustomers()))
                .message("Customers retrieved")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }



    @GetMapping("invoice/list")
    public ResponseEntity<HttpResponse> getInvoices(@AuthenticationPrincipal UserDTO user, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "invoices", customerService.getInvoices(page.orElse(0), size.orElse(10))))
                .message("Invoices retrieved")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    @GetMapping("/invoice/get/{id}")
    public ResponseEntity<HttpResponse> getInvoice(@AuthenticationPrincipal UserDTO user, @PathVariable("id") Long id) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "invoice", customerService.getInvoice(id)))
                .message("Invoice retrieved")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    @PostMapping("/invoice/addtocustomer/{id}")
    public ResponseEntity<HttpResponse> addInvoiceToCustomer(@AuthenticationPrincipal UserDTO user, @PathVariable("id") Long id, @RequestBody Invoice invoice) {
        customerService.addInvoiceToCustomer(id, invoice);
        return ResponseEntity.ok(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                        "customers", customerService.getCustomers()))
                .message("Customers retrieved")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }

}
