/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miage.AMS.ClubDePlongee.FrontOffice.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import static miage.AMS.ClubDePlongee.FrontOffice.FrontOfficeApplication.LIEU_SERVICE_URL;
import miage.AMS.ClubDePlongee.FrontOffice.rest.FoController;
import miage.AMS.ClubDePlongee.FrontOffice.transientO.Cours;
import miage.AMS.ClubDePlongee.FrontOffice.transientO.LieuPiscine;
import miage.AMS.ClubDePlongee.FrontOffice.transientO.Membre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class CoursMembreRepositoryImpl implements CoursMembreRepository {
    
    Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    @LoadBalanced
    protected RestTemplate  restTemplateCours;
    
    @Autowired
    @LoadBalanced
    protected RestTemplate  restTemplateMembre;
    
    protected String serviceUrlCours;
    
    protected String serviceUrlMembre;
    
    protected String serviceUrlLieu;

    /**
     * Constructeur repo
     * @param serviceUrlMembre
     * @param serviceUrlCours 
     */
    public CoursMembreRepositoryImpl(String serviceUrlMembre, String serviceUrlCours, String serviceUrlLieu) {
        this.serviceUrlCours = serviceUrlCours;
        this.serviceUrlMembre = serviceUrlMembre;
        this.serviceUrlLieu = serviceUrlLieu;
    }

    /**
     * POST nouveau cours
     * @param c
     * @return un nouveau cours
     */
    @Override
    public Cours nouveauCours(Cours c) {
        return restTemplateCours.postForObject(this.serviceUrlCours+"/nouveau",c,Cours.class);
    }
    
    /**
     * GET Récupère un membre via son login
     * @param login
     * @return la liste des enseignants
     */
    @Override
    public Membre getMembreByLogin(String login) {
        return restTemplateCours.getForObject(this.serviceUrlMembre+"/recherche/{login}",Membre.class, login);
    }

    /**
     * GET un cours particulier
     * @param id
     * @return un cours
     */
    @Override
    public Cours findById(String id) {
        return restTemplateCours.getForObject(this.serviceUrlCours+"/{id}",Cours.class, id);
    }

    /**
     * POST Inscrit un membre à un cours
     * @param idc
     * @param loginM
     * @return 
     */
    @Override
        public Cours inscriptionCours(String idc, String loginM) {
        logger.info("Inscription du membre");  
        // On instancie une nouvelle entête
        HttpHeaders headers = new HttpHeaders();
        // On définit le contenu sous format JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> e = new HttpEntity<>(loginM, headers);
        return restTemplateCours.postForObject(this.serviceUrlCours+"/inscription/{id}", e, Cours.class, idc);
    }   

    /**
     * GET Liste des cours
     * @return liste des cours
     */    
    @Override
    public Cours[] getListeDesCours() {
        return restTemplateCours.getForObject(this.serviceUrlCours+"/liste", Cours[].class);
    }
    
    /**
     * GET Un lieu spécifique
     * @param idLieu
     * @return un lieu
     */
    @Override
    public LieuPiscine getLieu(String idLieu) {
        LieuPiscine lieu = null;
        try {
            // Dans un premier on stocke les données au format Json dans une String
            String pageTexte = "";
            HttpURLConnection conn = (HttpURLConnection) new URL(LIEU_SERVICE_URL).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            Scanner sc = new Scanner(new URL(LIEU_SERVICE_URL).openStream());
            while(sc.hasNext()) {
                pageTexte+=sc.nextLine();
            }
            sc.close();
        
            // On parse ensuite la chaine String puis on mappe en POJO
            ObjectMapper mapper = new ObjectMapper();
            // On lit l'arbre json
            JsonNode rootArray = mapper.readTree(pageTexte).path("records");
            // Pour chaque noeud, on associe les valeurs à la classe LieuPiscine
            for(JsonNode root : rootArray) {
                if(root.path("recordid").asText().equalsIgnoreCase(idLieu)) {
                    lieu = mapper.treeToValue(root.path("fields"), LieuPiscine.class);
                    lieu.setRecordid(root.path("recordid").asText());
                }
            }
        } catch (JsonProcessingException ex) { 
            java.util.logging.Logger.getLogger(FoController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(FoController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lieu;
    }
    
    /**
     * Liste tous les membres
     * @return la liste des membres
     */
    @Override
    public Membre[] getAllMembres() {
        return restTemplateMembre.getForObject(serviceUrlMembre, Membre[].class);
    }
    
    /**
     * Liste les enseigants
     * @return la liste des enseignants
     */
    @Override
    public Membre[] getListeEnseignants() {
        return restTemplateMembre.getForObject(serviceUrlMembre+"/enseignants", Membre[].class);
    }
}
