package dev.kotletkin.silenceremover.service.impl;

import dev.kotletkin.silenceremover.exception.FileProcessingException;
import dev.kotletkin.silenceremover.service.AudioCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FFMpegCommandServiceImpl implements AudioCommandService {

    private static final String PROCESSED_POSTFIX = "_processed";

    private final FileStorageService fileStorageService;

    @Override
    public byte[] processWavAudio(MultipartFile audioFile) {
        FileStorageService.FileSaveResult fileSaveResult = fileStorageService.saveFile(audioFile);
        String processedFilePath = processCommand(Path.of(fileSaveResult.filePath()));
        byte[] processedWavBytes = fileStorageService.readFileToByteArray(processedFilePath);
        fileStorageService.deleteFile(List.of(fileSaveResult.filePath(), processedFilePath));
        return processedWavBytes;
    }

    private String processCommand(Path filepath) {

        String fileName = filepath.getFileName().toString().replace(".wav", PROCESSED_POSTFIX + ".wav");
        Path newPath = filepath.resolveSibling(fileName);

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", filepath.toString(),
                "-af", "afftdn=nf=-30:tn=1, speechnorm=e=4:r=0.0005:l=1, highpass=f=80,lowpass=f=3500,equalizer=f=1000:width_type=h:width=600:g=1.8,equalizer=f=2500:width_type=h:width=800:g=1.2,acompressor=threshold=-35dB:ratio=1.5:attack=50:release=500,silenceremove=start_periods=1:start_threshold=-90dB:start_duration=3.0:detection=peak:stop_periods=1:stop_threshold=-90dB:stop_duration=3.0, loudnorm=I=-16:TP=-2.0:LRA=11",
                "-ar", "8000",
                "-c:a", "pcm_s16le",
                newPath.toString(),
                "-y"
        );

        try {
            String command = String.join(" ", pb.command());
            log.info(command);
            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return newPath.toString();
            } else {
                throw new FileProcessingException("Код обработки аудио некорректен");
            }
        } catch (IOException | InterruptedException e) {
            throw new FileProcessingException("Возникли проблемы с выполнением обработки аудио");
        }
    }
}
