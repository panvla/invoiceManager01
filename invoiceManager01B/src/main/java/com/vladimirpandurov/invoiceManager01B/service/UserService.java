package com.vladimirpandurov.invoiceManager01B.service;

import com.vladimirpandurov.invoiceManager01B.domain.User;
import com.vladimirpandurov.invoiceManager01B.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);
}
