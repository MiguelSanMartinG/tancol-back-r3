# .\create_springboot_module.ps1 com.main.tancol.solicitudCompra PartidaCompra

param (
    [string]$PackageName,
    [string]$ModuleName
)

# Verifica que se hayan pasado los argumentos necesarios
if (-not $ModuleName -or -not $PackageName) {
    Write-Host "Uso: .\create_springboot_module.ps1 <nombre_del_modulo> <nombre_del_paquete>"
    Write-Host "Ejemplo: .\create_springboot_module.ps1 Almacen com.miempresa.miproyecto"
    exit
}

# Convertir el nombre del paquete en una ruta de directorio
$PackagePath = $PackageName.Replace(".", "/").ToLower()
$ModuleNameLowerCase = $ModuleName.ToLower();

# Ruta base del proyecto (ajusta según tu estructura)
$BasePath = "src/main/java/$PackagePath/$ModuleName"
$BasePath = $BasePath.ToLower()

# Crear la estructura de directorios
$directories = @(
    "$BasePath/entities",
    "$BasePath/controllers",
    "$BasePath/dto",
    "$BasePath/dto/request",
    "$BasePath/dto/response",
    "$BasePath/repositories",
    "$BasePath/service",
    "$BasePath/service/interfaces",
    "$BasePath/util"
)

foreach ($dir in $directories) {
    New-Item -ItemType Directory -Path $dir -Force
}

# Plantillas para los archivos .java

# Entity
@"
package $PackageName.$ModuleName.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.*;
import java.util.*;

@Entity
@Table
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ${ModuleName}Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Agrega más campos aquí
}
"@ | Out-File -FilePath "$BasePath/entities/${ModuleName}Entity.java" -Encoding ASCII

# Controller
@"
package $PackageName.$ModuleName.controllers;

import $PackageName.$ModuleNameLowerCase.dto.request.${ModuleName}RequestDTO;
import $PackageName.$ModuleNameLowerCase.dto.response.${ModuleName}ResponseDTO;
import $PackageName.$ModuleNameLowerCase.service.${ModuleName}Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/api/$ModuleName")
@Tag(name = "Undefinited")
public class ${ModuleName}Controller {

    private final ${ModuleName}Service service;


    public ${ModuleName}Controller(${ModuleName}Service service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<${ModuleName}ResponseDTO> create(@Valid @RequestBody ${ModuleName}RequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<${ModuleName}ResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<${ModuleName}ResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<${ModuleName}ResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<${ModuleName}ResponseDTO> update(@PathVariable UUID id, @RequestBody ${ModuleName}RequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<${ModuleName}ResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
"@ | Out-File -FilePath "$BasePath/controllers/${ModuleName}Controller.java" -Encoding ASCII

# DTO (Request)
@"
package $PackageName.$ModuleNameLowerCase.dto.request;

import lombok.*;

@Value
public class ${ModuleName}RequestDTO {
    // Agrega campos aquí
}
"@ | Out-File -FilePath "$BasePath/dto/request/${ModuleName}RequestDTO.java" -Encoding ASCII

# DTO (Response)
@"
package $PackageName.$ModuleNameLowerCase.dto.response;

import lombok.*;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ${ModuleName}ResponseDTO {
    private UUID id;
    // Agrega más campos aquí
}
"@ | Out-File -FilePath "$BasePath/dto/response/${ModuleName}ResponseDTO.java" -Encoding ASCII

# Repository
@"
package $PackageName.$ModuleNameLowerCase.repositories;

import $PackageName.$ModuleNameLowerCase.entities.${ModuleName}Entity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface ${ModuleName}Repository extends CrudRepository<${ModuleName}Entity, UUID>, JpaSpecificationExecutor<${ModuleName}Entity>{
    // Agrega métodos personalizados aquí
}
"@ | Out-File -FilePath "$BasePath/repositories/${ModuleName}Repository.java" -Encoding ASCII

# Service
@"
package $PackageName.$ModuleNameLowerCase.service;

import $PackageName.$ModuleNameLowerCase.dto.request.${ModuleName}RequestDTO;
import $PackageName.$ModuleNameLowerCase.dto.response.${ModuleName}ResponseDTO;
import $PackageName.$ModuleNameLowerCase.repositories.${ModuleName}Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import ${PackageName}.${ModuleName}.entities.${ModuleName}Entity;
import ${PackageName}.${ModuleName}.util.${ModuleName}Specification;
import ${PackageName}.${ModuleName}.service.interfaces.I${ModuleName}Service;

import java.util.*;

@Service
public class ${ModuleName}Service implements I${ModuleName}Service {

    private final ${ModuleName}Repository repository;

    public ${ModuleName}Service(${ModuleName}Repository repository) {
        this.repository = repository;
    }


    @Override
    public ${ModuleName}ResponseDTO create(${ModuleName}RequestDTO dto) {
        return null;
    }

    @Override
    public ${ModuleName}ResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public ${ModuleName}ResponseDTO update(UUID id, ${ModuleName}RequestDTO dto) {
        return null;
    }

    @Override
    public ${ModuleName}ResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<${ModuleName}ResponseDTO> getAll(Map<String, String> params) {
        List<${ModuleName}Entity> list = repository.findAll(filterWithParameters(params));
        return null;
    }

    public Specification<${ModuleName}Entity> filterWithParameters(Map<String, String> params) {
        return new ${ModuleName}Specification().getSpecificationByFilters(params);
    }
}
"@ | Out-File -FilePath "$BasePath/service/${ModuleName}Service.java" -Encoding ASCII

# Service Interface
@"
package $PackageName.$ModuleNameLowerCase.service.interfaces;

import $PackageName.$ModuleNameLowerCase.dto.request.${ModuleName}RequestDTO;
import $PackageName.$ModuleNameLowerCase.dto.response.${ModuleName}ResponseDTO;

import java.util.*;

public interface I${ModuleName}Service {

    ${ModuleName}ResponseDTO create(${ModuleName}RequestDTO dto);

    List<${ModuleName}ResponseDTO> getAll(Map<String, String> params);

    ${ModuleName}ResponseDTO getById(UUID id);

    ${ModuleName}ResponseDTO update(UUID id ,${ModuleName}RequestDTO dto);

    ${ModuleName}ResponseDTO delete(UUID dto);
}
"@ | Out-File -FilePath "$BasePath/service/interfaces/I${ModuleName}Service.java" -Encoding ASCII

# Util

@"
package $PackageName.${ModuleName}ValueMapper.util;


import $PackageName.$ModuleNameLowerCase.request.*;
import $PackageName.$ModuleNameLowerCase.response.*;
import $PackageName.$ModuleNameLowerCase.entities.*;
import java.util.List;

public class ${ModuleName}ValueMapper {

        public static ${ModuleName}Entity ${ModuleName}RequestToEntity(${ModuleName}RequestDTO dto) {
        return ${ModuleName}Entity.builder()
                .build();

    }

    public static ${ModuleName}ResponseDTO ${ModuleName}EntityToResponse(${ModuleName}Entity dto){
        return  ${ModuleName}ResponseDTO.builder()
                .build();
    }


}
"@ | Out-File -FilePath "$BasePath/util/${ModuleName}ValueMapper.java" -Encoding ASCII


# Specification

@"
package $PackageName.${ModuleName}.util;


import $PackageName.common.exeptions.FilterNotFound;
import $PackageName.${ModuleName}.entities.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.Map.Entry;



public class ${ModuleName}Specification implements Specification<${ModuleName}Entity> {

    @SuppressWarnings("null")
    @Nullable
    @Override
    public Predicate toPredicate(Root<${ModuleName}Entity> root , @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        return null;
    }

     public Specification<${ModuleName}Entity> getSpecificationByFilters(Map<String,String> params){
        Specification<${ModuleName}Entity> specification = Specification.where(this.filterByDeleted("false"));

        if (params == null || params.isEmpty()) {
            return specification;
        }

        for(Entry<String, String> entry : params.entrySet()){
            switch (entry.getKey()){
                case "id":
                    specification = specification.and(this.filterById(entry.getValue()));
                    break;
                case "nombre":
                    specification = specification.and(this.filterByName(entry.getValue()));
                    break;
                case "enabled":
                    specification = specification.and(this.filterByEnabled(entry.getValue()));
                    break;

                default:
                    throw new FilterNotFound("Invalid filter: " + entry.getKey());

            }
        }
        return specification;
    }

    private Specification<${ModuleName}Entity> filterById(String id){
        return (root,query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<${ModuleName}Entity> filterByName(String nombre){
        return (root,query, cb) -> cb.like(root.get("nombre"), nombre);
    }

    private Specification<${ModuleName}Entity> filterByEnabled(String enabled){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("enabled"), Boolean.parseBoolean(enabled));
    }

    private Specification<${ModuleName}Entity> filterByDeleted(String deleted){
        return (root,query, cb) -> cb.equal(root.<Boolean>get("deleted"), Boolean.parseBoolean(deleted));
    }


}
"@ | Out-File -FilePath "$BasePath/util/${ModuleName}Specification.java" -Encoding ASCII

# Mensaje de éxito
Write-Host "Estructura de paquetes y clases creada para el módulo $ModuleName."
Write-Host "Paquete base: $PackageName.$ModuleNameLowerCase"