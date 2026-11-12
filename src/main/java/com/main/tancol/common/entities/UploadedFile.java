package com.main.tancol.common.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Slf4j
public class UploadedFile {
    @Id
    private UUID id;
    private String extension;
    @Version
    private Integer version;
}
