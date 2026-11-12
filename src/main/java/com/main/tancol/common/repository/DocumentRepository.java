package com.main.tancol.common.repository;

import com.main.tancol.common.entities.DocumentUploadController;
import com.main.tancol.common.entities.UploadedFile;
import com.main.tancol.common.entities.UploadedImage;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DocumentRepository extends CrudRepository<UploadedFile, UUID> {
}
