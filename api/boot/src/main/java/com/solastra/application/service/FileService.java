package com.solastra.application.service;

import com.solastra.application.port.in.UploadFileUseCase;
import com.solastra.application.port.out.FileRepository;
import org.springframework.stereotype.Service;

@Service
public class FileService implements UploadFileUseCase {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }
}
