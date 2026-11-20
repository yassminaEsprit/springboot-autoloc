package tn.esprit.autoloc.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.entities.enums.CategorieVehicule;
import tn.esprit.autoloc.entities.enums.StatutVehicule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    private String immatriculation;
    private String marque;
    private String modele;

    @Enumerated(EnumType.STRING)
    private CategorieVehicule categorie;

    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut;

    @ManyToOne
    @JoinColumn(name = "id_agence")
    @JsonIgnoreProperties({"vehicules", "employes"})
    private Agence agence;

    @OneToMany(mappedBy = "vehicule")
    @JsonIgnoreProperties("vehicule")
    private List<Maintenance> maintenances;

    @OneToMany(mappedBy = "vehicule")
    @JsonIgnoreProperties("vehicule")
    private List<Reservation> reservations;

    // Côté propriétaire du @ManyToMany (table vehicule_equipement)
    @ManyToMany
    @JoinTable(
            name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "id_vehicule"),
            inverseJoinColumns = @JoinColumn(name = "id_equipement")
    )
    @JsonIgnoreProperties("vehicules")
    private List<Equipement> equipements = new ArrayList<>();
}