package pe.edu.upeu.vetandes.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pe.edu.upeu.vetandes.exception.ApiErrorWriter;
import org.springframework.http.HttpStatus;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ApiErrorWriter.write(response, HttpStatus.UNAUTHORIZED,
                "Autenticación requerida o token inválido", request.getRequestURI());
    }
}
