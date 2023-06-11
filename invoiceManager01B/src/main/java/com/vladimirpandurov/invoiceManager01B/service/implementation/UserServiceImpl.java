package com.vladimirpandurov.invoiceManager01B.service.implementation;

import com.vladimirpandurov.invoiceManager01B.domain.User;
import com.vladimirpandurov.invoiceManager01B.dto.UserDTO;
import com.vladimirpandurov.invoiceManager01B.dtomapper.UserDTOMapper;
import com.vladimirpandurov.invoiceManager01B.repository.UserRepository;
import com.vladimirpandurov.invoiceManager01B.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(this.userRepository.create(user));
    }
}
