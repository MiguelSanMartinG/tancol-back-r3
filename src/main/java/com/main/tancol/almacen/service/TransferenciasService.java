package com.main.tancol.almacen.service;

import com.main.tancol.almacen.dto.request.TransferenciasRequestDTO;
import com.main.tancol.almacen.dto.response.TransferenciasResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.TransferenciasEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.entities.partidas.PartidaTransferenciaEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.almacen.repositories.TransferenciasRepository;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.almacen.service.interfaces.ITransferenciasService;
import com.main.tancol.almacen.util.TransferenciasPageSpecification;
import com.main.tancol.almacen.util.TransferenciasSpecification;
import com.main.tancol.almacen.util.TransferenciasValueMapper;
import com.main.tancol.common.dto.CatalogPage;
import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferenciasService implements ITransferenciasService {

    private final TransferenciasRepository repository;
    private final AlmacenRepository almacenRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    private final EmpleadosRepository empleadosRepository;
    private final UnidadesMedidaRepository unidadesMedidaEntityRepository;

    @Override
    @Transactional
    public TransferenciasResponseDTO create(TransferenciasRequestDTO dto) {

        EmpleadosEntity empleado = empleadosRepository.findById(dto.getIdUsuario()).orElseThrow(() -> new IDNotFound("No se encontró el empleado"));
        TransferenciasEntity transferenciasEntity = (TransferenciasEntity.builder()
                .fechaTransferencia(dto.getFecha())
                .motivo(dto.getMotivo())
                .comentarios(dto.getComentario())
                .empleado(empleado)
                .partidas(new ArrayList<>())
                .build());

        List<PartidaTransferenciaEntity> listaPartidas = getTransferenciaPartidas(dto, transferenciasEntity);

        for (PartidaTransferenciaEntity partidaTransferenciaEntity : listaPartidas) {
            partidaTransferenciaEntity.setPadrePartida(transferenciasEntity);
        }
        transferenciasEntity.setPartidas(listaPartidas);


        return TransferenciasValueMapper.TransferenciasEntityToResponse(this.repository.save(transferenciasEntity));
    }

    private List<PartidaTransferenciaEntity> getTransferenciaPartidas(TransferenciasRequestDTO dto, TransferenciasEntity transferenciasEntity) {
        return dto.getTransferencias().stream().map(
                (partida) -> {

                    EmpleadosEntity empleado = empleadosRepository.findById(dto.getIdUsuario())
                            .orElseThrow(() -> new IDNotFound((String.format("El usuario de la partida %d no existe", partida.getIndice()))));

                    AlmacenEntity origen = this.almacenRepository.findById(partida.getIdAlmacenOrigen()).orElseThrow(
                            () -> new IDNotFound((String.format("El almacen de origen de la partida %d no existe", partida.getIndice())))
                    );
                    AlmacenEntity destino = this.almacenRepository.findById(partida.getIdAlmacenDestino()).orElseThrow(
                            () -> new IDNotFound((String.format("El almacen de destino de la partida %d no existe", partida.getIndice())))
                    );
                    ProductoBasicoEntity productoBasico = productoRepository.findById(partida.getIdProducto()).orElseThrow(
                            () -> new IDNotFound((String.format("El Producto de la partida %d no existe", partida.getIndice())))
                    );
                    UnidadesMedidaEntity uMedida = partida.getUnidades() == null ? null :
                            unidadesMedidaEntityRepository.findById(partida.getUnidades())
                                    .orElseThrow(() -> new IDNotFound("Unidad de Medida no encontrada"));

                    return this.inventarioService.transferir(partida, origen, destino, productoBasico, empleado, uMedida);
                }
        ).toList();
    }

    @Override
    public TransferenciasResponseDTO getById(UUID id) {
        TransferenciasEntity  tf = this.repository.findById(id).orElseThrow(() -> new IDNotFound(id + " no encontrado"));
        return TransferenciasValueMapper.TransferenciasEntityToResponse(tf);
    }

    @Override
    public TransferenciasResponseDTO update(UUID id, TransferenciasRequestDTO dto) {
        return null;
    }

    @Override
    public TransferenciasResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<TransferenciasResponseDTO> getAll(Map<String, String> params) {
        List<TransferenciasEntity> list = repository.findAll(filterWithParameters(params));
        log.debug(list.toString());
        return list.stream().map(TransferenciasValueMapper::TransferenciasEntityToResponse).toList();
    }

    public Specification<TransferenciasEntity> filterWithParameters(Map<String, String> params) {
        return new TransferenciasSpecification().getSpecificationByFilters(params);
    }

    private static final Set<String> PAGE_FILTERS = Set.of("base", "destino", "entrego", "entregoel");
    private static final Map<String, String> PAGE_SORTS = Map.of(
            "entrego", "empleado.nombre",
            "entregoel", "fechaTransferencia"
    );
    /** Ordenes calculados con subconsulta en la especificacion (almacen de las partidas). */
    private static final Map<String, TransferenciasPageSpecification.OrdenCalculado> PAGE_SORTS_CALCULADOS = Map.of(
            "base", TransferenciasPageSpecification.OrdenCalculado.BASE,
            "destino", TransferenciasPageSpecification.OrdenCalculado.DESTINO
    );
    private static final Set<String> PAGE_SORT_KEYS = Set.of("base", "destino", "entrego", "entregoel");

    /** GET /transferencias/page. Orden por defecto: fecha de transferencia DESC. */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResponse<TransferenciasResponseDTO> getPage(Map<String, String> params) {
        CatalogPage query = CatalogPage.parse(params, PAGE_FILTERS, PAGE_SORT_KEYS);

        TransferenciasPageSpecification.OrdenCalculado ordenCalculado =
                query.sortKey() == null ? null : PAGE_SORTS_CALCULADOS.get(query.sortKey());
        Sort.Direction direccionCalculada = null;
        Sort sort;
        if (ordenCalculado != null) {
            String direction = query.sortDirection() == null ? "" : query.sortDirection().trim();
            if (!direction.equals("ASC") && !direction.equals("DESC")) {
                throw new FilterNotFound("Invalid filter: sort_" + query.sortKey());
            }
            direccionCalculada = Sort.Direction.valueOf(direction);
            sort = Sort.unsorted();
        } else {
            sort = query.sort(
                    PAGE_SORTS,
                    Sort.by(Sort.Order.desc("fechaTransferencia"), Sort.Order.desc("createdAt")),
                    "idTransferencia"
            );
        }

        var result = repository.findAll(
                new TransferenciasPageSpecification(query.search(), query.filters(), ordenCalculado, direccionCalculada),
                query.pageable(sort)
        );
        var items = result.getContent().stream().map(TransferenciasValueMapper::TransferenciasEntityToResponse).toList();
        return new PageResponse<>(items, result.getTotalElements(), query.page(), query.pageSize());
    }


}
