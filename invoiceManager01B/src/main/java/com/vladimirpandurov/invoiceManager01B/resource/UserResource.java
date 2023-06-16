package com.vladimirpandurov.invoiceManager01B.resource;

import com.vladimirpandurov.invoiceManager01B.domain.HttpResponse;
import com.vladimirpandurov.invoiceManager01B.domain.User;
import com.vladimirpandurov.invoiceManager01B.domain.UserPrincipal;
import com.vladimirpandurov.invoiceManager01B.dto.UserDTO;
import com.vladimirpandurov.invoiceManager01B.exception.ApiException;
import com.vladimirpandurov.invoiceManager01B.form.LoginForm;
import com.vladimirpandurov.invoiceManager01B.form.SettingsForm;
import com.vladimirpandurov.invoiceManager01B.form.UpdateForm;
import com.vladimirpandurov.invoiceManager01B.form.UpdatePasswordForm;
import com.vladimirpandurov.invoiceManager01B.provider.TokenProvider;
import com.vladimirpandurov.invoiceManager01B.service.RoleService;
import com.vladimirpandurov.invoiceManager01B.service.UserService;
import com.vladimirpandurov.invoiceManager01B.utils.ExceptionUtils;
import com.vladimirpandurov.invoiceManager01B.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.vladimirpandurov.invoiceManager01B.dtomapper.UserDTOMapper.toUser;
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
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {

        Authentication authentication = authenticate(loginForm.getEmail(), loginForm.getPassword());
        UserDTO user = getLoggedInUser(authentication);
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
                .message("User created")
                .build()
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication){
        UserDTO user = this.userService.getUserByEmail(getAuthenticatedUser(authentication).getEmail());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user, "roles", this.roleService.getRoles()))
                        .message("Profile Retrieved")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }


    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code){
        UserDTO user = this.userService.verifyCode(email, code);
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
    @PostMapping("/resetpassword/{key}/{password}/{confirmPassword}")
    public ResponseEntity<HttpResponse> verifyPasswordUrl(@PathVariable("key") String key, @PathVariable("password") String password, @PathVariable("confirmPassword") String confirmPassword) {
        this.userService.renewPassword(key, password, confirmPassword);
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
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("Password updated successfully")
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
        TimeUnit.SECONDS.sleep(2);
        UserDTO updatedUser = this.userService.updateUserDetails(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", updatedUser))
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
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .data(Map.of("user", this.userService.getUserById(userDTO.getId()), "roles", roleService.getRoles()))
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
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .data(Map.of("user", userService.getUserById(userDTO.getId()), "roles", roleService.getRoles()))
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
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .data(Map.of("user", user, "roles", this.roleService.getRoles()))
                .timeStamp(LocalDateTime.now().toString())
                .message("Multi_Factor Authentication update")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build()
        );
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

    private Authentication authenticate(String email, String password) {
        try{
            Authentication authentication = this.authenticationManager.authenticate(unauthenticated(email, password));
            return authentication;
        }catch (Exception exception){
            //ExceptionUtils.processError(request, response,exception);
            throw new ApiException((exception.getMessage()));
        }
    }
}
