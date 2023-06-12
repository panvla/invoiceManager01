package com.vladimirpandurov.invoiceManager01B.service.implementation;

import com.vladimirpandurov.invoiceManager01B.domain.Role;
import com.vladimirpandurov.invoiceManager01B.repository.RoleRepository;
import com.vladimirpandurov.invoiceManager01B.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByUserId(Long id) {
        return this.roleRepository.getRoleByUserId(id);
    }
}
