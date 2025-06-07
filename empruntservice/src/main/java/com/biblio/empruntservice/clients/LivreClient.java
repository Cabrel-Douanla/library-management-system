 package com.biblio.empruntservice.clients;


// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;

// // import java.util.Arrays;
// // import java.util.List;
// // import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import com.biblio.empruntservice.dto.ExemplaireDTO;

// //@Component
// // public class LivreClient {

// //     @Autowired
// //     private RestTemplate restTemplate;
    
// //     // Appelle l'API du service Livre pour savoir combien d'exemplaires sont disponibles
// //     public int getNombreExemplairesDisponibles(Long livreId) {
// //         String url = "http://localhost:8082/api/livres/" + livreId + "/disponibles";
// //         return restTemplate.getForObject(url, Integer.class);
// //     }

// //     // Récupère les exemplaires disponibles d’un livre
// // public List<Exemplaire> getExemplairesDisponibles(Long livreId) {
// //     String url = "http://localhost:8081/api/livres/" + livreId + "/exemplaires-disponibles";
// //     ResponseEntity<Exemplaire[]> response = restTemplate.getForEntity(url, Exemplaire[].class);
// //     return Arrays.asList(response.getBody());
// // }

// // // Réserve un exemplaire (le marque comme non disponible)
// // public void reserverExemplaire(Long exemplaireId) {
// //     String url = "http://localhost:8081/api/exemplaires/" + exemplaireId + "/reserver";
// //     restTemplate.put(url, null);
// // }




// @Component
// public class LivreClient {
    

//     private final RestTemplate restTemplate;

//     public LivreClient(RestTemplate restTemplate) {
//         this.restTemplate = restTemplate;
//     }

//     public ExemplaireDTO getExemplaireDisponible(Long livreId) {
//         String url = "http://localhost:8081/api/livres/" + livreId + "/exemplaire-disponible";
//         ResponseEntity<ExemplaireDTO> response = restTemplate.getForEntity(url, ExemplaireDTO.class);
//         return response.getBody();
//     }

//     public void reserverExemplaire(Long exemplaireId) {
//         String url = "http://localhost:8081/api/exemplaires/" + exemplaireId + "/reserver";
//         restTemplate.put(url, null);
//     }
// }

//package com.biblio.empruntservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.biblio.empruntservice.dto.ExemplaireDTO;

@Service
public class LivreClient {

    @Autowired
    private RestTemplate restTemplate;

    private final String LIVRE_SERVICE_BASE_URL = "http://localhost:8081/api"; // port du service Livre

    public ExemplaireDTO getExemplaireDisponible(Long livreId) {
        return restTemplate.getForObject(LIVRE_SERVICE_BASE_URL + "/livres/" + livreId + "/exemplaire-disponible", ExemplaireDTO.class);
    }

    public void reserverExemplaire(Long exemplaireId) {
        restTemplate.put(LIVRE_SERVICE_BASE_URL + "/exemplaires/" + exemplaireId + "/reserver", null);
    }

    public void libererExemplaire(Long exemplaireId) {
        restTemplate.put(LIVRE_SERVICE_BASE_URL + "/exemplaires/" + exemplaireId + "/liberer", null);
    }
    //System.out.println(...);
}



