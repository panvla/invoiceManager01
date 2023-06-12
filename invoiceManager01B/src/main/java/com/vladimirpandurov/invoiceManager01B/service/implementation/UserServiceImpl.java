package com.vladimirpandurov.invoiceManager01B.service.implementation;

import com.vladimirpandurov.invoiceManager01B.domain.User;
import com.vladimirpandurov.invoiceManager01B.dto.UserDTO;
import com.vladimirpandurov.invoiceManager01B.dtomapper.UserDTOMapper;
import com.vladimirpandurov.invoiceManager01B.repository.UserRepository;
import com.vladimirpandurov.invoiceManager01B.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(this.userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return UserDTOMapper.fromUser(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        this.userRepository.sendVerificationCode(user);
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return UserDTOMapper.fromUser(this.userRepository.verifyCode(email, code));
    }

    @Override
    public User getUser(String email) {
        return this.userRepository.getUserByEmail(email);
    }


}
