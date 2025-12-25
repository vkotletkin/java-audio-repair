package dev.kotletkin.silenceremover.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AudioCommandService {

    Resource processWavAudio(MultipartFile audioFile);

    byte[] convertPcmToWav(MultipartFile pcmFile);
}
