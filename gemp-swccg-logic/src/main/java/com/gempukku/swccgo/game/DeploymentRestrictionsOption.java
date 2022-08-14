package com.gempukku.swccgo.game;


/**
 * This class is used to represent a way in which a card can ignore specific deployment restrictions when being deployed.
 */
public class DeploymentRestrictionsOption {
    private boolean _evenWithoutPresenceOrForceIcons;
    private boolean _ignoreLocationRestrictions;
    private boolean _ignoreGameTextRestrictions;
    private boolean _ignoreObjectiveRestrictions;
    private boolean _allowDeployLandedToExteriorSites;
    private boolean _allowDeployUnpilotedToSystemOrSector;
    private boolean _allowTrench;

    /**
     * Sets whether even without presence or Force icons.
     * @param value true or false
     */
    public void setEvenWithoutPresenceOrForceIcons(boolean value) {
        _evenWithoutPresenceOrForceIcons = value;
    }

    /**
     * Determines whether even without presence or Force icons.
     * @return true or false
     */
    public boolean isEvenWithoutPresenceOrForceIcons() {
        return _evenWithoutPresenceOrForceIcons;
    }

    /**
     * Sets whether to ignore location deployment restrictions.
     * @param ignore true or false
     */
    public void setIgnoreLocationDeploymentRestrictions(boolean ignore) {
        _ignoreLocationRestrictions = ignore;
    }

    /**
     * Determines whether to ignore location deployment restrictions.
     * @return true or false
     */
    public boolean isIgnoreLocationDeploymentRestrictions() {
        return _ignoreLocationRestrictions;
    }

    /**
     * Sets whether to ignore location deployment restrictions in game text.
     * @param ignore true or false
     */
    public void setIgnoreGameTextDeploymentRestrictions(boolean ignore) {
        _ignoreGameTextRestrictions = ignore;
    }

    /**
     * Determines whether to ignore location deployment restrictions in game text.
     * @return true or false
     */
    public boolean isIgnoreGameTextDeploymentRestrictions() {
        return _ignoreLocationRestrictions || _ignoreGameTextRestrictions;
    }

    /**
     * Sets whether to ignore Objective deployment restrictions.
     * @param ignore true or false
     */
    public void setIgnoreObjectiveDeploymentRestrictions(boolean ignore) {
        _ignoreObjectiveRestrictions = ignore;
    }

    /**
     * Determines whether to ignore objective deployment restrictions.
     * @return true or false
     */
    public boolean isIgnoreObjectiveDeploymentRestrictions() {
        return _ignoreObjectiveRestrictions;
    }

    /**
     * Sets whether to allow deployment as landed to exterior sites.
     * @param allowDeployLandedToExteriorSites true or false
     */
    public void setAllowDeployLandedToExteriorSites(boolean allowDeployLandedToExteriorSites) {
        _allowDeployLandedToExteriorSites = allowDeployLandedToExteriorSites;
    }

    /**
     * Determines whether to allow deployment as landed to exterior sites.
     * @return true or false
     */
    public boolean isAllowDeployLandedToExteriorSites() {
        return _allowDeployLandedToExteriorSites;
    }

    /**
     * Sets whether to allow deployment as landed to a system or sector.
     * @param allowDeployUnpilotedToSystemOrSector true or false
     */
    public void setAllowDeployUnpilotedToSystemOrSector(boolean allowDeployUnpilotedToSystemOrSector) {
        _allowDeployUnpilotedToSystemOrSector = allowDeployUnpilotedToSystemOrSector;
    }

    /**
     * Determines whether to allow deployment as unpiloted to a system or sector.
     * @return true or false
     */
    public boolean isAllowDeployUnpilotedToSystemOrSector() {
        return _allowDeployUnpilotedToSystemOrSector;
    }

    /**
     * Sets whether to allow deployment via Trench Rules.
     * @param allowTrench true or false
     */
    public void setAllowTrench(boolean allowTrench) {
        _allowTrench = allowTrench;
    }

    /**
     * Determines whether to allow deployment via Trench Rules.
     * @return true or false
     */
    public boolean isAllowTrench() {
        return _allowTrench;
    }

    /**
     * Gets an IgnoreDeploymentRestrictionsOption object that is set to allow deployment even without presence or Force icons.
     * @return the IgnoreDeploymentRestrictionsOption object
     */
    public static DeploymentRestrictionsOption evenWithoutPresenceOrForceIcons() {
        DeploymentRestrictionsOption option = new DeploymentRestrictionsOption();
        option.setEvenWithoutPresenceOrForceIcons(true);
        return option;
    }

    /**
     * Gets an IgnoreDeploymentRestrictionsOption object that is set to ignore location deployment restrictions.
     * @return the IgnoreDeploymentRestrictionsOption object
     */
    public static DeploymentRestrictionsOption ignoreLocationDeploymentRestrictions() {
        DeploymentRestrictionsOption option = new DeploymentRestrictionsOption();
        option.setIgnoreLocationDeploymentRestrictions(true);
        return option;
    }

    /**
     * Gets an IgnoreDeploymentRestrictionsOption object that is set to ignore game text deployment restrictions.
     * @return the IgnoreDeploymentRestrictionsOption object
     */
    public static DeploymentRestrictionsOption ignoreGameTextDeploymentRestrictions() {
        DeploymentRestrictionsOption option = new DeploymentRestrictionsOption();
        option.setIgnoreGameTextDeploymentRestrictions(true);
        return option;
    }

    /**
     * Gets an IgnoreDeploymentRestrictionsOption object that is set to allow deployment as landed to exterior sites.
     * @return the IgnoreDeploymentRestrictionsOption object
     */
    public static DeploymentRestrictionsOption allowToDeployLandedToExteriorSites() {
        DeploymentRestrictionsOption option = new DeploymentRestrictionsOption();
        option.setAllowDeployLandedToExteriorSites(true);
        return option;
    }

    /**
     * Gets an IgnoreDeploymentRestrictionsOption object that is set to allow deployment as unpiloted to systems or sectors.
     * @return the IgnoreDeploymentRestrictionsOption object
     */
    public static DeploymentRestrictionsOption allowToDeployUnpilotedToSystemOrSector() {
        DeploymentRestrictionsOption option = new DeploymentRestrictionsOption();
        option.setAllowDeployUnpilotedToSystemOrSector(true);
        return option;
    }

    /**
     * Gets an IgnoreDeploymentRestrictionsOption object that is set to allow deployment via Trench Rules.
     * @return the IgnoreDeploymentRestrictionsOption object
     */
    public static DeploymentRestrictionsOption allowedToTrench() {
        DeploymentRestrictionsOption option = new DeploymentRestrictionsOption();
        option.setAllowTrench(true);
        return option;
    }
}
