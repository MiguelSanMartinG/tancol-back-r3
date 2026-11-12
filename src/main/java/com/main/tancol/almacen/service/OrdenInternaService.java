package com.main.tancol.almacen.service;

import com.main.tancol.almacen.dto.request.OrdenInternaRequestDTO;
import com.main.tancol.almacen.dto.request.partidas.PartidaOrdenInternaRequestDTO;
import com.main.tancol.almacen.dto.response.OrdenInternaResponseDTO;
import com.main.tancol.almacen.repositories.OrdenInternaRepository;
import com.main.tancol.almacen.util.OrdenInternaValueMapper;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.main.tancol.almacen.entities.OrdenInternaEntity;
import com.main.tancol.almacen.util.OrdenInternaSpecification;
import com.main.tancol.almacen.service.interfaces.IOrdenInternaService;
import com.main.tancol.common.service.FolioService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrdenInternaService implements IOrdenInternaService {

    private final OrdenInternaRepository repository;
    private final EmpleadosRepository empleadoRepository;
    private final PartidaOrdenInternaService partidaOrdenInternaService;
    private final FolioService folioService;
    private final WhitelistProductoService whitelistProductoService;

    public OrdenInternaService(OrdenInternaRepository repository, EmpleadosRepository empleadoRepository, PartidaOrdenInternaService partidaOrdenInternaService, FolioService folioService, WhitelistProductoService whitelistProductoService) {
        this.repository = repository;
        this.empleadoRepository = empleadoRepository;
        this.partidaOrdenInternaService = partidaOrdenInternaService;
        this.folioService = folioService;
        this.whitelistProductoService = whitelistProductoService;
    }


    @Override
    @Transactional
    public OrdenInternaResponseDTO create(OrdenInternaRequestDTO dto) {
        EmpleadosEntity receptor = this.empleadoRepository.findById(dto.getEmpleadoReceptor()).orElseThrow(() -> new IDNotFound("Empleado-Receptor no encontrado"));
        EmpleadosEntity emisor = this.empleadoRepository.findById(dto.getEmpleadoEmisor()).orElseThrow(() -> new IDNotFound("Empleado-Emisor no encontrado"));
        List<UUID> productos = dto.getPartidas() == null
                ? List.of()
                : dto.getPartidas().stream().map(PartidaOrdenInternaRequestDTO::getIdProductoBasico).toList();
        this.whitelistProductoService.validarSolicitud(receptor, productos);

        OrdenInternaEntity ordenInterna = OrdenInternaValueMapper.OrdenInternaRequestToEntity(dto,emisor,receptor);
        ordenInterna.setFolio(folioService.generarFolio(3));
        ordenInterna = this.repository.save(ordenInterna);

        ordenInterna.setPartidas(this.partidaOrdenInternaService.createListPartida(dto.getPartidas(), ordenInterna));



        return OrdenInternaValueMapper.OrdenInternaEntityToResponse(ordenInterna);
    }

    @Override
    public OrdenInternaResponseDTO getById(UUID id) {
        return OrdenInternaValueMapper.OrdenInternaEntityToResponse(
                this.repository.findById(id).orElseThrow(()-> new IDNotFound("No se encontro la orden.")));
    }

    @Override
    public OrdenInternaResponseDTO update(UUID id, OrdenInternaRequestDTO dto) {
        return null;
    }

    @Override
    public OrdenInternaResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<OrdenInternaResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.by(Sort.Direction.DESC, "folio");
        List<OrdenInternaEntity> list = repository.findAll(filterWithParameters(params), sort);
        return list.stream().map(OrdenInternaValueMapper::OrdenInternaEntityToResponse).toList();
    }

    public Specification<OrdenInternaEntity> filterWithParameters(Map<String, String> params) {
        return new OrdenInternaSpecification().getSpecificationByFilters(params);
    }

}
