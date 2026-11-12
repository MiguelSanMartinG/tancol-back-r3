package com.main.tancol.logistica.util;


public class BitacoraPartidaVentasValueMapper {

//    public static BitacoraPartidaVentasResponseDTO BitacoraPartidaVentasEntityToResponse(BitacoraPartidaVentasEntity dto){
//        return  BitacoraPartidaVentasResponseDTO.builder()
//                .cantidadLlegada(dto.getCantidadLlegada())
//                .cantidadSalida(dto.getCantidadSalida())
//                .conductor(ObjectUtils.isNotEmpty(dto.getConductor())? EmpleadosValueMapper.EmpleadosEntityToResponse(dto.getConductor()): null)
//                .huecos(dto.getHuecos())
//                .rotos(dto.getRotos())
//                .vendidos(dto.getVendidos())
//                .idPartida(dto.getIdPartidaVenta())
//                .comisionesEmpleados(ObjectUtils.isNotEmpty(dto.getComisionesEmpleados())?  dto.getComisionesEmpleados().stream().map(VentasEmpleadoValueMapper::VentasEmpleadoEntityToResponse).collect(Collectors.toSet()) : null)
//                .productoComercial(ObjectUtils.isNotEmpty(dto.getProductoComercial()) ? ProductoComercialValueMapper.ProductoComercialEntityToResponse(dto.getProductoComercial()): null)
//                .ruta(ObjectUtils.isNotEmpty(dto.getRuta())? RutasValueMapper.RutasEntityToResponse(dto.getRuta()) : null)
//                .vehiculo(ObjectUtils.isNotEmpty(dto.getVehiculo())? VehiculosValueMapper.VehiculosEntityToResponse(dto.getVehiculo()) : null)
//                .estadoVentaRuta(dto.getEstadoVentaRuta())
//                .idImagenFirma(dto.getIdImagenFirma())
//                .comentario(dto.getComentario())
//                .rezago(ObjectUtils.isNotEmpty(dto.getProductosNoConsignados())?
//                        dto.getProductosNoConsignados().stream().map(ProductosNoConsignadosValueMapper::ProductosNoConsignadosEntityToResponse).collect(Collectors.toSet()) : null)
//                .build();
//    }
//
//
//    public static BitacoraPartidaVentasEntity BitacoraPartidaVentasRequestToEntity(BitacoraPartidaVentasRequestDTO x,
//                                                                                   RutasEntity ruta, EmpleadosEntity conductor, EmpleadosEntity ayudante, VehiculosEntity vehiculo, ProductoComercialEntity producto) {
//        return BitacoraPartidaVentasEntity.builder()
//                .ruta(ruta)
//                .conductor(conductor)
//                .vehiculo(vehiculo)
//                .productoComercial(producto)
//                .cantidadLlegada(x.getCantidadLlegada())
//                .cantidadSalida(x.getCantidadSalida())
//                .vendidos(x.getCantidadSalida().subtract(x.getCantidadLlegada()))
//                .huecos(x.getHuecos())
//                .build();
//    }
}
