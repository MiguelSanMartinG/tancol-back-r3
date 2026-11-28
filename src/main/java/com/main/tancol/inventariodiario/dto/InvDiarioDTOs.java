package com.main.tancol.inventariodiario.dto;

import com.main.tancol.inventariodiario.enums.ConceptoEntrega;
import com.main.tancol.inventariodiario.enums.EstatusSolicitud;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Peticiones y respuestas del inventario diario (contrato del Buzón 1 y 2). */
public final class InvDiarioDTOs {

    private InvDiarioDTOs() {
    }

    // ------------------------------------------------------------------ catálogos
    public record Area(UUID id, String nombre, UUID almacenId, String almacenNombre) {}

    /** totalInsumos: insumos con registro en el almacén (Inventario inicial). */
    public record Almacen(UUID id, String nombre, UUID responsableId, String responsableNombre, long totalInsumos) {}

    public record Insumo(UUID id, String nombre, String unidad, BigDecimal existencia) {}

    public record Persona(UUID id, String nombre) {}

    public record Ruta(UUID id, String nombre) {}

    public record Catalogos(List<Area> areas, List<Almacen> almacenes, List<Persona> empleados, List<Ruta> rutas) {}

    public record MisPermisos(boolean esAdmin, List<UUID> responsableArea, List<UUID> responsableAlmacen) {}

    // ------------------------------------------------------------------ cupos
    public record SolicitudAbierta(UUID id, String folio, EstatusSolicitud estatus) {}

    /**
     * consumido: solo lo firmado; pendienteFirma: lo registrado hoy sin firmar;
     * disponible = inicial + ampliado − consumido − pendienteFirma; existencia: la real del almacén.
     */
    public record Cupo(UUID id, UUID areaId, UUID insumoId, String insumoNombre, String unidad,
                       UUID almacenId, String almacenNombre, boolean porDefecto,
                       BigDecimal base, BigDecimal minimo,
                       BigDecimal inicial, BigDecimal ampliado, BigDecimal consumido, BigDecimal disponible,
                       BigDecimal faltante, BigDecimal existencia, SolicitudAbierta solicitudAbierta,
                       BigDecimal pendienteFirma, UUID creadoPorId, String creadoPor, String idImagen) {}

    public record CupoCrear(UUID areaId, UUID almacenId, UUID insumoId, BigDecimal base, BigDecimal minimo) {}

    public record CupoEditar(BigDecimal base, BigDecimal minimo) {}

    // ------------------------------------------------------------------ confirmación
    public record Confirmacion(UUID areaId, LocalDate fecha, String confirmadoPor, LocalDateTime confirmadoEn) {}

    public record PreviaCupo(UUID cupoId, UUID insumoId, String insumoNombre, String unidad, BigDecimal base,
                             BigDecimal existencia, BigDecimal seEntrega, BigDecimal faltante) {}

    public record ConfirmarDia(UUID areaId) {}

    public record Faltante(UUID insumoId, String insumo, BigDecimal faltante) {}

    public record ResultadoConfirmacion(Confirmacion confirmacion, List<Faltante> faltantes) {}

    // ------------------------------------------------------------------ partidas
    public record Partida(UUID id, String folio, LocalDateTime fecha, UUID areaId, UUID rutaId, String rutaNombre,
                          UUID insumoId, String insumoNombre, BigDecimal cantidad, ConceptoEntrega concepto,
                          String comentario, String rotoPor, String rotoPorNombre, UUID usuarioId, String usuario,
                          UUID firmanteId, String firmante, String firma, LocalDateTime fechaFirma) {}

    /** rotoPor: id de empleado o "MAQUINA" (solo con CAMBIO_ROTO). */
    public record PartidaCrear(UUID areaId, UUID rutaId, UUID insumoId, BigDecimal cantidad,
                               ConceptoEntrega concepto, String comentario, String rotoPor) {}

    /** Solo se aplican los campos que vienen (null = sin cambio); rutaId vacío en "quitarRuta". */
    public record PartidaEditar(UUID rutaId, Boolean quitarRuta, UUID insumoId, BigDecimal cantidad,
                                ConceptoEntrega concepto, String comentario, String rotoPor) {}

    public record Ids(List<UUID> ids) {}

    public record Eliminadas(int eliminadas) {}

    // ------------------------------------------------------------------ solicitudes
    public record Solicitud(UUID id, String folio, UUID areaId, String areaNombre, UUID insumoId, String insumoNombre,
                            String unidad, UUID almacenId, String almacenNombre, BigDecimal existencia,
                            BigDecimal cantidadSolicitada, BigDecimal cantidadAEntregar, BigDecimal cantidadRecibida,
                            String nota, UUID solicitadoPorId, String solicitadoPor, LocalDateTime fechaSolicitud,
                            EstatusSolicitud estatus, String confirmadoPor, LocalDateTime fechaConfirmacion,
                            String comentario, LocalDateTime fechaRecepcion, String recibidoPor, String firma,
                            String firmaAlmacen, LocalDateTime fechaFirmaAlmacen, String firmadoAlmacenPor) {}

    public record SolicitudCrear(UUID areaId, UUID insumoId, BigDecimal cantidad, String nota) {}

    public record SolicitudEditar(BigDecimal cantidadSolicitada, String nota, BigDecimal cantidadRecibida) {}

    public record Aceptar(BigDecimal cantidadAEntregar, String comentario) {}

    public record Rechazar(String comentario) {}

    public record Declinar(String motivo) {}
}
