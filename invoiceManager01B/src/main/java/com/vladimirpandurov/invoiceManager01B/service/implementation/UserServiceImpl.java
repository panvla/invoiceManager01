package com.vladimirpandurov.invoiceManager01B.service.implementation;

import com.vladimirpandurov.invoiceManager01B.domain.Role;
import com.vladimirpandurov.invoiceManager01B.domain.User;
import com.vladimirpandurov.invoiceManager01B.dto.UserDTO;
import com.vladimirpandurov.invoiceManager01B.dtomapper.UserDTOMapper;
import com.vladimirpandurov.invoiceManager01B.form.UpdateForm;
import com.vladimirpandurov.invoiceManager01B.repository.RoleRepository;
import com.vladimirpandurov.invoiceManager01B.repository.UserRepository;
import com.vladimirpandurov.invoiceManager01B.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.vladimirpandurov.invoiceManager01B.dtomapper.UserDTOMapper.fromUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDTO createUser(User user) {
        return mapToUserDTO(this.userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        this.userRepository.sendVerificationCode(user);
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(this.userRepository.verifyCode(email, code));
    }

    @Override
    public UserDTO getUser(String email) {
        return mapToUserDTO(this.userRepository.getUserByEmail(email));
    }

    @Override
    public void resetPassword(String email) {
        this.userRepository.resetPassword(email);
    }

    @Override
    public UserDTO verifyPasswordKey(String key) {
        return mapToUserDTO(this.userRepository.verifyPasswordKey(key));
    }

    @Override
    public void renewPassword(String key, String password, String confirmPassword) {
        this.userRepository.renewPassword(key, password, confirmPassword);
    }

    @Override
    public UserDTO verifyAccountKey(String key) {
        return mapToUserDTO(this.userRepository.verifyAccountKey(key));
    }

    @Override
    public UserDTO updateUserDetails(UpdateForm user) {
        return mapToUserDTO(this.userRepository.updateUserDetails(user));
    }

    @Override
    public UserDTO getUserById(Long userId) {
      return  mapToUserDTO(this.userRepository.get(userId));
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword) {
        this.userRepository.updatePassword(id, currentPassword, newPassword, confirmNewPassword);
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {
        this.roleRepository.updateUserRole(userId, roleName);
    }

    @Override
    public void updateAccountSettings(Long id, Boolean enabled, Boolean notLocked) {
        this.userRepository.updateAccountSettings(id, enabled, notLocked);
    }

    @Override
    public UserDTO toggleMfa(String email) {
        return mapToUserDTO(this.userRepository.toggleMfa(email));
    }

    @Override
    public void updateImage(UserDTO user, MultipartFile image) {
        this.userRepository.updateImage(user, image);
    }


    private UserDTO mapToUserDTO(User user) {
        return fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
