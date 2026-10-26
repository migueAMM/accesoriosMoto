package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.application.dto.response.LoginResponse;
import com.uniquindio.accesoriosMoto.application.exception.CredencialesInvalidasException;
import com.uniquindio.accesoriosMoto.application.port.CodificadorContrasena;
import com.uniquindio.accesoriosMoto.application.port.EmisorToken;
import com.uniquindio.accesoriosMoto.domain.entity.Usuario;
import com.uniquindio.accesoriosMoto.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(readOnly=true)
public class IniciarSesionUseCase {
    private final UsuarioRepository usuarios;
    private final CodificadorContrasena contrasenas;
    private final EmisorToken tokens;

    public IniciarSesionUseCase(UsuarioRepository usuarios, CodificadorContrasena contrasenas, EmisorToken tokens) {
        this.usuarios = usuarios;
        this.contrasenas = contrasenas;
        this.tokens = tokens;
    }

    public LoginResponse ejecutar(String correo, String contrasena) {
        if (correo == null || contrasena == null) throw new CredencialesInvalidasException();
        Usuario usuario = usuarios.obtenerPorCorreo(correo.strip().toLowerCase(Locale.ROOT))
                .orElseThrow(CredencialesInvalidasException::new);
        if (!contrasenas.coincide(contrasena, usuario.getContrasena())) throw new CredencialesInvalidasException();
        return new LoginResponse(tokens.emitir(usuario), "Bearer", usuario.getId(), usuario.getRol().name());
    }
}
