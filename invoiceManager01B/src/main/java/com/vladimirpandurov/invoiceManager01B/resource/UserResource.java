package com.vladimirpandurov.invoiceManager01B.resource;

import com.vladimirpandurov.invoiceManager01B.domain.HttpResponse;
import com.vladimirpandurov.invoiceManager01B.domain.User;
import com.vladimirpandurov.invoiceManager01B.domain.UserPrincipal;
import com.vladimirpandurov.invoiceManager01B.dto.UserDTO;
import com.vladimirpandurov.invoiceManager01B.form.LoginForm;
import com.vladimirpandurov.invoiceManager01B.provider.TokenProvider;
import com.vladimirpandurov.invoiceManager01B.service.RoleService;
import com.vladimirpandurov.invoiceManager01B.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping(path="/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserResource {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));
        UserDTO user = this.userService.getUserByEmail(loginForm.getEmail());
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

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                "refresh_toke", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login Success")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(userService.getUser(user.getEmail()), roleService.getRoleByUserId(user.getId()).getPermission());
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
}
