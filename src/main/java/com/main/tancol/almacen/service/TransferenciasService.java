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
import com.main.tancol.almacen.util.TransferenciasSpecification;
import com.main.tancol.almacen.util.TransferenciasValueMapper;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


}
