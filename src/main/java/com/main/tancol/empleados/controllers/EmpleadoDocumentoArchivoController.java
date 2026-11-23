package com.main.tancol.empleados.controllers;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Archivos del expediente de RH de los empleados (PDF, JPG o PNG, hasta 10 MB).
 * Requieren sesión (no están en la lista de rutas públicas), a diferencia de las imágenes de catálogo.
 */
@RestController
@RequestMapping("/empleados/documentos/archivo")
@Tag(name = "📄 Documentos de empleados")
public class EmpleadoDocumentoArchivoController {

    private static final long MAX_BYTES = 10L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONES = Map.of(
            "application/pdf", ".pdf",
            "image/jpeg", ".jpg",
            "image/png", ".png");
    private static final Map<String, MediaType> TIPOS = Map.of(
            ".pdf", MediaType.APPLICATION_PDF,
            ".jpg", MediaType.IMAGE_JPEG,
            ".png", MediaType.IMAGE_PNG);
    /** Solo nombres generados aquí: UUID + extensión (evita rutas como ../). */
    private static final Pattern NOMBRE = Pattern.compile("^[0-9a-f-]{36}\\.(pdf|jpg|png)$");

    private final Path carpeta = Paths.get("uploads", "empleados");

    public EmpleadoDocumentoArchivoController() throws IOException {
        Files.createDirectories(carpeta);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> subir(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new GenericExeption("El archivo está vacío.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new GenericExeption("El archivo no puede pasar de 10 MB.");
        }
        String extension = EXTENSIONES.get(file.getContentType());
        if (extension == null) {
            throw new GenericExeption("Solo se permiten archivos PDF, JPG o PNG.");
        }
        String nombre = UUID.randomUUID() + extension;
        Files.copy(file.getInputStream(), carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
        return ResponseEntity.ok(Map.of("fileName", nombre));
    }

    @GetMapping("/{nombre:.+}")
    public ResponseEntity<Resource> ver(@PathVariable String nombre) throws IOException {
        if (!NOMBRE.matcher(nombre).matches()) {
            return ResponseEntity.notFound().build();
        }
        Path archivo = carpeta.resolve(nombre);
        if (!Files.exists(archivo)) {
            return ResponseEntity.notFound().build();
        }
        String extension = nombre.substring(nombre.lastIndexOf('.'));
        return ResponseEntity.ok()
                .contentType(TIPOS.get(extension))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombre + "\"")
                .body(new UrlResource(archivo.toUri()));
    }
}
