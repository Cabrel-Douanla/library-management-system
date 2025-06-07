package com.library.management.mspubliccatalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nouveautes")
public class NewArrival {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "livre_id", unique = true, nullable = false)
    private UUID bookId;

    @Column(name = "date_ajout", nullable = false)
    private LocalDate dateAjout; // Date à laquelle il a été marqué comme nouvelle arrivée
}