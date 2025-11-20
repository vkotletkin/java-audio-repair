package dev.kotletkin.silenceremover.service;

import org.springframework.web.multipart.MultipartFile;

public interface AudioCommandService {

    byte[] processWavAudio(MultipartFile audioFile);
}
