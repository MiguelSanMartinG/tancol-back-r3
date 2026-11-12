package com.main.tancol.cliente.service;

import com.main.tancol.cliente.dto.request.ClienteProductoRequestDTO;
import com.main.tancol.cliente.dto.response.ClienteProductoResponseDTO;
import com.main.tancol.cliente.dto.response.HistoricoPrecioClienteResponseDTO;
import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.cliente.entities.ClientePrecioHistorialEntity;
import com.main.tancol.cliente.entities.ClienteProductoEntity;
import com.main.tancol.cliente.enums.TipoActualizacionPrecio;
import com.main.tancol.cliente.repositories.ClientePrecioHistorialRepository;
import com.main.tancol.cliente.repositories.ClienteProductoRelRepository;
import com.main.tancol.cliente.repositories.ClienteRepository;
import com.main.tancol.cliente.util.ClienteProductoRelSpecification;
import com.main.tancol.cliente.util.ClienteProductoRelValueMapper;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteProductoService {

    private final ClienteProductoRelRepository repository;
    private final ClienteRepository clienteRepository;
    private final ProductoComercialRepository productoComercialRepository;
    private final ClientePrecioHistorialRepository historialRepository;


    public ClienteProductoResponseDTO getById(UUID id) {
        return ClienteProductoRelValueMapper.ClienteProductoEntityToResponse(this.repository.findById(id)
                .orElseThrow(()-> new IDNotFound("Cliente no encontrado")));
    }

    public List<HistoricoPrecioClienteResponseDTO> getHistorialPrecios(UUID idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new IDNotFound("Cliente no encontrado");
        }
        return historialRepository.findByCliente_IdOrderByFechaDescIdDesc(idCliente).stream()
                .map(this::toHistorialResponse)
                .toList();
    }

    @Transactional
    public Set<ClienteProductoResponseDTO> update(UUID idCliente, Set<ClienteProductoRequestDTO> dto) {

        List<ClienteProductoEntity> copy = new LinkedList<>(repository.findByCliente_Id(idCliente));
        List<CambioPrecio> cambios = new ArrayList<>();
        List<ClienteProductoEntity> listaNueva = (List<ClienteProductoEntity>) this.repository.saveAll(dto.stream().map((dtoItem) -> {

            ClienteProductoEntity newCP;

            log.info(String.valueOf(dtoItem.getIdPrecioCliente()));

            if(dtoItem.getIdPrecioCliente() != null){


                ClienteProductoEntity cp = repository.findById(dtoItem.getIdPrecioCliente()).orElseThrow(
                        () -> new IDNotFound("No se encontro el idPrecio"));

                if(!cp.getCliente().getId().equals(idCliente)){
                    throw new IDNotFound("No es el idPrecio correcto no concuerda con el cliente");
                }

                if (cambioDescuento(cp.getDescuento(), dtoItem.getDescuento())) {
                    cambios.add(new CambioPrecio(cp.getProductoComercial(), cp.getDescuento(), dtoItem.getDescuento()));
                }
                cp.setDescuento(dtoItem.getDescuento());
                newCP = repository.save(cp);
            }
            else {

                ClienteEntity cliente = clienteRepository.findById(idCliente).orElseThrow(
                        () -> new IDNotFound("No se encontro el cliente"));

                ProductoComercialEntity pc = productoComercialRepository.findById(dtoItem.getIdProducto()).orElseThrow(() ->
                        new IDNotFound("No se encontro el producto"));

                cambios.add(new CambioPrecio(pc, null, dtoItem.getDescuento()));
                ClienteProductoEntity clienteProducto = ClienteProductoEntity.builder()
                        .productoComercial(pc)
                        .cliente(cliente)
                        .descuento(dtoItem.getDescuento())
                        .build();
                newCP = repository.save(clienteProducto);

            }


            return newCP;
        }).collect(Collectors.toSet()));

        copy.removeAll(listaNueva);
        copy.forEach((miembro) -> {
            cambios.add(new CambioPrecio(miembro.getProductoComercial(), miembro.getDescuento(), null));
            repository.deleteById(miembro.getId());
        });

        registrarHistorial(idCliente, cambios);

        return repository.findByCliente_Id(idCliente).stream().map(ClienteProductoRelValueMapper::ClienteProductoEntityToResponse
        ).collect(Collectors.toSet());
    }

    private void registrarHistorial(UUID idCliente, List<CambioPrecio> cambios) {
        if (cambios.isEmpty()) {
            return;
        }

        EmpleadosEntity empleado = EmpleadoContext.getEmpleadoActual();
        if (ObjectUtils.isEmpty(empleado) || !StringUtils.hasText(empleado.getNombreUsuario())) {
            throw new IllegalArgumentException("No se encontró el usuario autenticado");
        }

        ClienteEntity cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IDNotFound("No se encontro el cliente"));
        TipoActualizacionPrecio tipo = cambios.size() == 1
                ? TipoActualizacionPrecio.INDIVIDUAL
                : TipoActualizacionPrecio.MASIVA;
        UUID operacionId = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();

        historialRepository.saveAll(cambios.stream().map(cambio -> {
            ProductoComercialEntity producto = cambio.producto();
            return ClientePrecioHistorialEntity.builder()
                    .operacionId(operacionId)
                    .cliente(cliente)
                    .productoComercial(producto)
                    .productoNombre(producto != null ? producto.getNombre() : null)
                    .precioAnterior(cambio.precioAnterior())
                    .precioNuevo(cambio.precioNuevo())
                    .usuario(empleado.getNombreUsuario())
                    .fecha(fecha)
                    .tipoActualizacion(tipo)
                    .build();
        }).toList());
    }

    private HistoricoPrecioClienteResponseDTO toHistorialResponse(ClientePrecioHistorialEntity registro) {
        ProductoComercialEntity producto = registro.getProductoComercial();
        String nombre = registro.getProductoNombre();
        if (!StringUtils.hasText(nombre) && producto != null) {
            nombre = producto.getNombre();
        }
        return HistoricoPrecioClienteResponseDTO.builder()
                .id(registro.getId())
                .operacionId(registro.getOperacionId())
                .idProducto(producto != null ? producto.getId() : null)
                .producto(nombre)
                .precioAnterior(registro.getPrecioAnterior())
                .precioNuevo(registro.getPrecioNuevo())
                .usuario(registro.getUsuario())
                .fecha(registro.getFecha())
                .tipoActualizacion(registro.getTipoActualizacion())
                .build();
    }

    private boolean cambioDescuento(BigDecimal anterior, BigDecimal nuevo) {
        if (anterior == null && nuevo == null) {
            return false;
        }
        if (anterior == null || nuevo == null) {
            return true;
        }
        return anterior.compareTo(nuevo) != 0;
    }

    private record CambioPrecio(ProductoComercialEntity producto, BigDecimal precioAnterior, BigDecimal precioNuevo) {
    }


    public List<ClienteProductoResponseDTO> getAll(Map<String, String> params) {
        List<ClienteProductoEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(ClienteProductoRelValueMapper::ClienteProductoEntityToResponse).toList();
    }

    public Specification<ClienteProductoEntity> filterWithParameters(Map<String, String> params) {
        return new ClienteProductoRelSpecification().getSpecificationByFilters(params);
    }






}
