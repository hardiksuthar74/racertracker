package com.service.racertracker.db.mixins;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public class IdMixin {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id",updatable = false,nullable = false)
    private UUID id;

}
