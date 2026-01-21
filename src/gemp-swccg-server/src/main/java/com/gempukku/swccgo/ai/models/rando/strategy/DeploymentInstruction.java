package com.gempukku.swccgo.ai.models.rando.strategy;

/**
 * A specific instruction to deploy one card to one location.
 *
 * Ported from Python deploy_planner.py DeploymentInstruction dataclass.
 */
public class DeploymentInstruction {
    private String cardBlueprintId;
    private String cardName;
    private String targetLocationId;       // null for locations (they deploy to table)
    private String targetLocationName;
    private int priority;                  // Lower = deploy first (0 = locations, 1 = reinforce, 2 = establish)
    private String reason;
    private int powerContribution = 0;
    private int deployCost = 0;
    private int abilityContribution = 0;   // For battle destiny eligibility

    // Backup target if primary is unavailable
    private String backupLocationId;
    private String backupLocationName;
    private String backupReason;

    // For pilots deploying aboard ships
    private String aboardShipName;
    private String aboardShipBlueprintId;
    private String aboardShipCardId;

    // For cards deploying to locations in hand
    private boolean targetLocationPending = false;
    private String targetLocationBlueprintId;

    public DeploymentInstruction(String cardBlueprintId, String cardName, String targetLocationId,
                                  String targetLocationName, int priority, String reason) {
        this.cardBlueprintId = cardBlueprintId;
        this.cardName = cardName;
        this.targetLocationId = targetLocationId;
        this.targetLocationName = targetLocationName;
        this.priority = priority;
        this.reason = reason;

        // Auto-detect pending locations from 'planned_' prefix
        if (targetLocationId != null && targetLocationId.startsWith("planned_")) {
            this.targetLocationBlueprintId = targetLocationId.substring(8);  // Skip "planned_"
            this.targetLocationPending = true;
            this.targetLocationId = null;
        }
    }

    // Getters and setters
    public String getCardBlueprintId() { return cardBlueprintId; }
    public void setCardBlueprintId(String cardBlueprintId) { this.cardBlueprintId = cardBlueprintId; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public String getTargetLocationId() { return targetLocationId; }
    public void setTargetLocationId(String targetLocationId) { this.targetLocationId = targetLocationId; }

    public String getTargetLocationName() { return targetLocationName; }
    public void setTargetLocationName(String targetLocationName) { this.targetLocationName = targetLocationName; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getPowerContribution() { return powerContribution; }
    public void setPowerContribution(int powerContribution) { this.powerContribution = powerContribution; }

    public int getDeployCost() { return deployCost; }
    public void setDeployCost(int deployCost) { this.deployCost = deployCost; }

    public int getAbilityContribution() { return abilityContribution; }
    public void setAbilityContribution(int abilityContribution) { this.abilityContribution = abilityContribution; }

    public String getBackupLocationId() { return backupLocationId; }
    public void setBackupLocationId(String backupLocationId) { this.backupLocationId = backupLocationId; }

    public String getBackupLocationName() { return backupLocationName; }
    public void setBackupLocationName(String backupLocationName) { this.backupLocationName = backupLocationName; }

    public String getBackupReason() { return backupReason; }
    public void setBackupReason(String backupReason) { this.backupReason = backupReason; }

    public String getAboardShipName() { return aboardShipName; }
    public void setAboardShipName(String aboardShipName) { this.aboardShipName = aboardShipName; }

    public String getAboardShipBlueprintId() { return aboardShipBlueprintId; }
    public void setAboardShipBlueprintId(String aboardShipBlueprintId) { this.aboardShipBlueprintId = aboardShipBlueprintId; }

    public String getAboardShipCardId() { return aboardShipCardId; }
    public void setAboardShipCardId(String aboardShipCardId) { this.aboardShipCardId = aboardShipCardId; }

    public boolean isTargetLocationPending() { return targetLocationPending; }
    public void setTargetLocationPending(boolean targetLocationPending) { this.targetLocationPending = targetLocationPending; }

    public String getTargetLocationBlueprintId() { return targetLocationBlueprintId; }
    public void setTargetLocationBlueprintId(String targetLocationBlueprintId) { this.targetLocationBlueprintId = targetLocationBlueprintId; }
}
