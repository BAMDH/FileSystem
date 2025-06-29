/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package drive.webdrive.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import drive.webdrive.Services.DriveService;
import drive.webdrive.modelos.Archivo;
import drive.webdrive.modelos.Directorio;
import drive.webdrive.modelos.UsuarioData;

import jakarta.servlet.http.HttpSession;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/*
public class DriveController{
    @Autowired
    private DriveService driveService;
    @GetMapping("/")
    public String mostrarHome() {
        return "home"; // SIN .html
        }
     

      @GetMapping("/drive")
      public String mostrarDrive(Model model) {
        // Simulaciones para ejemplo, luego se reemplazan por datos reales

        model.addAttribute("usuario", "daniel123");
        model.addAttribute("rutaActual", "/");
        model.addAttribute("carpetas", List.of(
                Map.of("nombre", "documentos"),
                Map.of("nombre", "tareas")
        ));
        model.addAttribute("archivos", List.of(
                Map.of("nombre", "info.txt"),
                Map.of("nombre", "proyecto.java")
        ));

        return "home"; // Renderiza templates/home.html
    }
      
    @GetMapping("/crear-carpeta")
    public String crearCarpeta(HttpSession session) {
        String usuario = (String) session.getAttribute("usuario");
        driveService.crearDirectorio(usuario, "nuevaCarpeta");
        return "redirect:/";
    }
      
      
      
}
   */


    

@Controller
public class DriveController {
private final String SERVER_URL = "http://localhost:3000";
    @Autowired
    private DriveService service;
@GetMapping("/")
public String home(HttpSession session, Model model) {
    // 1. Verificar sesión
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";

    // 2. Obtener datos del usuario
    UsuarioData data = service.cargarUsuario(usuario);
    if (data == null) return "redirect:/login";

    // 3. Inicializar sesión
    session.setAttribute("rutaActual", "/root");
    session.setAttribute("directorioActual", data.getRaiz());

    // 4. Preparar modelo
    model.addAttribute("usuario", usuario);
    model.addAttribute("rutaActual", "/root");
    model.addAttribute("carpetas", data.getRaiz().getSubdirectories());
    model.addAttribute("archivos", data.getRaiz().getFiles());
    
    return "home";
}
   @GetMapping("/ver")
public String verArchivo(
    @RequestParam("archivo") String nombreArchivo,
    HttpSession session,
    Model model) {
    
    // 1. Verificar sesión
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";

    // 2. Obtener directorio actual desde sesión
    Directorio directorioActual = (Directorio) session.getAttribute("directorioActual");
    if (directorioActual == null) {
        UsuarioData data = service.cargarUsuario(usuario);
        directorioActual = data.getRaiz();
    }

    // 3. Buscar archivo SOLO en el directorio actual
    Archivo archivo = directorioActual.getFiles().stream()
        .filter(a -> (a.getName() + "." + a.getExtension()).equals(nombreArchivo))
        .findFirst()
        .orElse(null);

    if (archivo == null) {
        return "redirect:/?error=Archivo+no+encontrado";
    }

    // 4. Guardar contexto para volver
    String rutaActual = (String) session.getAttribute("rutaActual");
    session.setAttribute("rutaParaVolver", rutaActual);
    session.setAttribute("directorioParaVolver", directorioActual);

    // 5. Preparar modelo
    model.addAttribute("archivo", archivo);
    return "ver-archivo";
}
@GetMapping("/volver-desde-archivo")
public String volverDesdeArchivo(HttpSession session) {
    // 1. Recuperar contexto guardado
    String rutaParaVolver = (String) session.getAttribute("rutaParaVolver");
    Directorio directorioParaVolver = (Directorio) session.getAttribute("directorioParaVolver");
    
    // 2. Validaciones
    if (rutaParaVolver == null || directorioParaVolver == null) {
        return "redirect:/";
    }

    // 3. Restaurar estado anterior
    session.setAttribute("rutaActual", rutaParaVolver);
    session.setAttribute("directorioActual", directorioParaVolver);
    
    // 4. Redirigir al home con el estado correcto
    return "redirect:/";
}@GetMapping("/navegar")
public String navegarCarpeta(
    @RequestParam("carpeta") String nombreCarpeta,
    HttpSession session,
    Model model) {
    
    // 1. Verificación de sesión y autenticación
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) {
        return "redirect:/login";
    }

    // 2. Cargar datos del usuario desde el servicio
    UsuarioData data = service.cargarUsuario(usuario);
    if (data == null) {
        return "redirect:/login";
    }

    // 3. Manejo de la ruta actual en sesión
    String rutaActual = (String) session.getAttribute("rutaActual");
    Directorio directorioActual = (Directorio) session.getAttribute("directorioActual");
    
    // Inicialización si es la primera vez
    if (rutaActual == null || directorioActual == null) {
        rutaActual = "/root";
        directorioActual = data.getRaiz();
        session.setAttribute("rutaActual", rutaActual);
        session.setAttribute("directorioActual", directorioActual);
    }

    // 4. Construcción de la nueva ruta con manejo de casos especiales
    String nuevaRuta;
    if (rutaActual.equals("/root")) {
        nuevaRuta = "/root/" + nombreCarpeta;
    } else {
        nuevaRuta = rutaActual + "/" + nombreCarpeta;
    }

    // 5. Búsqueda del directorio objetivo en la estructura
    Directorio directorioObjetivo = buscarDirectorioPorRuta(data.getRaiz(), nuevaRuta);
    
    // Manejo de error si no se encuentra la carpeta
    if (directorioObjetivo == null) {
        return "redirect:/?error=Carpeta+no+encontrada";
    }

    // 6. Guardar el estado actual como "previo" para poder volver
    session.setAttribute("rutaPrevia", rutaActual);
    session.setAttribute("directorioPrevio", directorioActual);
    
    // 7. Actualizar el estado de navegación en la sesión
    session.setAttribute("rutaActual", nuevaRuta);
    session.setAttribute("directorioActual", directorioObjetivo);

    // 8. Preparación del modelo para la vista Thymeleaf
    model.addAttribute("usuario", usuario);
    model.addAttribute("rutaActual", nuevaRuta);
    model.addAttribute("carpetas", directorioObjetivo.getSubdirectories());
    model.addAttribute("archivos", directorioObjetivo.getFiles());
    
    return "home";
}

/**
 * Método auxiliar para buscar un directorio por su ruta completa
 */
private Directorio buscarDirectorioPorRuta(Directorio raiz, String rutaCompleta) {
    // Eliminar /root inicial si existe y dividir la ruta
    String[] partesRuta = rutaCompleta.replaceFirst("^/root", "").split("/");
    
    Directorio directorioActual = raiz;
    
    for (String parte : partesRuta) {
        if (!parte.isEmpty()) {
            Optional<Directorio> subdirectorio = directorioActual.getSubdirectories()
                .stream()
                .filter(dir -> dir.getName().equals(parte))
                .findFirst();
            
            if (subdirectorio.isPresent()) {
                directorioActual = subdirectorio.get();
            } else {
                return null; // Subdirectorio no encontrado
            }
        }
    }
    
    return directorioActual;
}



@GetMapping("/volver")
public String volverAtras(HttpSession session, Model model) {
    // 1. Obtener ruta actual
    String rutaActual = (String) session.getAttribute("rutaActual");
    if (rutaActual == null || rutaActual.equals("/root")) {
        return "redirect:/";
    }

    // 2. Calcular nueva ruta
    String nuevaRuta = rutaActual.substring(0, rutaActual.lastIndexOf('/'));

    // 3. Obtener datos del usuario
    String usuario = (String) session.getAttribute("usuario");
    UsuarioData data = service.cargarUsuario(usuario);

    // 4. Buscar directorio
    Directorio directorioObjetivo = buscarDirectorioPorRuta(data.getRaiz(), nuevaRuta);
    
    if (directorioObjetivo == null) {
        return "redirect:/";
    }

    // 5. Actualizar sesión
    session.setAttribute("rutaActual", nuevaRuta);
    session.setAttribute("directorioActual", directorioObjetivo);

    // 6. Preparar modelo
    model.addAttribute("usuario", usuario);
    model.addAttribute("rutaActual", nuevaRuta);
    model.addAttribute("carpetas", directorioObjetivo.getSubdirectories());
    model.addAttribute("archivos", directorioObjetivo.getFiles());
    
    return "home";
}
private Directorio buscarCarpetaPorRuta(Directorio raiz, String ruta) {
    System.out.println(ruta);
    String[] partes = ruta.split("/");
    Directorio actual = raiz;
    
    for (String parte : partes) {
        if (!parte.isEmpty() && !parte.equals("root")) {
            actual = actual.getSubdirectories().stream()
                .filter(d -> d.getName().equals(parte))
                .findFirst()
                .orElse(null);
            
            if (actual == null) return null;
        }
    }
    return actual;
}
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam String nombre, HttpSession session, Model model) {
        UsuarioData data = service.cargarUsuario(nombre);
        System.out.println("Usuario data " + data );
        if (data == null) {
            model.addAttribute("error", "Usuario no encontrado en el servidor.");
            return "login";
        }

        session.setAttribute("usuario", nombre);
        return "redirect:/";
    }

    @GetMapping("/crear-carpeta")
    public String crearCarpeta(HttpSession session) {
        String usuario = (String) session.getAttribute("usuario");
        UsuarioData data = service.cargarUsuario(usuario);
       // service.crearDirectorio(data, "nuevaCarpeta");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    
    
    
   @PostMapping("/guardar-archivo")
public ResponseEntity<String> guardarArchivoJson(
    @RequestBody Map<String, String> datos,
    HttpSession session
) {
    String usuario = (String) session.getAttribute("usuario");
    if (usuario == null) return ResponseEntity.status(401).body("No autenticado");

    String nombreArchivo = datos.get("nombreArchivo");
    String contenido = datos.get("contenido");
    String rutaActual = (String) session.getAttribute("rutaActual");  // <-- obtener ruta actual

    try {
        // Agregamos la ruta actual al JSON enviado
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(Map.of(
            "usuario", usuario,
            "archivo", nombreArchivo,
            "contenido", contenido,
            "rutaActual", rutaActual != null ? rutaActual : "/root"  // valor por defecto si es null
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:3000/api/guardar-archivo",
            request,
            String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error al conectar: " + e.getMessage());
    }
}

    
}


