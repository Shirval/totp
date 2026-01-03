package com.shirval;

import com.shirval.repository.CodeRepository;
import com.shirval.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CleanupService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CodeRepository codeRepository;

    @Transactional
    public void clean(int customerId) {
        codeRepository.removeByCustomerId(customerId);
        customerRepository.remove(customerId);
    }
}
