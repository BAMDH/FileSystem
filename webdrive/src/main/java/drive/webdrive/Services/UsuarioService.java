/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package drive.webdrive.Services;

/**
 *
 * @author drayo
 */
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.HashMap;
import java.util.Map;
import drive.webdrive.modelos.UsuarioData;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UsuarioService {
        public UsuarioData autenticarYObtenerDatos(String username, String password) {
        String url = "http://localhost:3000/login"; // URL del servidor externo
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<UsuarioData>  response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
               UsuarioData.class
        );
        System.out.print("response " + response.getBody() + "\n");
        //return response;
        return response.getBody();
    }
    
    
    
}
