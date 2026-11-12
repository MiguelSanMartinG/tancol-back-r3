package com.main.tancol.producto;

/**
 * Interruptor temporal de la jerarquía de categorías de producto básico.
 * Mientras regrese false, no se crean subcategorías ni se recorren en consultas.
 */
public final class SubcategoriaProductoBasico {

    private SubcategoriaProductoBasico() {
    }

    public static boolean habilitada() {
        return false;
    }
}
