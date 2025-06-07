package com.biblio.empruntservice.dto;

import lombok.Data;

@Data
public class ExemplaireDTO {
    private Long id;
    private boolean disponible;
    private boolean livreId;
}
