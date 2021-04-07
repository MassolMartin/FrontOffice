/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miage.AMS.ClubDePlongee.FrontOffice.repositories;

import miage.AMS.ClubDePlongee.FrontOffice.transientO.Cours;
import miage.AMS.ClubDePlongee.FrontOffice.transientO.LieuPiscine;
import miage.AMS.ClubDePlongee.FrontOffice.transientO.Membre;

/**
 * 
 * @author Miage
 */
public interface CoursMembreRepository {
    
    Cours nouveauCours(Cours c);
    
    Membre getMembreByLogin(String login);
    
    Cours findById(String id);
    
    Cours inscriptionCours(String idc, String loginM);
    
    Cours[] getListeDesCours();
    
    LieuPiscine getLieu(String idLieu);
    
    Membre[] getAllMembres();
    
    Membre[] getListeEnseignants();
}
