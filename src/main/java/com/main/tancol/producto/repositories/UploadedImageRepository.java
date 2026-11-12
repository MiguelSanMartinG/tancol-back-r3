package com.main.tancol.producto.repositories;

import com.main.tancol.common.entities.UploadedImage;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UploadedImageRepository extends CrudRepository<UploadedImage, UUID> {
}