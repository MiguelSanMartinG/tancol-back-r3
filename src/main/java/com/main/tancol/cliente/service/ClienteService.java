package com.main.tancol.cliente.service;

import com.main.tancol.cliente.dto.request.ClienteRequestDTO;
import com.main.tancol.cliente.dto.response.ClienteNombreHistorialResponseDTO;
import com.main.tancol.cliente.dto.response.ClienteResponseDTO;
import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.cliente.entities.ClienteNombreHistorialEntity;
import com.main.tancol.cliente.entities.TipoClienteEntity;
import com.main.tancol.cliente.enums.TipoNombreHistorial;
import com.main.tancol.cliente.repositories.ClienteNombreHistorialRepository;
import com.main.tancol.cliente.repositories.ClienteRepository;
import com.main.tancol.cliente.repositories.TiposClientesRepository;
import com.main.tancol.cliente.service.interfaces.IClienteService;
import com.main.tancol.cliente.util.ClienteSpecification;
import com.main.tancol.cliente.util.ClienteValueMapper;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.repositories.RutasRepository;
import com.main.tancol.proveedor.entity.InformacionFiscal;
import com.main.tancol.proveedor.enums.TipoPago;
import com.main.tancol.proveedor.util.InformacionFiscalValueMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClienteService implements IClienteService {

    private static final int MAX_HISTORIAL_NOMBRES = 2;
    private static final UUID TIPO_CLIENTE_EMPLEADO_ID =
            UUID.fromString("e4c70e69-2817-4c12-b0eb-01333c0da553");

    private final ClienteRepository repository;
    private final TiposClientesRepository tiposClientesRepository;
    private final RutasRepository rutasRepository;
    private final ClienteNombreHistorialRepository historialRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Integer getNextIdExterno() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('cliente_id_externo_seq')")
                .getSingleResult()).intValue();
    }

    public ClienteService(ClienteRepository repository,
                          TiposClientesRepository tiposClientesRepository,
                          RutasRepository rutasRepository,
                          ClienteNombreHistorialRepository historialRepository) {
        this.repository = repository;
        this.tiposClientesRepository = tiposClientesRepository;
        this.rutasRepository = rutasRepository;
        this.historialRepository = historialRepository;
    }


    @Override
    public ClienteResponseDTO create(ClienteRequestDTO dto) {
        TipoPago tipoPago = null;

        try {
            tipoPago = TipoPago.valueOf(dto.getTipoPago().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IDNotFound("El tipo de pago esta escrito incorrectamente"); // o lanzar una excepción personalizada, o retornar un valor por defecto
        }

        if(tipoPago.equals(TipoPago.CREDITO) && dto.getDiasDeCredito() == null) {
            throw new IDNotFound("Es necesario que introduzca dias de crédito");
        }

        RutasEntity ruta = null;
        if(dto.getIdRuta() != null) {
            ruta = rutasRepository.findById(dto.getIdRuta()).orElseThrow(() -> new IDNotFound("El ruta no existe"));
        }

        TipoClienteEntity query = this.tiposClientesRepository.findById(dto.getIdTipoDeCliente())
                .orElseThrow(() -> new IDNotFound("No se encontró el tipo de cliente"));

        ClienteEntity cliente = ClienteValueMapper.ClienteRequestToEntity(dto , query, tipoPago, ruta);
        cliente.setIdCliente(getNextIdExterno());

        ClienteEntity newCliente = repository.save(cliente);
        return ClienteValueMapper.ClienteEntityToResponse(newCliente);
    }

    @Override
    public void crearDesdeEmpleado(EmpleadosEntity empleado) {
        TipoClienteEntity tipoEmpleado = tiposClientesRepository
                .findById(TIPO_CLIENTE_EMPLEADO_ID)
                .orElseThrow(() -> new IDNotFound("No se encontró el tipo de cliente Empleado"));

        ClienteEntity cliente = ClienteEntity.builder()
                .nombreComercial(nombreComercialDesdeEmpleado(empleado))
                .tipoDeCliente(tipoEmpleado)
                .tipoPago(TipoPago.CONTADO)
                .fechaAlta(LocalDateTime.now())
                .correo(empleado.getCorreo())
                .telefono(empleado.getTelefono())
                .esEmpleado(true)
                .build();
        cliente.setIdCliente(getNextIdExterno());
        repository.save(cliente);
    }

    private String nombreComercialDesdeEmpleado(EmpleadosEntity empleado) {
        String nombreCompleto = Stream.of(empleado.getNombre(), empleado.getPrimerApellido(), empleado.getSegundoApellido())
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.joining(" "));
        if (StringUtils.hasText(nombreCompleto)) {
            return nombreCompleto;
        }
        if (StringUtils.hasText(empleado.getNombreUsuario())) {
            return empleado.getNombreUsuario().trim();
        }
        return "Empleado";
    }

    @Override
    public ClienteResponseDTO getById(UUID id) {
        ClienteEntity query = repository.findById(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Map<UUID, Map<TipoNombreHistorial, List<String>>> historiales =
                cargarUltimosHistoriales(List.of(query.getId()));
        return mapearConHistorial(query, historiales);
    }

    @Override
    public ClienteResponseDTO update(UUID id, ClienteRequestDTO dto) {
        ClienteEntity query = repository.findById(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        TipoClienteEntity query2 = tiposClientesRepository.findById(dto.getIdTipoDeCliente()).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        TipoPago tipoPago = null;
        try {
            tipoPago = TipoPago.valueOf(dto.getTipoPago().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IDNotFound("El tipo de pago esta escrito incorrectamente"); // o lanzar una excepción personalizada, o retornar un valor por defecto
        }

        if(tipoPago.equals(TipoPago.CREDITO) && dto.getDiasDeCredito() == null) {
            throw new IDNotFound("Es necesario que introduzca dias de crédito");
        }

        String nuevaRazonSocial = dto.getInformacionFiscal() != null
                ? dto.getInformacionFiscal().getRazonSocial()
                : null;
        String razonSocialActual = Optional.ofNullable(query.getInformacionFiscal())
                .map(InformacionFiscal::getRazonSocial)
                .orElse(null);

        registrarCambioHistorial(query, TipoNombreHistorial.NOMBRE_COMERCIAL, query.getNombreComercial(), dto.getNombreComercial());
        registrarCambioHistorial(query, TipoNombreHistorial.RAZON_SOCIAL, razonSocialActual, nuevaRazonSocial);

        query.setNombreComercial(dto.getNombreComercial());
        query.setTipoPago(tipoPago);
        query.setDiasDeCredito(dto.getDiasDeCredito());
        query.setCorreo(dto.getCorreo());
        query.setFechaAlta(dto.getFechaAlta());
        query.setTelefono(dto.getTelefono());
        query.setDiasDeCredito(dto.getDiasDeCredito());
        query.setTipoDeCliente(query2);
        query.setInformacionFiscal(InformacionFiscalValueMapper.InformacionFiscalRequestToEntity(dto.getInformacionFiscal()));
        query.setComentario(dto.getComentario());
        ClienteEntity guardado = repository.save(query);
        Map<UUID, Map<TipoNombreHistorial, List<String>>> historiales =
                cargarUltimosHistoriales(List.of(guardado.getId()));
        return mapearConHistorial(guardado, historiales);
    }

    @Override
    public List<ClienteNombreHistorialResponseDTO> getHistorialNombreComercial(UUID id) {
        return getHistorial(id, TipoNombreHistorial.NOMBRE_COMERCIAL);
    }

    @Override
    public List<ClienteNombreHistorialResponseDTO> getHistorialRazonSocial(UUID id) {
        return getHistorial(id, TipoNombreHistorial.RAZON_SOCIAL);
    }

    private List<ClienteNombreHistorialResponseDTO> getHistorial(UUID id, TipoNombreHistorial tipo) {
        if (!repository.existsById(id)) {
            throw new IDNotFound("Cliente no encontrado");
        }
        return historialRepository.findByClienteIdAndTipoOrderByFechaDesc(id, tipo).stream()
                .map(h -> ClienteNombreHistorialResponseDTO.builder()
                        .valor(h.getValor())
                        .usuario(h.getUsuario())
                        .fecha(h.getFecha())
                        .build())
                .toList();
    }

    private void registrarCambioHistorial(ClienteEntity cliente,
                                          TipoNombreHistorial tipo,
                                          String valorActual,
                                          String valorNuevo) {
        if (!huboCambio(valorActual, valorNuevo) || !StringUtils.hasText(valorActual)) {
            return;
        }

        Optional<ClienteNombreHistorialEntity> ultimo =
                historialRepository.findFirstByClienteIdAndTipoOrderByFechaDesc(cliente.getId(), tipo);
        if (ultimo.isPresent() && Objects.equals(ultimo.get().getValor(), valorActual)) {
            return;
        }

        EmpleadosEntity empleado = EmpleadoContext.getEmpleadoActual();
        if (ObjectUtils.isEmpty(empleado)) {
            throw new IllegalArgumentException("No se encontró el usuario autenticado");
        }

        historialRepository.save(ClienteNombreHistorialEntity.builder()
                .cliente(cliente)
                .tipo(tipo)
                .valor(valorActual)
                .usuario(empleado.getNombreUsuario())
                .fecha(LocalDateTime.now())
                .build());

        limitarHistorial(cliente.getId(), tipo);
    }

    private void limitarHistorial(UUID clienteId, TipoNombreHistorial tipo) {
        List<ClienteNombreHistorialEntity> historial =
                historialRepository.findByClienteIdAndTipoOrderByFechaDesc(clienteId, tipo);
        if (historial.size() <= MAX_HISTORIAL_NOMBRES) {
            return;
        }
        historialRepository.deleteAll(historial.subList(MAX_HISTORIAL_NOMBRES, historial.size()));
    }

    private boolean huboCambio(String valorActual, String valorNuevo) {
        return !Objects.equals(valorActual, valorNuevo);
    }

    @Override
    public ClienteResponseDTO delete(UUID id) {
        ClienteEntity query = repository.findById(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        query.setDeleted(true);
        repository.save(query);
        return ClienteValueMapper.ClienteEntityToResponse(repository.save(query));
    }

    @Override
    public List<ClienteResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.unsorted();
        sort = Sort.by(Sort.Direction.ASC, "idCliente");
        List<ClienteEntity> list = repository.findAll(filterWithParameters(params),sort);
        if (list.isEmpty()) {
            return List.of();
        }
        Map<UUID, Map<TipoNombreHistorial, List<String>>> historiales =
                cargarUltimosHistoriales(list.stream().map(ClienteEntity::getId).toList());
        return list.stream().map(cliente -> mapearConHistorial(cliente, historiales)).toList();
    }

    private ClienteResponseDTO mapearConHistorial(ClienteEntity cliente,
                                                  Map<UUID, Map<TipoNombreHistorial, List<String>>> historiales) {
        Map<TipoNombreHistorial, List<String>> porTipo =
                historiales.getOrDefault(cliente.getId(), Collections.emptyMap());
        return ClienteValueMapper.ClienteEntityToResponse(
                cliente,
                porTipo.getOrDefault(TipoNombreHistorial.NOMBRE_COMERCIAL, Collections.emptyList()),
                porTipo.getOrDefault(TipoNombreHistorial.RAZON_SOCIAL, Collections.emptyList())
        );
    }

    private Map<UUID, Map<TipoNombreHistorial, List<String>>> cargarUltimosHistoriales(List<UUID> clienteIds) {
        if (clienteIds == null || clienteIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ClienteNombreHistorialEntity> registros =
                historialRepository.findByClienteIdInOrderByFechaDesc(clienteIds);

        Map<UUID, Map<TipoNombreHistorial, List<String>>> resultado = new HashMap<>();
        for (ClienteNombreHistorialEntity registro : registros) {
            UUID clienteId = registro.getCliente().getId();
            Map<TipoNombreHistorial, List<String>> porTipo =
                    resultado.computeIfAbsent(clienteId, id -> new EnumMap<>(TipoNombreHistorial.class));
            List<String> valores = porTipo.computeIfAbsent(registro.getTipo(), t -> new ArrayList<>());
            if (valores.size() < MAX_HISTORIAL_NOMBRES) {
                valores.add(registro.getValor());
            }
        }
        return resultado;
    }

    public Specification<ClienteEntity> filterWithParameters(Map<String, String> params) {
        return new ClienteSpecification().getSpecificationByFilters(params);
    }
}
