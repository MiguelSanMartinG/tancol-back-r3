package com.main.tancol.preferencias;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Preferencias del usuario en sesión (Buzón 7): objeto libre { clave: valor }.
 * PUT fusiona las claves recibidas (valor null = se borra la clave).
 */
@RestController
@RequestMapping("/usuarios/me/preferencias")
@RequiredArgsConstructor
@Tag(name = "⚙️ Preferencias de usuario")
public class PreferenciasController {

    private static final int MAX_CLAVE = 150;
    private static final int MAX_VALOR = 20_000;

    private final PreferenciaUsuarioRepository repository;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public Map<String, JsonNode> obtener() {
        Map<String, JsonNode> resultado = new LinkedHashMap<>();
        for (PreferenciaUsuarioEntity p : repository.findByEmpleado_IdAndDeletedFalse(usuario().getId())) {
            try {
                resultado.put(p.getClave(), objectMapper.readTree(p.getValor()));
            } catch (JsonProcessingException e) {
                // Valor ilegible: se omite.
            }
        }
        return resultado;
    }

    @PutMapping
    @Transactional
    public Map<String, JsonNode> guardar(@RequestBody Map<String, JsonNode> cambios) {
        EmpleadosEntity yo = usuario();
        cambios.forEach((clave, valor) -> {
            if (clave == null || clave.isBlank() || clave.length() > MAX_CLAVE) {
                throw new GenericExeption("Clave de preferencia no válida.");
            }
            var existente = repository.findByEmpleado_IdAndClave(yo.getId(), clave);
            if (valor == null || valor.isNull()) {
                existente.ifPresent(repository::delete);
                return;
            }
            String json = valor.toString();
            if (json.length() > MAX_VALOR) throw new GenericExeption("La preferencia «" + clave + "» es demasiado grande.");
            PreferenciaUsuarioEntity p = existente.orElseGet(() -> PreferenciaUsuarioEntity.builder().empleado(yo).clave(clave).build());
            p.setValor(json);
            p.setDeleted(false);
            repository.save(p);
        });
        return obtener();
    }

    private EmpleadosEntity usuario() {
        EmpleadosEntity e = EmpleadoContext.getEmpleadoActual();
        if (e == null) throw new GenericExeption("No se encontró el usuario en sesión.");
        return e;
    }
}
