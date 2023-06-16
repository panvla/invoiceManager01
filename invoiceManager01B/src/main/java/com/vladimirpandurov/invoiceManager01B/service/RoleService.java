package com.vladimirpandurov.invoiceManager01B.service;

import com.vladimirpandurov.invoiceManager01B.domain.Role;

import java.util.Collection;

public interface RoleService {
    Role getRoleByUserId(Long id);
    Collection<Role> getRoles();
}
