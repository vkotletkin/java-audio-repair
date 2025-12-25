package dev.kotletkin.silenceremover.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AudioCommandService {

    Resource upWavQuality(MultipartFile audioFile);

    Resource convertPcmToWav(MultipartFile pcmFile, int audioRate, byte channelCount);
}
