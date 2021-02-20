package com.ticketlog.server.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
  public void init();

  public String save(MultipartFile file);

  public String save(MultipartFile file, String filename);


  public void deleteAll();

  public boolean delete(String filename);

}