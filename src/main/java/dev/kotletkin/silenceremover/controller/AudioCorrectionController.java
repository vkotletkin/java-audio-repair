package dev.kotletkin.silenceremover.controller;

import dev.kotletkin.silenceremover.dto.AudioProcessingParams;
import dev.kotletkin.silenceremover.service.AudioCommandService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/correction")
@RequiredArgsConstructor
public class AudioCorrectionController {

    private static final MediaType AUDIO_WAV_MEDIATYPE = MediaType.parseMediaType("audio/wav");

    private final AudioCommandService audioService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> processAudio(@Parameter(description = "WAV audio file to process")
                                                 @RequestParam("file") MultipartFile file) {

        byte[] processedWavData = audioService.processWavAudio(file, new AudioProcessingParams());
        Resource resource = createByteArrayResource(processedWavData);

        return ResponseEntity.ok()
                .contentType(AUDIO_WAV_MEDIATYPE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("processed_" + file.getOriginalFilename())
                                .build()
                                .toString())
                .body(resource);
    }

    private Resource createByteArrayResource(byte[] processedWavData) {
        return new ByteArrayResource(processedWavData) {
            @Override
            public String getFilename() {
                return "processed_audio_" + System.currentTimeMillis() + ".wav";
            }
        };
    }
}
