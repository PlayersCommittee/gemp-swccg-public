package com.gempukku.swccgo.game;


import com.gempukku.swccgo.common.CaptureOption;

/**
 * This class is used to represent a way in which a card can be deployed as a captive.
 */
public class DeployAsCaptiveOption {
    private CaptureOption _captureOption;
    private boolean _frozen;

    /**
     * Sets the capture option.
     * @param captureOption capture option
     */
    public void setCaptureOption(CaptureOption captureOption) {
        _captureOption = captureOption;
    }

    /**
     * Gets the capture option.
     * @return the capture option
     */
    public CaptureOption getCaptureOption() {
        return _captureOption;
    }

    /**
     * Sets whether captive is to be frozen.
     * @param frozen true if captive is to be frozen, otherwise false
     */
    public void setFrozenCaptive(boolean frozen) {
        _frozen = frozen;
    }

    /**
     * Determines if the captive is to be frozen.
     * @return true if captive is to be frozen, otherwise false
     */
    public boolean isFrozenCaptive() {
        return _frozen;
    }

    /**
     * Gets an DeployAsCaptiveOption object that is set to deploy as imprisoned captive.
     * @return the DeployAsCaptiveOption object
     */
    public static DeployAsCaptiveOption deployAsImprisonedCaptive() {
        DeployAsCaptiveOption option = new DeployAsCaptiveOption();
        option.setCaptureOption(CaptureOption.IMPRISONMENT);
        return option;
    }

    /**
     * Gets an DeployAsCaptiveOption object that is set to deploy as imprisoned frozen captive.
     * @return the DeployAsCaptiveOption object
     */
    public static DeployAsCaptiveOption deployAsImprisonedFrozenCaptive() {
        DeployAsCaptiveOption option = new DeployAsCaptiveOption();
        option.setCaptureOption(CaptureOption.IMPRISONMENT);
        option.setFrozenCaptive(true);
        return option;
    }

    /**
     * Gets an DeployAsCaptiveOption object that is set to deploy as unattended frozen captive.
     * @return the DeployAsCaptiveOption object
     */
    public static DeployAsCaptiveOption deployAsUnattendedFrozenCaptive() {
        DeployAsCaptiveOption option = new DeployAsCaptiveOption();
        option.setCaptureOption(CaptureOption.LEAVE_UNATTENDED);
        option.setFrozenCaptive(true);
        return option;
    }
}
