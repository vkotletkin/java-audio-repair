package dev.kotletkin.silenceremover;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SilenceremoverApplication {
    static void main(String[] args) {
        /*
        ffmpeg -i "OSR_us_000_0036_8k.wav" -af "afftdn=nf=-30:tn=1, speechnorm=e=4:r=0.0005:l=1, highpass=f=80,lowpass=f=3500, equalizer=f=1200:width_type=h:width=400:g=1.5, acompressor=threshold=-35dB:ratio=1.5:attack=50:release=500, silenceremove=start_periods=1:start_threshold=-90dB:start_duration=3.0:detection=peak:stop_periods=-1:stop_threshold=-90dB:stop_duration=3.0, loudnorm=I=-16:TP=-2.0:LRA=11" -ar 8000 -c:a pcm_s16le "OSR_us_000_0036_8k_processed.wav" -y
         */
        SpringApplication.run(SilenceremoverApplication.class, args);
    }
}
