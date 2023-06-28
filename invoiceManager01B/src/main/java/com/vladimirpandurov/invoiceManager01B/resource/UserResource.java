package com.vladimirpandurov.invoiceManager01B.resource;

import com.vladimirpandurov.invoiceManager01B.domain.HttpResponse;
import com.vladimirpandurov.invoiceManager01B.domain.User;
import com.vladimirpandurov.invoiceManager01B.domain.UserPrincipal;
import com.vladimirpandurov.invoiceManager01B.dto.UserDTO;
import com.vladimirpandurov.invoiceManager01B.enumeration.EventType;
import com.vladimirpandurov.invoiceManager01B.event.NewUserEvent;
import com.vladimirpandurov.invoiceManager01B.exception.ApiException;
import com.vladimirpandurov.invoiceManager01B.form.*;
import com.vladimirpandurov.invoiceManager01B.provider.TokenProvider;
import com.vladimirpandurov.invoiceManager01B.service.EventService;
import com.vladimirpandurov.invoiceManager01B.service.RoleService;
import com.vladimirpandurov.invoiceManager01B.service.UserService;
import com.vladimirpandurov.invoiceManager01B.utils.ExceptionUtils;
import com.vladimirpandurov.invoiceManager01B.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.vladimirpandurov.invoiceManager01B.dtomapper.UserDTOMapper.toUser;
import static com.vladimirpandurov.invoiceManager01B.enumeration.EventType.*;
import static com.vladimirpandurov.invoiceManager01B.utils.UserUtils.getAuthenticatedUser;
import static com.vladimirpandurov.invoiceManager01B.utils.UserUtils.getLoggedInUser;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;


@RestController
@RequestMapping(path="/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserResource {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final UserService userService;
    private final RoleService roleService;
    private final EventService eventService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
        UserDTO user =  authenticate(loginForm.getEmail(), loginForm.getPassword());
        return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);

    }


    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
        UserDTO userDTO = this.userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", userDTO))
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message(String.format("User created for user %s", user.getFirstName()))
                .build()
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication){
        UserDTO user = this.userService.getUserByEmail(getAuthenticatedUser(authentication).getEmail());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user, "events" , this.eventService.getEventsByUserId(user.getId()) , "roles", this.roleService.getRoles()))
                        .message("Profile Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }


    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code){
        UserDTO user = this.userService.verifyCode(email, code);
        publisher.publishEvent(new NewUserEvent(LOGIN_ATTEMPT_SUCCESS, user.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", user,
                        "access_token",tokenProvider.createAccessToken(getUserPrincipal(user)),
                        "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                .message("Login Success")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }

    @GetMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request){
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason("There is no maping for a " + request.getMethod() + " request for this path on the server")
                        .status(HttpStatus.NOT_FOUND)
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .build()
        );
    }
    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> ressetPassword(@PathVariable("email") String email) {
        this.userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("Email sent. Please check your email to reset your password")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }

    @GetMapping("/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) {

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message(this.userService.verifyAccountKey(key).isEnabled()? "Account already verified" : "Account verified")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyPassword(@PathVariable("key") String key) {
        UserDTO user = this.userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user))
                        .message("Please enter a new password")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }
    @PutMapping("/new/password")
    public ResponseEntity<HttpResponse> resetPasswordWithKey(@RequestBody @Valid NewPasswordForm form) {
        this.userService.updatePassword(form.getUserId(), form.getPasswword(), form.getConfirmPassword());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("Password reset successfuly")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    @PatchMapping("/update/password")
    public ResponseEntity<HttpResponse> updatePassword(Authentication authentication, @RequestBody @Valid UpdatePasswordForm form) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        this.userService.updatePassword(userDTO.getId(), form.getCurrentPassword(), form.getNewPassword(), form.getConfirmNewPassword());
        publisher.publishEvent(new NewUserEvent(PASSWORD_UPDATE, userDTO.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of("user", userService.getUserById(userDTO.getId()),"events" , this.eventService.getEventsByUserId(userDTO.getId()) , "roles", roleService.getRoles()))
                .timeStamp(LocalDateTime.now().toString())
                .message("Password updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }


    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
        if(isHeaderAndTokenValid(request)){
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserDTO user = userService.getUserById(tokenProvider.getSubject(token, request));
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("user",user, "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)), "refresh_token", token))
                            .message("Token refresh")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason("Refresh token missing or invalid")
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build()
        );
    }
    @PatchMapping("/update")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateForm user) throws InterruptedException {
        UserDTO updatedUser = this.userService.updateUserDetails(user);
        publisher.publishEvent(new NewUserEvent(PROFILE_UPDATE,updatedUser.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", updatedUser ,"events",  this.eventService.getEventsByUserId(updatedUser.getId()) , "roles", roleService.getRoles()))
                .message("User updated")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    @PatchMapping("/update/role/{roleName}")
    public ResponseEntity<HttpResponse> updateUserRole(Authentication authentication, @PathVariable("roleName") String roleName) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        this.userService.updateUserRole(userDTO.getId(), roleName);
        publisher.publishEvent(new NewUserEvent(ROLE_UPDATE, userDTO.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .data(Map.of("user", this.userService.getUserById(userDTO.getId()),"events" , this.eventService.getEventsByUserId(userDTO.getId()), "roles", roleService.getRoles()))
                .timeStamp(LocalDateTime.now().toString())
                .message("Role updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    @PatchMapping("/update/settings")
    public ResponseEntity<HttpResponse> updateAccountSettings(Authentication authentication, @RequestBody @Valid SettingsForm form) {
        UserDTO userDTO = getAuthenticatedUser(authentication);
        this.userService.updateAccountSettings(userDTO.getId(), form.getEnabled(), form.getNotLocked());
        publisher.publishEvent(new NewUserEvent(ACCOUNT_SETTINGS_UPDATE, userDTO.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .data(Map.of("user", userService.getUserById(userDTO.getId()),"events" , this.eventService.getEventsByUserId(userDTO.getId()), "roles", roleService.getRoles()))
                .timeStamp(LocalDateTime.now().toString())
                .message("Account settings updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    @PatchMapping("/togglemfa")
    public ResponseEntity<HttpResponse> toggleMfa(Authentication authentication) {
        UserDTO user = this.userService.toggleMfa(getAuthenticatedUser(authentication).getEmail());
        publisher.publishEvent(new NewUserEvent(MFA_UPDATE, user.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .data(Map.of("user", user, "events" , this.eventService.getEventsByUserId(user.getId()) ,"roles", this.roleService.getRoles()))
                .timeStamp(LocalDateTime.now().toString())
                .message("Multi_Factor Authentication update")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build()
        );
    }
    @PatchMapping("/update/image")
    public ResponseEntity<HttpResponse> updateProfileImage(Authentication authentication, @RequestParam("image")MultipartFile image){
        UserDTO user = getAuthenticatedUser(authentication);
        userService.updateImage(user, image);
        publisher.publishEvent(new NewUserEvent(PROFILE_PICTURE_UPDATE, user.getEmail()));
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .data(Map.of("user", this.userService.getUserById(user.getId()),"events" , this.eventService.getEventsByUserId(user.getId()), "roles", roleService.getRoles()))
                .timeStamp(LocalDateTime.now().toString())
                .message("Profile image updated")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }

    @GetMapping(path = "/image/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Documents/panvlaGit/resources/secureCapita/images/" + fileName + ".jpg"));
    }

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && tokenProvider.isTokenValid(tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request),
                request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()));
    }


    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login Success")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(toUser(userService.getUser(user.getEmail())), roleService.getRoleByUserId(user.getId()));
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user){
        this.userService.sendVerificationCode(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user))
                        .message("Verification Code Sent")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    private UserDTO authenticate(String email, String password) {
        try{
            if(null != userService.getUserByEmail(email)){
                publisher.publishEvent(new NewUserEvent(EventType.LOGIN_ATTEMPT, email));
            }
            Authentication authentication = this.authenticationManager.authenticate(unauthenticated(email, password));
            UserDTO loggedInUser  = getLoggedInUser(authentication);
            if(!loggedInUser.isUsingMfa()){
                publisher.publishEvent(new NewUserEvent(EventType.LOGIN_ATTEMPT_SUCCESS, email));
            }
            return loggedInUser;
        }catch (Exception exception){
            publisher.publishEvent(new NewUserEvent(EventType.LOGIN_ATTEMPT_FAILURE, email));
            ExceptionUtils.processError(request, response,exception);
            throw new ApiException((exception.getMessage()));
        }
    }
}
