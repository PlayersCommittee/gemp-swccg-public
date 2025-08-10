package com.gempukku.swccgo.logic.effects;


import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to capture the specified character.
 */
public class CaptureCharacterOnTableEffect extends CaptureCharactersOnTableEffect {

    /**
     * Creates an effect to capture the specified character.
     * @param action the action performing this effect
     * @param character the character to capture
     */
    public CaptureCharacterOnTableEffect(Action action, PhysicalCard character) {
        super(action, Collections.singleton(character));
    }

    /**
     * Creates an effect to capture the specified character.
     * @param action the action performing this effect
     * @param character the character to capture
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     */
    public CaptureCharacterOnTableEffect(Action action, PhysicalCard character, PhysicalCard cardFiringWeapon) {
        super(action, Collections.singleton(character), false, cardFiringWeapon);
    }

    /**
     * Creates an effect to capture the specified character.
     * @param action the action performing this effect
     * @param character the character to capture
     * @param freezeCharacter true if the character is 'frozen' when captured, otherwise false
     */
    public CaptureCharacterOnTableEffect(Action action, PhysicalCard character, boolean freezeCharacter) {
        super(action, Collections.singleton(character), freezeCharacter, null);
    }

    /**
     * Creates an effect to capture the specified character.
     * @param action the action performing this effect
     * @param character the character to capture
     * @param freezeCharacter true if the character is 'frozen' when captured, otherwise false
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     */
    public CaptureCharacterOnTableEffect(Action action, PhysicalCard character, boolean freezeCharacter, PhysicalCard cardFiringWeapon) {
        super(action, Collections.singleton(character), freezeCharacter, cardFiringWeapon);
    }

     /**
     * Creates an effect to capture the specified character.
     * @param action the action performing this effect
     * @param character the character to capture
     * @param freezeCharacter true if the character is 'frozen' when captured, otherwise false
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     * @param seizeEvenIfNotPossible true if seizing the captive will be facilitated by immediately disembarking a starship/vehicle or releasing another captive
     */
    public CaptureCharacterOnTableEffect(Action action, PhysicalCard character, boolean freezeCharacter, PhysicalCard cardFiringWeapon, boolean seizeEvenIfNotPossible) {
        super(action, Collections.singleton(character), freezeCharacter, cardFiringWeapon, seizeEvenIfNotPossible);
    }
}
