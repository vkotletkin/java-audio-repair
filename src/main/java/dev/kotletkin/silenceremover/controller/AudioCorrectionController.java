package dev.kotletkin.silenceremover.controller;

import dev.kotletkin.silenceremover.service.AudioCommandService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AudioCorrectionController {

    private static final MediaType AUDIO_WAV_MEDIATYPE = MediaType.parseMediaType("audio/wav");

    private final AudioCommandService audioService;

    @PostMapping(path = "/correction",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> correctionWav(@Parameter(description = "WAV audio file to process")
                                                  @RequestParam("file") MultipartFile file) {

        Resource resource = audioService.upWavQuality(file);

        return ResponseEntity.ok()
                .contentType(AUDIO_WAV_MEDIATYPE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("processed_" + file.getOriginalFilename())
                                .build()
                                .toString())
                .body(resource);
    }

    @PostMapping(path = "/convert/pcm-to-wav",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> processPcmToWav(@Parameter(description = "PCM file to process")
                                                    @RequestParam("file") MultipartFile file,
                                                    @RequestParam(value = "audioRate", defaultValue = "44100") Integer audioRate,
                                                    @Parameter(schema = @Schema(type = "integer", allowableValues = {"1", "2"}))
                                                    @RequestParam(value = "channelsCount", defaultValue = "1")
                                                    @Min(1) @Max(2) Byte channels) {

        Resource resource = audioService.convertPcmToWav(file, audioRate, channels);

        return ResponseEntity.ok()
                .contentType(AUDIO_WAV_MEDIATYPE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("processed_" + file.getOriginalFilename())
                                .build()
                                .toString())
                .body(resource);
    }
}
