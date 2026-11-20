package tn.esprit.autoloc.services;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.entities.Agence;
import tn.esprit.autoloc.entities.Vehicule;
import tn.esprit.autoloc.repositories.AgenceRepository;
import tn.esprit.autoloc.repositories.VehiculeRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class IAgenceServiceImp implements IAgenceService {

    private final AgenceRepository agenceRepository;
    private final VehiculeRepository vehiculeRepository;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public Agence ajouterAgence(Agence agence) {
        return agenceRepository.save(agence);
    }

    @Override
    public Agence modifierAgence(Agence agence) {
        return agenceRepository.save(agence);
    }

    @Override
    public List<Agence> consulterAgences() {
        return agenceRepository.findAll();
    }

    @Override
    public Agence consulterAgenceById(long idAgence) {
        return agenceRepository.findById(idAgence).orElse(null);
    }

    @Override
    public void supprimerAgence(long idAgence) {
        agenceRepository.deleteById(idAgence);
    }

    @Override
    @Transactional
    public Agence ajouterAgenceEtAffecterVehicules(Agence agence) {
        List<Vehicule> vehicules = agence.getVehicules();

        // 1) Enregistrer l'agence seule
        agence.setVehicules(null);
        Agence agenceSauvegardee = agenceRepository.save(agence);

        // 2) Enregistrer chaque véhicule et 3) l'affecter à l'agence
        List<Vehicule> vehiculesSauvegardes = new ArrayList<>();
        if (vehicules != null) {
            for (Vehicule v : vehicules) {
                v.setIdVehicule(null);
                v.setAgence(agenceSauvegardee);
                vehiculesSauvegardes.add(vehiculeRepository.save(v));
            }
        }
        agenceSauvegardee.setVehicules(vehiculesSauvegardes);
        return agenceSauvegardee;
    }

    // Question 2 : 2 minutes après la fin de chaque exécution
    @Scheduled(fixedDelay = 120000)
    public void suivreNombreAgences() {
        List<Agence> agences = agenceRepository.findAll();
        System.out.println("===== Suivi des agences =====");
        System.out.println("Nombre total d'agences : " + agences.size());
        System.out.println("Date et heure de la vérification : "
                + LocalDateTime.now().format(FORMAT));
    }
}