package dev.kotletkin.silenceremover.service;

import dev.kotletkin.silenceremover.dto.AudioProcessingParams;
import org.springframework.web.multipart.MultipartFile;

public interface AudioCommandService {

    byte[] processWavAudio(MultipartFile audioFile, AudioProcessingParams params);
}
