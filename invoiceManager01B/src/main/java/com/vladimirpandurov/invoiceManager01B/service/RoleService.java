package com.vladimirpandurov.invoiceManager01B.service;

import com.vladimirpandurov.invoiceManager01B.domain.Role;

public interface RoleService {
    Role getRoleByUserId(Long id);
}
