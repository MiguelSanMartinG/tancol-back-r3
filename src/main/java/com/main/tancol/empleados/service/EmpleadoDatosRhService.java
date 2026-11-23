package com.main.tancol.empleados.service;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.empleados.dto.EmpleadoRhDTOs;
import com.main.tancol.empleados.dto.request.EmpleadosRequestDTO;
import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import com.main.tancol.empleados.entities.EmpleadoContactoEmergenciaEntity;
import com.main.tancol.empleados.entities.EmpleadoDocumentoEntity;
import com.main.tancol.empleados.entities.EmpleadoDomicilio;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.enums.TipoDocumentoEmpleado;
import com.main.tancol.empleados.repositories.EmpleadoContactoEmergenciaRepository;
import com.main.tancol.empleados.repositories.EmpleadoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Datos de RH del empleado: personales, domicilio, contactos de emergencia (hasta 3) y documentos (uno por tipo).
 * Todo es opcional. Los errores de validación responden 409 (GenericExeption).
 */
@Service
@RequiredArgsConstructor
public class EmpleadoDatosRhService {

    static final int MAX_CONTACTOS = 3;
    static final Set<String> NACIONALIDADES = Set.of("MEXICANA", "EXTRANJERA");
    static final Set<String> SEXOS = Set.of("MASCULINO", "FEMENINO", "NO_ESPECIFICADO");
    static final Set<String> ESTADOS_CIVILES = Set.of("SOLTERO", "CASADO", "DIVORCIADO", "VIUDO", "UNION_LIBRE");
    static final Set<String> TIPOS_SANGRE = Set.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
    static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    static final Pattern CLABE = Pattern.compile("^\\d{18}$");
    static final Pattern MES = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");
    static final Pattern ARCHIVO = Pattern.compile("^[A-Za-z0-9._-]{1,300}$");

    private final EmpleadoContactoEmergenciaRepository contactoRepository;
    private final EmpleadoDocumentoRepository documentoRepository;

    /** Datos personales y domicilio (columnas del empleado). Se llama antes de guardar el empleado. */
    public void aplicarPersonales(EmpleadosEntity empleado, EmpleadosRequestDTO dto) {
        empleado.setNacionalidad(opcion(dto.getNacionalidad(), NACIONALIDADES, "La nacionalidad"));
        empleado.setSexo(opcion(dto.getSexo(), SEXOS, "El sexo"));
        empleado.setEstadoCivil(opcion(dto.getEstadoCivil(), ESTADOS_CIVILES, "El estado civil"));
        empleado.setTipoSangre(opcion(dto.getTipoSangre(), TIPOS_SANGRE, "El tipo de sangre"));
        String foto = texto(dto.getIdImagen());
        if (foto != null && !ARCHIVO.matcher(foto).matches()) {
            throw new GenericExeption("La foto del empleado no es válida.");
        }
        empleado.setIdImagen(foto);
        EmpleadoRhDTOs.Domicilio d = dto.getDomicilio();
        empleado.setDomicilio(d == null ? null : EmpleadoDomicilio.builder()
                .codigoPostal(texto(d.getCodigoPostal()))
                .tipoVialidad(texto(d.getTipoVialidad()))
                .nombreVialidad(texto(d.getNombreVialidad()))
                .numeroExterior(texto(d.getNumeroExterior()))
                .numeroInterior(texto(d.getNumeroInterior()))
                .colonia(texto(d.getColonia()))
                .localidad(texto(d.getLocalidad()))
                .municipio(texto(d.getMunicipio()))
                .entidadFederativa(texto(d.getEntidadFederativa()))
                .calle1(texto(d.getCalle1()))
                .calle2(texto(d.getCalle2()))
                .build());
    }

    /** Valida contactos y documentos sin guardar (para fallar antes de tocar la base). */
    public void validarListas(EmpleadosRequestDTO dto) {
        List<EmpleadoRhDTOs.ContactoEmergencia> contactos = dto.getContactosEmergencia();
        if (contactos != null) {
            if (contactos.size() > MAX_CONTACTOS) {
                throw new GenericExeption("Solo se permiten hasta " + MAX_CONTACTOS + " contactos de emergencia.");
            }
            for (EmpleadoRhDTOs.ContactoEmergencia c : contactos) {
                if (texto(c.getNombre()) == null || texto(c.getTelefono()) == null) {
                    throw new GenericExeption("Cada contacto de emergencia necesita nombre y teléfono.");
                }
                String correo = texto(c.getCorreo());
                if (correo != null && !CORREO.matcher(correo).matches()) {
                    throw new GenericExeption("El correo del contacto de emergencia no es válido.");
                }
            }
        }
        List<EmpleadoRhDTOs.Documento> documentos = dto.getDocumentos();
        if (documentos != null) {
            EnumSet<TipoDocumentoEmpleado> vistos = EnumSet.noneOf(TipoDocumentoEmpleado.class);
            for (EmpleadoRhDTOs.Documento d : documentos) {
                if (d.getTipo() == null) {
                    throw new GenericExeption("Falta el tipo de un documento.");
                }
                if (!vistos.add(d.getTipo())) {
                    throw new GenericExeption("El documento " + d.getTipo().etiqueta() + " viene repetido.");
                }
                String archivo = texto(d.getArchivo());
                if (archivo == null || !ARCHIVO.matcher(archivo).matches()) {
                    throw new GenericExeption("Falta el archivo del documento " + d.getTipo().etiqueta() + ".");
                }
                if (d.getMes() != null && !d.getMes().isBlank() && !MES.matcher(d.getMes().trim()).matches()) {
                    throw new GenericExeption("El mes del documento " + d.getTipo().etiqueta() + " debe tener formato AAAA-MM.");
                }
                String clabe = texto(d.getClabe());
                if (clabe != null && !CLABE.matcher(clabe).matches()) {
                    throw new GenericExeption("La CLABE debe tener 18 dígitos.");
                }
                if (d.getFecha() != null && d.getFechaVigencia() != null && d.getFechaVigencia().isBefore(d.getFecha())) {
                    throw new GenericExeption("La vigencia del documento " + d.getTipo().etiqueta()
                            + " no puede ser anterior a su fecha.");
                }
            }
        }
    }

    /** Reemplaza contactos y documentos del empleado ya guardado (solo las listas que vengan en el request). */
    public void guardarListas(EmpleadosEntity empleado, EmpleadosRequestDTO dto) {
        if (dto.getContactosEmergencia() != null) {
            contactoRepository.deleteByEmpleado_Id(empleado.getId());
            contactoRepository.flush();
            int orden = 1;
            for (EmpleadoRhDTOs.ContactoEmergencia c : dto.getContactosEmergencia()) {
                contactoRepository.save(EmpleadoContactoEmergenciaEntity.builder()
                        .empleado(empleado)
                        .orden(orden++)
                        .nombre(texto(c.getNombre()))
                        .parentesco(texto(c.getParentesco()))
                        .telefono(texto(c.getTelefono()))
                        .correo(texto(c.getCorreo()))
                        .build());
            }
        }
        if (dto.getDocumentos() != null) {
            documentoRepository.deleteByEmpleado_Id(empleado.getId());
            documentoRepository.flush();
            for (EmpleadoRhDTOs.Documento d : dto.getDocumentos()) {
                TipoDocumentoEmpleado tipo = d.getTipo();
                documentoRepository.save(EmpleadoDocumentoEntity.builder()
                        .empleado(empleado)
                        .tipo(tipo)
                        .archivo(texto(d.getArchivo()))
                        // Solo se guardan los datos que aplican a cada tipo.
                        .fecha(tipo.usaFecha() ? d.getFecha() : null)
                        .fechaVigencia(tipo.usaVigencia() ? d.getFechaVigencia() : null)
                        .mes(tipo.usaMes() ? texto(d.getMes()) : null)
                        .nivelEstudios(tipo.usaEstudios() ? d.getNivelEstudios() : null)
                        .banco(tipo.usaBanco() ? texto(d.getBanco()) : null)
                        .clabe(tipo.usaBanco() ? texto(d.getClabe()) : null)
                        .build());
            }
        }
    }

    /** Agrega contactos y documentos a la respuesta de un empleado. */
    public EmpleadosResponseDTO completar(EmpleadosResponseDTO respuesta, UUID empleadoId) {
        if (respuesta == null || empleadoId == null) return respuesta;
        List<EmpleadoRhDTOs.ContactoEmergencia> contactos = contactoRepository
                .findByEmpleado_IdAndDeletedFalseOrderByOrdenAsc(empleadoId).stream()
                .map(c -> EmpleadoRhDTOs.ContactoEmergencia.builder()
                        .nombre(c.getNombre())
                        .parentesco(c.getParentesco())
                        .telefono(c.getTelefono())
                        .correo(c.getCorreo())
                        .build())
                .toList();
        List<EmpleadoRhDTOs.Documento> documentos = documentoRepository
                .findByEmpleado_IdAndDeletedFalseOrderByTipoAsc(empleadoId).stream()
                .map(d -> EmpleadoRhDTOs.Documento.builder()
                        .tipo(d.getTipo())
                        .archivo(d.getArchivo())
                        .fecha(d.getFecha())
                        .fechaVigencia(d.getFechaVigencia())
                        .mes(d.getMes())
                        .nivelEstudios(d.getNivelEstudios())
                        .banco(d.getBanco())
                        .clabe(d.getClabe())
                        .build())
                .toList();
        return respuesta.toBuilder().contactosEmergencia(contactos).documentos(documentos).build();
    }

    private static String opcion(String valor, Set<String> permitidos, String campo) {
        String limpio = texto(valor);
        if (limpio == null) return null;
        String normalizado = limpio.toUpperCase(Locale.ROOT);
        if (!permitidos.contains(normalizado)) {
            throw new GenericExeption(campo + " no es válido.");
        }
        return normalizado;
    }

    private static String texto(String valor) {
        if (valor == null) return null;
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
