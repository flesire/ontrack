package net.nemerosa.ontrack.model;

import lombok.Data;

@Data
public class PromotionLevel {

    private final String id;
    private final String name;
    private final String description;
    private final Branch branch;

}
