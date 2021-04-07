/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miage.AMS.ClubDePlongee.FrontOffice.transientO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Miage
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LieuPiscine {

    // Identifiant d'une piscine sur toulouse
    public String recordid;
    
    // Représente la saison d'ouverture du lieu
    public String saison;
    
    // Adresse de la piscine
    public String adresse;
    
    // Numéro de téléphone
    public String telephone;
    
    // Nom complet définissant la piscine
    public String nom_complet;
    
}
