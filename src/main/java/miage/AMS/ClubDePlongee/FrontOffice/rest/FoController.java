/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miage.AMS.ClubDePlongee.FrontOffice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import static miage.AMS.ClubDePlongee.FrontOffice.FrontOfficeApplication.LIEU_SERVICE_URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import miage.AMS.ClubDePlongee.FrontOffice.repositories.CoursMembreRepository;
import miage.AMS.ClubDePlongee.FrontOffice.transientO.Cours;
import miage.AMS.ClubDePlongee.FrontOffice.transientO.LieuPiscine;
import miage.AMS.ClubDePlongee.FrontOffice.transientO.Membre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller pour les services associés au Front Office
 * @author Miage
 */
@RestController("/")
public class FoController {
    
    Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    CoursMembreRepository coursMembreRepo;
    
    /**
     * POST Création d'un cours respectant les règles métiers définies
     * @param c
     * @return un nouveau cours
     */
    @PostMapping("/nouveau")
    public Cours nouveauCours(@RequestBody Cours c) {
        logger.info("On ajoute un cours : " + c.nom);
        Membre m = coursMembreRepo.getMembreByLogin(c.getLoginEnseignant());
        // Le membre doit être un enseignant
        if(!m.getEnseignant()) {
            logger.info("ERREUR : Membre non enseignant");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce login n'est pas enseignant : " + c.getLoginEnseignant());
        }
        
        // 604800000 ajoute 7 jours à la date actuelle
        if(c.getCreneau().before(new Date(new Date().getTime() + 604800000))) {
            logger.info("ERREUR : La date du cours doit être supérieure à 7 à partir de maintenant");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La date du cours doit être supérieure à 7 à partir de maintenant : " + c.getCreneau());
        }
        
        // Un enseignant ne peut créer un cours que si il est apte, cad avec un certificat valide
        if(!m.getApte()) {
            logger.info("ERREUR : Cet enseignant n'est pas apte");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet enseignant n'est pas apte : " + m.getPrenom() + " " + m.getNom());
        }   
        // L'enseignant doit avoir un niveau d'expertise strictement supérieur à celui du niveau cible du cours
        if(c.getNiveauCible().compareTo(m.getNiveauExpertise()) >= 0) {
            logger.info("ERREUR : Cet enseignant n'a pas un niveau strictement supérieur au niveau cible.");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet enseignant n'a pas un niveau strictement supérieur au niveau cible.");
        }
        // L'identifiant du lieu doit être valide
        if(coursMembreRepo.getLieu(c.getLieu()) == null) {
            logger.info("ERREUR : Lieu invalide");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lieu invalide");
        }
        logger.info("Cours créer avec succès !");
        return coursMembreRepo.nouveauCours(c);
    }  
    
    /**
     * POST inscription d'un membre à un cours en respectant les règles métiers
     * @param idc
     * @param loginM
     * @return un cours avec un membre inscrit en plus
     */
    @PostMapping("/inscription/{id}")
    public Cours inscriptionCours(@PathVariable("id") String idc, @RequestBody String loginM) {;
        Cours cours = this.coursMembreRepo.findById(idc);
        Membre membre = this.coursMembreRepo.getMembreByLogin(loginM);
        logger.info("Inscription de " + membre.getPrenom() + " " + membre.getNom() + " au cours " + cours.getNom());
        // Il faut qu'il reste des places dans ce cours pour s'inscrire
        if(!cours.placesRestantes(membre)) {
            logger.info("ERREUR : Plus de places disponibles dans ce cours : " + cours.getNom());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ERREUR : Plus de places dans ce cours");
        }
        // Ne peut pas s'inscrire à un cours si il est déjà passé
        if(cours.getCreneau().before(new Date())) {
            logger.info("ERREUR : Ce cours est déjà passé : " + cours.getNom());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ERREUR : Cours terminé");
        }
        // Il faut que le membre est le niveau ciblé du cours
        if(membre.getNiveauExpertise() == null || !membre.getNiveauExpertise().equals(cours.getNiveauCible())) {
            logger.info("ERREUR : Le niveau de ce membre n'est pas égal à celui du cours. Niveau du cours : " + cours.getNiveauCible() + " Niveau membre :" + membre.getNiveauExpertise());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ERREUR : Le niveau de ce membre n'est pas égal à celui du cours");
        }
        // L'enseignant ne peux pas s'inscrire à son propre cours, c'est bête
        if(cours.getLoginEnseignant().equalsIgnoreCase(membre.getUserLogin())) {
            logger.info("ERREUR : Un enseignant n'est pas omniprésent, il ne peux donc pas s'inscrire");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ERREUR : Un enseignant n'est pas omniprésent");
        }
        // Le membre doit être apte
        if(!membre.getApte()) {
            logger.info("ERREUR : Ce membre n'est pas apte");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ERREUR : Ce membre n'est pas apte");
        }
        // Le membre doit avoir réglé sa cotisation
        if(!membre.getCotisationValide()) {
            logger.info("ERREUR : La cotisation de ce membre n'est pas valide");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ERREUR : La cotisation de ce membre n'est pas valide");
        }
        // Le membre doit être 
        return coursMembreRepo.inscriptionCours(idc, loginM);
    }
    
    /**
     * GET Liste les cours disponibles pour un membre 
     * @param login
     * @return 
     */
    @GetMapping("/disponibles/{login}")
    public Collection<Cours> listerCoursDisponiblesPourUnMembre(@PathVariable("login") String login) {
        Collection<Cours> sortie = new ArrayList<>();
        Membre m = coursMembreRepo.getMembreByLogin(login);
        Cours[] cours = coursMembreRepo.getListeDesCours();
        Boolean ok;
        logger.info("Demande de la liste des cours disponibles pour ce membre " + m.getPrenom() + " " + m.getNom());
        for (Cours cour : cours) {
            ok = true;
            // Il faut qu'il reste des places dans ce cours pour s'inscrire
            if(!cour.placesRestantes(m)) {
                ok = false;
            }
            // Ne peut pas s'inscrire à un cours si il est déjà passé
            if(cour.getCreneau().before(new Date())) {
                ok = false;
            }
            // Il faut que le membre est le niveau ciblé du cours
            if(m.getNiveauExpertise() == null || !m.getNiveauExpertise().equals(cour.getNiveauCible())) {
                ok = false;
            }
            // L'enseignant ne peux pas s'inscrire à son propre cours, c'est bête
            if(cour.getLoginEnseignant().equalsIgnoreCase(m.getUserLogin())) {
                ok = false;
            }
            // Le membre doit avoir sa cotisation valide
            if(!m.getCotisationValide()) {
                ok = false;
            }
            // Le membre doit être apte
            if(!m.getApte()) {
                ok = false;
            }
            if(ok) {
                sortie.add(cour);
            }
        }
        return sortie;
    }
    
    /**
     * Liste des piscines sur toulouse
     * @return la liste des piscines sur toulouse
     */
    @GetMapping("/liste/piscines")
    public Collection<LieuPiscine> listePiscinesToulouse() {
        ArrayList<LieuPiscine> lieux = new ArrayList<>();
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
                LieuPiscine lieuNode = mapper.treeToValue(root.path("fields"), LieuPiscine.class);
                lieuNode.setRecordid(root.path("recordid").asText());
                lieux.add(lieuNode);
            }
        } catch (JsonProcessingException ex) { 
            java.util.logging.Logger.getLogger(FoController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            java.util.logging.Logger.getLogger(FoController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lieux;
    }
    
    /**
     * GET Statistiques destinées au président
     * @return une hashMap de stats
     */
    @GetMapping("/statistiques")
    public HashMap<String,String> getStats() {
        HashMap<String,String> listeStats = new HashMap<>();
        Membre[] listeMembres = coursMembreRepo.getAllMembres();
        // Nombre de membres 
        listeStats.put("nbMembres",String.valueOf(listeMembres.length));
        // Nombre d'enseignants
        listeStats.put("nbEnseignants",String.valueOf(coursMembreRepo.getListeEnseignants().length));
        // Nombre de cours positionnés
        listeStats.put("nbCours",String.valueOf(coursMembreRepo.getListeDesCours().length));
        //Total des cotisations prévues
        listeStats.put("cotisationPrevues",String.valueOf(listeMembres.length*Membre.PRIX_COTISATION_ANNEE));
        // Total des cotisations réglées
        Double total = 0.0;
        for (Membre m : listeMembres) {
            if(m.cotisationValide) {
                total+=Membre.PRIX_COTISATION_ANNEE;
            }
        }
        listeStats.put("cotisationReglees",String.valueOf(total));
        return listeStats;
    }
    
    /**
     * Permet de connaître le solde du compte de l'association
     * NOTE : Il est impossible actuellement de calculer ce montant
     * @return solde du compte de l'association
     */
    @GetMapping("/solde")
    public String getSoldeCompte() {
        return "Solde du compte de l'association : 2 156€";
    }
}
