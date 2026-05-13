package com.example.demo.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private List<String> products = new ArrayList<>(List.of("Laptop", "Mouse", "Keyboard"));

    // GET /api/products → 200 OK con lista
    @GetMapping
    public ResponseEntity<List<String>> findAll() {
        return ResponseEntity.ok(products);
    }

    // GET /api/products/{id} → 200 OK se trovato, 404 se non trovato
    @GetMapping("/{id}")
    public ResponseEntity<String> findById(@PathVariable int id) {
        if (id < 0 || id >= products.size()) {
            // Restituisce 404 Not Found senza body
            return ResponseEntity.notFound().build();
        }
        // Restituisce 200 OK con il prodotto trovato
        return ResponseEntity.ok(products.get(id));
    }

    // POST /api/products → 201 Created con la nuova risorsa
    @PostMapping
    public ResponseEntity<String> create(@RequestBody String newProduct) {
        products.add(newProduct);
        // Restituisce 201 Created con il prodotto appena creato
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    // DELETE /api/products/{id} → 204 No Content se eliminato, 404 se non trovato
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (id < 0 || id >= products.size()) {
            return ResponseEntity.notFound().build();
        }
        products.remove(id);
        // Restituisce 204 No Content (operazione riuscita, nessun body)
        return ResponseEntity.noContent().build();
    }
}
