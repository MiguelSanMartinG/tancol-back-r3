package com.main.tancol.almacen.controller;

import com.main.tancol.almacen.entities.Departamento;
import com.main.tancol.almacen.service.DepartamentoService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/departamento")
@Hidden
@RestController
@Tag(name = "Departamento")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }


    @GetMapping
    List<Departamento> getAllDepartamentos(){
        System.out.println(departamentoService.getAllDepartamentos());
        return this.departamentoService.getAllDepartamentos();
    }

    @GetMapping("/{id}")
    Departamento getDepartamentoById(@PathVariable UUID id){
        return this.departamentoService.findOne(id);
    }

    @PostMapping
    Departamento createDepartamento(@RequestBody Departamento departamento){
        return this.departamentoService.create(departamento);
    }

    @DeleteMapping("/{id}")
    public Departamento disableDepartamento(@PathVariable UUID id){
        return this.departamentoService.disable(id);
    }



}
