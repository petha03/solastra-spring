package com.solastra.application.service;

import com.solastra.application.port.in.UploadFileUseCase;
import com.solastra.application.port.out.FileRepository;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class FileService implements UploadFileUseCase {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public String uploadFile(String fileName, String contentType, InputStream inputStream, long contentLength) {
        return fileRepository.saveFile(fileName, contentType, inputStream, contentLength);
    }
}
