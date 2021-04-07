/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miage.AMS.ClubDePlongee.FrontOffice.transientO;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Miage
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Membre implements Serializable {
    
    // Prix de la cotisation à l'année pour un membre
    public static final double PRIX_COTISATION_ANNEE = 10;
    
    public Long id;
    
    // Nom d'utlisateur
    public String userLogin;
        
    // Mdp de l'utilisateur
    public String password;
    
    // Nom du membre
    public String nom;
    
    // Prénom du membre
    public String prenom;
    
    // Email du membre
    public String email;
    
    // Niveau d'expertise en plongée (de 1 à 5)
    public Niveau niveauExpertise;
    
    // Numéro de licence du membre
    public Long numLicence;
    
    // Date à laquelle le certificat est déposé
    public Date dateCertificat;
    
    // Numéro de l'iban pour régler la cotisation
    public String iban;
    
    // Signale si le paiement est valide ou non
    public Boolean cotisationValide;

    // Un membre est président
    public Boolean president;
    
    // Mmebre enseignant
    public Boolean enseignant;
    
    // Membre secretaire
    public Boolean secretaire;
    
    // Désigne le fait qu'un membre soit apte ou non; true = apte
    public Boolean apte;
}
