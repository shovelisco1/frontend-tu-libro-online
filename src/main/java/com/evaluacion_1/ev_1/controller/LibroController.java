package com.evaluacion_1.ev_1.controller;
import com.evaluacion_1.ev_1.service.LibroEventProducer;
import com.evaluacion_1.ev_1.model.Libro;
import com.evaluacion_1.ev_1.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "*") 
public class LibroController {

    @Autowired
    private LibroEventProducer libroEventProducer;

    @Autowired
    private LibroRepository libroRepository;

    
    @GetMapping
    public List<Libro> listarTodos() {
        return libroRepository.findAll();
    }

    
    @PostMapping
    public Libro agregarLibro(@RequestBody Libro libro) {
        
        libro.setDisponible(true);
        return libroRepository.save(libro);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        if (libroRepository.existsById(id)) {
            libroRepository.deleteById(id);
            return ResponseEntity.ok().build(); 
        }
        return ResponseEntity.notFound().build(); 
    }

    
    @PutMapping("/{id}/prestar")
    public ResponseEntity<Libro> prestarLibro(@PathVariable Long id) {
        Optional<Libro> libroOpt = libroRepository.findById(id);

        if (libroOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Libro libro = libroOpt.get();

        if (!libro.isDisponible()) {
            return ResponseEntity.badRequest().build();
        }

        libro.setDisponible(false);
        Libro guardado = libroRepository.save(libro);

        libroEventProducer.publicarPrestamo(guardado);

        return ResponseEntity.ok(guardado);
    }

    
    @PutMapping("/{id}/devolver")
    public ResponseEntity<Libro> devolverLibro(@PathVariable Long id) {
        Optional<Libro> libroOpt = libroRepository.findById(id);
        
        if (libroOpt.isPresent()) {
            Libro libro = libroOpt.get();
            if (!libro.isDisponible()) { 
                libro.setDisponible(true); 
                return ResponseEntity.ok(libroRepository.save(libro));
            } else {
                return ResponseEntity.badRequest().build(); 
            }
        }
        return ResponseEntity.notFound().build(); 
    }
}