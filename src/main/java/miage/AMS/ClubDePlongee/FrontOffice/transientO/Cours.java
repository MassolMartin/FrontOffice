/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miage.AMS.ClubDePlongee.FrontOffice.transientO;

import java.util.Collection;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Transiant object pour un cours
 * @author Miage
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Cours {
    
    // Nombre d'élèves max dans un cours (pour le test 2)
    private static final int ELEVES_MAX = 2;
    
    // Identifiant d'un cours
    public String id;
    
    // Nom du cours
    public String nom;
    
    // Niveau cible fixé pour le cours
    public Niveau niveauCible;
    
    // Date du créneau du cours
    public Date creneau;
    
    // Enseignant sur ce cours
    public String loginEnseignant;
    
    // Lieu du cours
    public String lieu;
    
    // Durée du cours
    public double duree;
    
    // Liste des participants
    public Collection<String> participants;
    
    /**
     * Permet de savoir si il reste des places dans ce cours
     * Normalement 15 places mais pour les tests 2;
     * @param m
     * @return 
     * true = places restantes pour ce cours
     * False = plus de places
     */
    public Boolean placesRestantes(Membre m) {
        // Si il y n'y a plus de places ou que l'élève ne soit pas déjà présen
        if(participants == null) {
            return true;
        } else {
            return !(participants.size() >= ELEVES_MAX || participants.contains(m.getUserLogin()));
        }
    }
}
