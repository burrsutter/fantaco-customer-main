package com.customer.service;

import com.customer.dto.CustomerRequest;
import com.customer.dto.CustomerResponse;
import com.customer.dto.CustomerUpdateRequest;
import com.customer.exception.CustomerNotFoundException;
import com.customer.exception.DuplicateCustomerIdException;
import com.customer.model.Customer;
import com.customer.repository.CustomerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        // Check for duplicate customer ID
        if (customerRepository.existsById(request.customerId())) {
            throw new DuplicateCustomerIdException("Customer with ID " + request.customerId() + " already exists");
        }

        Customer customer = new Customer();
        customer.setCustomerId(request.customerId());
        customer.setCompanyName(request.companyName());
        customer.setContactName(request.contactName());
        customer.setContactTitle(request.contactTitle());
        customer.setAddress(request.address());
        customer.setCity(request.city());
        customer.setRegion(request.region());
        customer.setPostalCode(request.postalCode());
        customer.setCountry(request.country());
        customer.setPhone(request.phone());
        customer.setFax(request.fax());
        customer.setContactEmail(request.contactEmail());

        try {
            Customer savedCustomer = customerRepository.save(customer);
            return toResponse(savedCustomer);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCustomerIdException("Customer with ID " + request.customerId() + " already exists");
        }
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found"));
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(String companyName, String contactName, String contactEmail, String phone) {
        List<Customer> customers = new ArrayList<>();

        if (companyName != null && !companyName.isBlank()) {
            customers.addAll(customerRepository.findByCompanyNameContainingIgnoreCase(companyName));
        } else if (contactName != null && !contactName.isBlank()) {
            customers.addAll(customerRepository.findByContactNameContainingIgnoreCase(contactName));
        } else if (contactEmail != null && !contactEmail.isBlank()) {
            customers.addAll(customerRepository.findByContactEmailContainingIgnoreCase(contactEmail));
        } else if (phone != null && !phone.isBlank()) {
            customers.addAll(customerRepository.findByPhoneContaining(phone));
        } else {
            customers.addAll(customerRepository.findAll());
        }

        return customers.stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse updateCustomer(String customerId, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found"));

        customer.setCompanyName(request.companyName());
        customer.setContactName(request.contactName());
        customer.setContactTitle(request.contactTitle());
        customer.setAddress(request.address());
        customer.setCity(request.city());
        customer.setRegion(request.region());
        customer.setPostalCode(request.postalCode());
        customer.setCountry(request.country());
        customer.setPhone(request.phone());
        customer.setFax(request.fax());
        customer.setContactEmail(request.contactEmail());

        Customer updatedCustomer = customerRepository.save(customer);
        return toResponse(updatedCustomer);
    }

    public void deleteCustomer(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerRepository.deleteById(customerId);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getCompanyName(),
                customer.getContactName(),
                customer.getContactTitle(),
                customer.getAddress(),
                customer.getCity(),
                customer.getRegion(),
                customer.getPostalCode(),
                customer.getCountry(),
                customer.getPhone(),
                customer.getFax(),
                customer.getContactEmail(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
