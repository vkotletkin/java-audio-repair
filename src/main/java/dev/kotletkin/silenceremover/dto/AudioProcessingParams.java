package dev.kotletkin.silenceremover.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AudioProcessingParams {

    @NotNull
    @Min(-110)
    @Max(110)
    byte noiseFloor = -30;

    @NotNull
    @Max(1)
    @Min(0)
    byte trackingNoiseEnabled = 1;

    @NotNull
    @Max(100)
    @Min(10)
    byte highPass;

    @NotNull
    @Max(4000)
    @Min(300)
    short lowPass = 3400;

    @NotNull
    @Max(3)
    @Min(1)
    byte startPeriods = 1;

    @NotNull
    @Max(110)
    @Min(-110)
    byte startThreshold = -90;

    @NotNull
    @Max(5)
    @Min(1)
    byte startDuration = 3;

    @NotNull
    @Max(3)
    @Min(1)
    byte stopPeriods = 1;

    @NotNull
    @Max(110)
    @Min(-110)
    byte stopThreshold = -90;

    @NotNull
    @Max(5)
    @Min(1)
    byte stopDuration = 3;
}
