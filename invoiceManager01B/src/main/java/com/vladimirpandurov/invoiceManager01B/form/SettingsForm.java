package com.vladimirpandurov.invoiceManager01B.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsForm {
    @NotNull(message = "Enabled cannot be null or empty")
    private Boolean enabled;
    @NotNull(message = "Not Locked be null or empty")
    private Boolean notLocked;
}
