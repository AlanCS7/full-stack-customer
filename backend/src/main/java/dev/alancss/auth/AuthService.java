package dev.alancss.auth;

import dev.alancss.customer.Customer;
import dev.alancss.customer.CustomerDTOMapper;
import dev.alancss.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper customerDTOMapper;
    private final JWTUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, CustomerDTOMapper customerDTOMapper, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(AuthRequest request) {
        var authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        var customer = (Customer) authenticate.getPrincipal();
        var customerDTO = customerDTOMapper.apply(customer);
        String token = jwtUtil.issueToken(customer.getUsername(), customerDTO.roles().toArray(new String[0]));

        return new AuthResponse(token);
    }
}
