package com.ticketlog.server.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ticketlog.server.exception.ApiRequestException;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path root = Paths.get("uploads");

    @Override
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new ApiRequestException("Erro de armazenamento!");
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            Path filePath = this.root.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath);
            return filePath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Nao foi possivel salvar o arquivo. Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public boolean delete(String filename) {
        Path filePath = this.root.resolve(filename);
        try {
            Files.deleteIfExists(filePath);
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
       
    }

    @Override
    public String save(MultipartFile file, String filename) {
        try {
            Path filePath = this.root.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            return filePath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Nao foi possivel salvar o arquivo. Error: " + e.getMessage());
        }
    }

}