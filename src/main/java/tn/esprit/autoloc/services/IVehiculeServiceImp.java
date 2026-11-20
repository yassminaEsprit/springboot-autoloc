package tn.esprit.autoloc.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.autoloc.entities.Equipement;
import tn.esprit.autoloc.entities.Vehicule;
import tn.esprit.autoloc.repositories.EquipementRepository;
import tn.esprit.autoloc.repositories.VehiculeRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class IVehiculeServiceImp implements IVehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final EquipementRepository equipementRepository;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public Vehicule ajouterVehicule(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule modifierVehicule(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public List<Vehicule> consulterVehicules() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule consulterVehiculeById(long idVehicule) {
        return vehiculeRepository.findById(idVehicule).orElse(null);
    }

    @Override
    public void supprimerVehicule(long idVehicule) {
        vehiculeRepository.deleteById(idVehicule);
    }

    // Affectation au départ du client
    @Override
    @Transactional
    public Vehicule affecterEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Véhicule introuvable : " + idVehicule));
        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Équipement introuvable : " + idEquipement));

        if (vehicule.getEquipements() == null) {
            vehicule.setEquipements(new ArrayList<>());
        }
        boolean dejaAffecte = vehicule.getEquipements().stream()
                .anyMatch(e -> e.getIdEquipement().equals(idEquipement));
        if (!dejaAffecte) {
            vehicule.getEquipements().add(equipement);
        }
        return vehiculeRepository.save(vehicule);
    }

    // Désaffectation au retour du client
    @Override
    @Transactional
    public Vehicule desaffecterEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Véhicule introuvable : " + idVehicule));
        if (!equipementRepository.existsById(idEquipement)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Équipement introuvable : " + idEquipement);
        }

        if (vehicule.getEquipements() != null) {
            vehicule.getEquipements().removeIf(e -> e.getIdEquipement().equals(idEquipement));
        }
        return vehiculeRepository.save(vehicule);
    }

    // Question 1 : 5 secondes après le démarrage, puis toutes les 60 secondes
    @Scheduled(initialDelay = 5000, fixedRate = 60000)
    public void actualiserEtatVehicules() {
        List<Vehicule> vehicules = vehiculeRepository.findAll();
        System.out.println("===== Actualisation des véhicules =====");
        System.out.println("Nombre total de véhicules : " + vehicules.size());
        System.out.println("Date et heure de l'actualisation : "
                + LocalDateTime.now().format(FORMAT));
    }

    // Question 3 : tous les lundis à 09h00
    @Scheduled(cron = "0 0 9 * * MON", zone = "Africa/Tunis")
    public void genererRapportVehicules() {
        List<Vehicule> vehicules = vehiculeRepository.findAll();
        int total = vehicules.size();
        System.out.println("========== RAPPORT HEBDOMADAIRE DES VÉHICULES ==========");
        System.out.println("Nombre total de véhicules : " + total);
        System.out.println("Date et heure de génération : "
                + LocalDateTime.now().format(FORMAT));
        System.out.println("========================================================");
    }
}