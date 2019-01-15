package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;

/**
 * Set: Set 10
 * Type: Objective
 * Title: Ralltiir Operations (V) / In The Hands Of The Empire (V)
 */
public class Card210_042 extends AbstractObjective {
    public Card210_042() {
        super(Side.DARK, 0, Title.Ralltiir_Operations);
        setVirtualSuffix(true);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Ralltiir system. For remainder of game, spaceport sites are immune to He Hasn't Come Back Yet and Ounee Ta. Your Force generation is +1 at each Ralltiir location. Once per battle, when you draw battle destiny, may exchange a card in hand with a card of same card type in Lost Pile. While this side up, once per turn, may deploy from Reserve Deck a site (or non-unique Imperial) to Ralltiir. Flip this card if Imperials control at least three Ralltiir sites and opponent controls no Ralltiir locations.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return deployCardWithObjectiveText(self, Filters.Ralltiir_system, "Ralltiir system");
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(spaceportSitesImmuneToHeHasntComeBackYetForRemainderOfGame(self,action));
        action.appendEffect(spaceportSitesImmuneToOuneeTaForRemainderOfGame(self,action));
        yourForceGenPlusOneAtEachRalltiirLocation(self, game);
        // TODO FROG lost pile swap
        return action;
    }

    // TODO WTSU download

    // TODO Flip condition

    private void yourForceGenPlusOneAtEachRalltiirLocation(PhysicalCard self, SwccgGame game) {
        if (!GameConditions.cardHasAnyForRemainderOfGameDataSet(self)) {
            self.setForRemainderOfGameData(self.getCardId(), new ForRemainderOfGameData());
            game.getModifiersEnvironment().addUntilEndOfGameModifier(
                    new ForceGenerationModifier(self, Filters.Ralltiir_location, 1, self.getOwner()));
        }
    }

    private AddUntilEndOfGameModifierEffect spaceportSitesImmuneToHeHasntComeBackYetForRemainderOfGame(PhysicalCard self, RequiredGameTextTriggerAction action) {
        return new AddUntilEndOfGameModifierEffect(action,
                new ImmuneToTitleModifier(self, Filters.at(Filters.spaceport_site), Title.He_Hasnt_Come_Back_Yet), null);
    }

    private AddUntilEndOfGameModifierEffect spaceportSitesImmuneToOuneeTaForRemainderOfGame(PhysicalCard self, RequiredGameTextTriggerAction action) {
        return new AddUntilEndOfGameModifierEffect(action,
                new ImmuneToTitleModifier(self, Filters.spaceport_site, Title.Ounee_Ta), null);
    }
}
