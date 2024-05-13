package com.gempukku.swccgo.game;


/**
 * This class is used to represent a way keep track of special deployment options.
 */
public class DeploymentOption {
    private boolean _allowDeployOfSquadronWithoutDeployCost;

    /**
     * Sets whether deployment allowed for squadrons that have no deploy cost.
     * @param value true or false
     */
    public void setAllowDeploymentOfSquadronWithoutDeployCost(boolean value) {
        _allowDeployOfSquadronWithoutDeployCost = value;
    }

    /**
     * Determines whether deployment allowed for squadrons that have no deploy cost.
     * @return true or false
     */
    public boolean isAllowDeploymentOfSquadronWithoutDeployCost() {
        return _allowDeployOfSquadronWithoutDeployCost;
    }

    /**
     * Gets an DeploymentOption object that is set to allow deployment of squadrons that have no deploy cost.
     * @return the DeploymentOption object
     */
    public static DeploymentOption allowSquadronWithoutDeployCost() {
        DeploymentOption option = new DeploymentOption();
        option.setAllowDeploymentOfSquadronWithoutDeployCost(true);
        return option;
    }
}
