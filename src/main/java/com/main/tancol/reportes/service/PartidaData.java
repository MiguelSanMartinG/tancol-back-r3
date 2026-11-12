package com.main.tancol.reportes.service;

import java.math.BigDecimal;

/**
 * Clase interna para representar los datos de una partida
 * Puedes moverla a un archivo separado si prefieres
 */
public class PartidaData {
    private String nombre;
    private String cliente;
    private BigDecimal cantidad;
    private BigDecimal precio;
    private BigDecimal importe;
    private String nombre2;
    private String tipos;
    private String comentario;

    // Constructor
    public PartidaData(String nombre, String cliente, BigDecimal cantidad, BigDecimal precio,
                       BigDecimal importe, String nombre2, String tipos, String comentario) {
        this.nombre = nombre;
        this.cliente = cliente;
        this.cantidad = cantidad;
        this.precio = precio;
        this.importe = importe;
        this.nombre2 = nombre2;
        this.tipos = tipos;
        this.comentario = comentario;
    }

    // Getters y Setters (necesarios para JasperReports)
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    public String getNombre2() { return nombre2; }
    public void setNombre2(String nombre2) { this.nombre2 = nombre2; }

    public String getTipos() { return tipos; }
    public void setTipos(String tipos) { this.tipos = tipos; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
