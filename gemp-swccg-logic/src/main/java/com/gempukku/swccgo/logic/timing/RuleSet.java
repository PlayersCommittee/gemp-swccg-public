package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.rules.*;

/**
 * Defines specific automatic game rules to apply.
 */
public class RuleSet {
    private ActionsEnvironment _actionsEnvironment;
    private ModifiersEnvironment _modifiersEnvironment;

    public RuleSet(ActionsEnvironment actionsEnvironment, ModifiersEnvironment modifiersEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _modifiersEnvironment = modifiersEnvironment;
    }

    /**
     * Applies the rules.
     * @param game the game
     */
    public void applyRuleSet(SwccgGame game) {
        new AdmiralsOrderRule(_actionsEnvironment).applyRule();
        new AsteroidDestinyRule(_actionsEnvironment).applyRule();
        new AT_AT_LandspeedRule(_modifiersEnvironment).applyRule();
        new AttachedToInvalidCardRule(_actionsEnvironment).applyRule();
        new AttackRunLeadStarfighterRule(_actionsEnvironment).applyRule();
        new BlownAwayDeathStarOrDeathStarIIRule(_actionsEnvironment).applyRule();
        new BlownAwayRebelBaseRule(_actionsEnvironment).applyRule();
        new BombingRunRule(_actionsEnvironment).applyRule();
        new BreakCoverWhenNotSpyRule(_actionsEnvironment).applyRule();
        new CancelGameTextRule(_actionsEnvironment).applyRule();
        new CaveRule(_actionsEnvironment).applyRule();
        new CharactersForfeitReducedToZeroRule(_actionsEnvironment).applyRule();
        new CreaturesAttackEachOtherRule(_actionsEnvironment).applyRule();
        new DeathStarAndDeathStarIIRule(_actionsEnvironment).applyRule();
        new EffectsOfRevolutionRule(_actionsEnvironment).applyRule();
        new EffectsOfScarifTurboLiftComplexRotationRule(_actionsEnvironment).applyRule();
        new ExcludeBattleParticipantsRule(_actionsEnvironment).applyRule();
        new ExpandLocationGameTextRule(_actionsEnvironment).applyRule();
        new HabitatRule(_actionsEnvironment).applyRule();
        new HitCardOutsideOfAttackOrBattleRule(_actionsEnvironment).applyRule();
        new HothEnergyShieldRule(_modifiersEnvironment).applyRule();
        new FrozenCaptiveRule(_modifiersEnvironment).applyRule();
        new InsertCardRevealedRule(_actionsEnvironment).applyRule();
        if (game.getFormat().hasJpSealedRule()) {
            new JabbasPalaceSealedRule(_modifiersEnvironment).applyRule();
        }
        new JediTestTargetRule(_actionsEnvironment).applyRule();
        new JumpOffVehicleRule(_actionsEnvironment).applyRule();
        new LeavesTableCardRule(_actionsEnvironment).applyRule();
        new LostIfAboutToBeStolenRule(_actionsEnvironment).applyRule();
        new LukesBackpackRule(_actionsEnvironment).applyRule();
        new OperativesRule(_actionsEnvironment, _modifiersEnvironment).applyRule();
        new PresenceIconRule(_modifiersEnvironment).applyRule();
        new ReleaseCaptivesWithLightSideEscortRule(_actionsEnvironment).applyRule();
        new SuspendCardRule(_actionsEnvironment).applyRule();
        new TurnOverCardPilesRule(_actionsEnvironment).applyRule();
    }
}
