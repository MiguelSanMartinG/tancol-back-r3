package com.main.tancol.almacen.service;

import com.main.tancol.auth.exceptions.exeption.GenericExeption;
import com.main.tancol.almacen.entities.Departamento;
import com.main.tancol.almacen.repositories.DepartamentoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class DepartamentoService {
    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {

        this.departamentoRepository = departamentoRepository;
    }

    public List<Departamento> getAllDepartamentos(){
        return (List<Departamento>) this.departamentoRepository.findAll();
    }

    public Departamento findOne(UUID id){
       return this.departamentoRepository.findById(id).orElse(null);
    }

    public Departamento create(Departamento departamento) {
         Departamento newDepa = Departamento.builder().nombre(departamento.getNombre()).activo(true).build();
        return this.departamentoRepository.save(newDepa);
    }

    public Departamento disable(UUID id) {
        Departamento departamento = this.departamentoRepository.findById(id).orElse(null);
        if(departamento == null) {
            throw new GenericExeption("Departamento no encontrado");
        }

        departamento.setActivo(!departamento.isActivo());
        this.departamentoRepository.save(departamento);
        return departamento;
    }
}
