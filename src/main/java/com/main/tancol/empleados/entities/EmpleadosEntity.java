package com.main.tancol.empleados.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.permisos.entities.ModulePermissionAuthority;
import com.main.tancol.permisos.entities.ProfileEntity;
import com.main.tancol.permisos.entities.RoleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "empleados")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmpleadosEntity extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    private Long idEmpleado;

    @Column(name = "user_name", unique = true)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String nombreUsuario;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombre;

    @Column(name = "primer_apellido")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String primerApellido;

    @Column(name = "segundo_apellido")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String segundoApellido;

    @ManyToOne
    @JoinColumn(name = "tipos_empleados_list_id")
    private TiposEmpleadosEntity tiposEmpleados;

    @Column(name = "correo")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String correo;

    @Column(name = "telefono")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaNacimiento;

    @Column(name = "contra")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String contra;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "id_departamento_id")
    private DepartamentoEntity departamento;

    @ManyToMany(mappedBy = "empleadosActuales", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<RutasEntity> rutasAsignadas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "empleado")
    private Set<VentasEmpleadoEntity> comisiones = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (ProfileEntity perm : this.role.getRoleProfile()) {
            if (perm.isCanRead()) {
                authorities.add(new ModulePermissionAuthority(perm.getModule().getName(), "READ"));
            }
            if (perm.isCanWrite()) {
                authorities.add(new ModulePermissionAuthority(perm.getModule().getName(), "WRITE"));
            }
            if (perm.isCanDelete()) {
                authorities.add(new ModulePermissionAuthority(perm.getModule().getName(), "DELETE"));
            }
            if(perm.isCanUpdate()){
                authorities.add(new ModulePermissionAuthority(perm.getModule().getName(), "UPDATE"));
            }
        }
        return authorities;
//        return this.getRole().getPermissions().stream()
//                .map(p -> new SimpleGrantedAuthority(p.getModule().name() + "_" + p.getRole().getName()))
//                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return this.contra;
    }

    @Override
    public String getUsername() {
        return this.getNombreUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

//    @Override
//    public boolean isEnabled() {
//        return this.isEnabled();
//    }

    // Agrega m??s campos aqu??
}
