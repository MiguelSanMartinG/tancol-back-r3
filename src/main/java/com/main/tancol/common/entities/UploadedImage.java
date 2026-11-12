package com.main.tancol.common.entities;

import jakarta.persistence.*;
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
public class UploadedImage {
    @Id
    private UUID id;
    private String extension;
    @Version
    private Integer version;
}
