//package com.main.tancol.logistica.entities;
//
//import com.main.tancol.empleados.entities.EmpleadosEntity;
//import com.main.tancol.logistica.entities.bitacora.BitacoraVentaRutaEntity;
//import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
//import com.main.tancol.logistica.entities.rutas.RutasEntity;
//import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
//import com.main.tancol.logistica.enums.EstadoVentaRuta;
//import com.main.tancol.producto.entities.ProductosFueraEspecificacion;
//import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.JdbcTypeCode;
//import org.hibernate.type.SqlTypes;
//
//import java.math.BigDecimal;
//import java.util.LinkedHashSet;
//import java.util.Set;
//import java.util.UUID;
//
//
////@Entity
////@Table(name = "bitacora_rutas_partidas")
////@Builder
////@Getter
////@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class BitacoraPartidaVentasEntity{
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @JdbcTypeCode(SqlTypes.UUID)
//    private UUID idPartidaVenta;
//
//    @Column(name = "cantidad_salida", precision = 19, scale = 2)
//    private BigDecimal cantidadSalida;
//
//    @Column(name = "cantidad_llegada", precision = 19, scale = 2)
//    private BigDecimal cantidadLlegada;
//
//    @Column(name = "vendidos" ,precision = 19, scale = 2)
//    private BigDecimal vendidos;
//
//    @Column(name = "huecos", precision = 19, scale = 2)
//    private BigDecimal huecos;
//
//    @Column(name = "rotos", precision = 19, scale = 2)
//    private BigDecimal rotos;
//
//    @Column(name = "donados", precision = 19, scale = 2)
//    private BigDecimal donados;
//
//    @Column(name = "id_imagen_firma")
//    private String idImagenFirma;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "estado_venta_ruta")
//    private EstadoVentaRuta estadoVentaRuta;
//
//    private Boolean mandadoALiquidacion;
//
//    // Muchas partidas tienen un@..
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ruta_id")
//    private RutasEntity ruta;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "conductor_id")
//    private EmpleadosEntity conductor;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "vehiculo_id")
//    private VehiculosEntity vehiculo;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "producto_comercial_id")
//    private ProductoComercialEntity productoComercial;
//
//    @ManyToOne
//    @JoinColumn(name = "bitacora_padre_id_bitacora")
//    private BitacoraVentaRutaEntity bitacoraPadre;
//
//    @OneToMany(mappedBy = "partidaBitacora", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<ProductosFueraEspecificacion> productosNoConsignados = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<VentasEmpleadoEntity> comisionesEmpleados = new LinkedHashSet<>();
//
//    @Column(name = "comentario", length = 1040)
//    private String comentario;
//    // -------------------------------------------------------------------- //
//
////    public void agregarPartida(VentasEmpleadoEntity comision){
////        comisionesEmpleados.add(comision);
////        comision.setPartida(this);
////    }
////
////    public void removerPartida(VentasEmpleadoEntity comision){
////        comisionesEmpleados.remove(comision);
////        comision.setPartida(null);
////    }
//
//
//
//    // -------------------------------------------------------------------//
//
////    public void agregarRemanente(ProductosFueraEspecificacion remanente){
////        productosNoConsignados.add(remanente);
////        remanente.setPartidaBitacora(this);
////    }
////
////    public void removerRemanente(ProductosFueraEspecificacion remanente){
////        productosNoConsignados.remove(remanente);
////        remanente.setPartidaBitacora(null);
////    }
////
////    public void limpiarRemanentes(){
////        for(ProductosFueraEspecificacion remanente : productosNoConsignados){
////            remanente.setPartidaBitacora(null);
////        }
////        productosNoConsignados.clear();
////
////    }
//
//    // ---------------------------------------------------------------//
//
//
//}
