# HuertoHogar - Backend (Spring Boot, 3 microservicios)

Backend para el frontend React `traiganmen_al_json_optimized`. Los campos del JSON
(`name`, `price`, `image`, `role`...) calzan con lo que ya usa el front.

| Servicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `producto-service` | **8080** | `huertohogar_productos` | Catálogo, CRUD del admin, stock |
| `usuario-service` | 8081 | `huertohogar_usuarios` | Registro, login, perfil, HuertoPuntos |
| `pedido-service` | 8082 | `huertohogar_pedidos` | Confirmar pedido, seguimiento, historial |

`producto-service` usa el 8080 porque `ProductContext.jsx` ya apunta a `http://localhost:8080/api/productos`.

## Requisitos
- JDK 17 o superior
- Maven 3.9+ (o abrir cada carpeta en IntelliJ / VS Code / STS)
- Nada más para probar el catálogo: `producto-service` usa H2 (archivo local `./data`) por defecto.
- MySQL 8 local o AWS RDS **solo si defines `DB_URL`** (usuario-service y pedido-service siguen usando MySQL).

## Configuración (variables de entorno)
Cada servicio lee lo mismo; no hay claves escritas en el código.

| Variable | Default | Ejemplo RDS |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/huertohogar_<servicio>?createDatabaseIfNotExist=true...` | `jdbc:mysql://TU-ENDPOINT.rds.amazonaws.com:3306/huertohogar_productos` |
| `DB_USER` | `root` | `admin` |
| `DB_PASSWORD` | (vacío) | tu clave |
| `CORS_ORIGINS` | `http://localhost:*,http://127.0.0.1:*` | URL del front |
| `PRODUCTO_URL` / `USUARIO_URL` | `http://localhost:8080` / `:8081` | (solo pedido-service) |

En RDS: el Security Group debe permitir el puerto 3306 desde tu IP, y las 3 bases
(`huertohogar_productos`, `huertohogar_usuarios`, `huertohogar_pedidos`) se pueden
crear a mano o con `createDatabaseIfNotExist=true` en la URL.

## Cómo levantarlo
Cada uno en su terminal, en este orden:

```bash
cd producto-service && mvn spring-boot:run
cd usuario-service  && mvn spring-boot:run
cd pedido-service   && mvn spring-boot:run   # necesita a los otros dos arriba
```
Las tablas se crean solas (`ddl-auto=update`) y se cargan datos de prueba la primera vez.

## Datos de prueba
- Productos: los 9 de `datos.js` (FR001...PL001)
- `admin@huertohogar.cl` / `admin123` (role `admin`)
- `cliente@correo.com` / `cliente123` (role `user`, id 2, 250 puntos)
- Pedidos `HH-1001`, `HH-1002`, `HH-1003` (los del seguimiento de demo)

## Endpoints

### producto-service (:8080)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productos?categoria=frutas&buscar=manz` | Lista (filtros opcionales) |
| GET | `/api/productos/{id}` | Detalle |
| POST | `/api/productos` | Crear (409 si el código existe) |
| PUT | `/api/productos/{id}` | Editar (parcial) |
| DELETE | `/api/productos/{id}` | Eliminar (204) |
| PATCH | `/api/productos/{id}/stock` | `{"delta": -2}` descuenta; 409 si no alcanza |

### usuario-service (:8081)
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/auth/register` | `{name,email,password}` → rol `user`, 50 puntos |
| POST | `/api/auth/login` | `{email,password}` → usuario (401 si falla) |
| GET | `/api/usuarios/{id}` | Perfil |
| PUT | `/api/usuarios/{id}` | `{name,phone,address,city}` |
| PATCH | `/api/usuarios/{id}/puntos` | `{"delta": 120}` |

### pedido-service (:8082)
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/pedidos` | `{usuarioId, items:[{productoId, quantity}]}` |
| GET | `/api/pedidos/{codigo}` | Seguimiento (`HH-1001`) |
| GET | `/api/pedidos?usuarioId=2` | Historial del usuario |
| PUT | `/api/pedidos/{codigo}/estado` | `{"estado": 2}` (0 a 3) |

Al crear un pedido, el servidor lee los precios desde producto-service (no confía en
el precio del navegador), descuenta stock y suma el 10 % del total en puntos.

## Pruebas rápidas (curl)
```bash
curl http://localhost:8080/api/productos
curl -X POST http://localhost:8081/api/auth/login -H "Content-Type: application/json" \
     -d '{"email":"cliente@correo.com","password":"cliente123"}'
curl -X POST http://localhost:8082/api/pedidos -H "Content-Type: application/json" \
     -d '{"usuarioId":2,"items":[{"productoId":1,"quantity":2},{"productoId":9,"quantity":1}]}'
curl http://localhost:8082/api/pedidos/HH-1001
```

## Conectar el frontend
- **Productos / Admin**: ya funciona sin cambios (`ProductContext.jsx` usa `/api/productos`).
- **Login y registro** (`AuthContext.jsx`): cambiar el mock por `POST :8081/api/auth/login` y `/register`.
  El objeto que devuelve ya trae `role: 'admin' | 'user'`.
- **Carrito** (`handleConfirm`): `POST :8082/api/pedidos` con `items: cart.map(i => ({ productoId: i.id, quantity: i.quantity }))`.
- **Seguimiento**: reemplazar `pedidosOriginales` por `GET :8082/api/pedidos/{codigo}`.
- **Perfil**: `GET/PUT :8081/api/usuarios/{id}` y `GET :8082/api/pedidos?usuarioId={id}`.

## Limitaciones conocidas
- No hay JWT: los endpoints de escritura (CRUD de productos, cambio de estado) están abiertos;
  el control de admin está solo en el front. Siguiente paso natural: Spring Security + JWT.
- La actualización de stock entre servicios no es una transacción distribuida: si una llamada
  falla se intenta devolver el stock descontado, pero no hay garantía total.
