package dev.kotletkin.silenceremover.service.impl;

import dev.kotletkin.silenceremover.dto.AudioProcessingParams;
import dev.kotletkin.silenceremover.service.AudioCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FFMpegCommandServiceImpl implements AudioCommandService {


    @Override
    public byte[] processWavAudio(MultipartFile audioFile, AudioProcessingParams params) {
        return new byte[0];
    }
}
