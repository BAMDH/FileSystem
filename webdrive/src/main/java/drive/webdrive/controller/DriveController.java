/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package drive.webdrive.controller;
import drive.webdrive.Services.DriveService;
import drive.webdrive.modelos.UsuarioData;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Autowired
    private DriveService service;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        String usuario = (String) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        UsuarioData data = service.cargarUsuario(usuario);
        if (data == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("rutaActual", data.getRutaActual());
        model.addAttribute("carpetas", data.getRaiz().getSubdirectorios());
        model.addAttribute("archivos", data.getRaiz().getArchivos());
        return "drive";
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
        service.crearDirectorio(data, "nuevaCarpeta");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
