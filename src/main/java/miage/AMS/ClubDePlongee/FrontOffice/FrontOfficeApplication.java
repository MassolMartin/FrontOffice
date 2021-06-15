package miage.AMS.ClubDePlongee.FrontOffice;

import miage.AMS.ClubDePlongee.FrontOffice.repositories.CoursMembreRepository;
import miage.AMS.ClubDePlongee.FrontOffice.repositories.CoursMembreRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class FrontOfficeApplication {

    // URL des micro-services de gestion des membres
    //public static final String MEMBRES_SERVICE_URL = "http://localhost:10000";
    public static final String MEMBRES_SERVICE_URL = "http://GESTIONMEMBRES";
    // URL des micro-services de gestion des cours
    //public static final String COURS_SERVICE_URL = "http://localhost:20000";
    public static final String COURS_SERVICE_URL = "http://GESTIONCOURS";
    // URL de l'API OpenData de Toulouse métropole pour la gestion des lieux
    public static final String LIEU_SERVICE_URL = "https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=piscines&q=&rows=-1";

    public static void main(String[] args) {
            SpringApplication.run(FrontOfficeApplication.class, args);
    }

    /**
     * Factory de bean
     * @return 
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() { return new RestTemplate(); }


    @Bean
    public CoursMembreRepository coursRepository() {
        return new CoursMembreRepositoryImpl(MEMBRES_SERVICE_URL, COURS_SERVICE_URL, LIEU_SERVICE_URL);
    }   
  
}
