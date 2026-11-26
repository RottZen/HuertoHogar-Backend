package cl.huertohogar.producto.config;

import cl.huertohogar.producto.model.Producto;
import cl.huertohogar.producto.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Carga el catalogo inicial (el mismo de datos.js) si la tabla esta vacia. */
@Component
public class DataLoader implements CommandLineRunner {

    private final ProductoRepository repo;

    public DataLoader(ProductoRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            return;
        }
        repo.saveAll(List.of(
            nuevo("FR001", "Manzanas Fuji", "frutas", 1200, 150, "kg", "/assets/img/manzana-fuji.jpg",
                "Valle del Maule",
                "Manzanas Fuji crujientes y dulces, cultivadas en el Valle del Maule. Perfectas para meriendas saludables o como ingrediente en postres."),
            nuevo("FR002", "Naranjas Valencia", "frutas", 1000, 200, "kg", "/assets/img/naranjas-valencia.jpg",
                "Chile",
                "Jugosas y ricas en vitamina C, ideales para zumos frescos y refrescantes."),
            nuevo("FR003", "Plátanos Cavendish", "frutas", 800, 250, "kg", "/assets/img/platanos-cavendish.jpg",
                "Chile",
                "Plátanos maduros y dulces, perfectos para el desayuno o como snack energético."),
            nuevo("VR001", "Zanahorias Orgánicas", "verduras", 900, 100, "kg", "/assets/img/zanahorias-organicas.jpg",
                "Región de O'Higgins",
                "Zanahorias crujientes cultivadas sin pesticidas. Excelente fuente de vitamina A y fibra."),
            nuevo("VR002", "Espinacas Frescas", "verduras", 700, 80, "bolsa 500g", "/assets/img/espinacas-frescas.jpg",
                "Chile",
                "Espinacas frescas y nutritivas, perfectas para ensaladas y batidos verdes."),
            nuevo("VR003", "Pimientos Tricolores", "verduras", 1500, 120, "kg", "/assets/img/pimientos-tricolores.jpg",
                "Chile",
                "Pimientos rojos, amarillos y verdes, ideales para salteados y platos coloridos."),
            nuevo("PO001", "Miel Orgánica", "organicos", 5000, 50, "frasco 500g", "/assets/img/miel-organica.jpg",
                "Región de La Araucanía",
                "Miel pura y orgánica producida por apicultores locales. Rica en antioxidantes y con un sabor inigualable."),
            nuevo("PO003", "Quinua Orgánica", "organicos", 3200, 35, "bolsa 1kg", "/assets/img/quinua-organica.jpg",
                "Chile",
                "Quinua orgánica, alta en proteínas y libre de gluten, ideal para una alimentación saludable."),
            nuevo("PL001", "Leche Entera", "lacteos", 1100, 90, "litro", "/assets/img/leche-entera.jpg",
                "Chile",
                "Leche entera proveniente de granjas locales, rica en calcio y nutrientes esenciales.")
        ));
    }

    private Producto nuevo(String codigo, String name, String categoria, int price, int stock,
                           String unidad, String image, String origen, String descripcion) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setName(name);
        p.setCategoria(categoria);
        p.setPrice(price);
        p.setStock(stock);
        p.setUnidad(unidad);
        p.setImage(image);
        p.setOrigen(origen);
        p.setDescripcion(descripcion);
        return p;
    }
}
