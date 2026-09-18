package com.evaluacion_1.ev_1.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "libros")
public class Libro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String autor;
    private String isbn;
    private boolean disponible = true;
}