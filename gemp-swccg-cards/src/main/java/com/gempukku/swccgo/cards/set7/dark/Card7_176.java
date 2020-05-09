package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.PeekAtAndReorderTopCardsOForcePileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Dodo Bodonawieedo
 */
public class Card7_176 extends AbstractAlien {
    public Card7_176() {
        super(Side.DARK, 3, 2, 2, 1, 2, "Dodo Bodonawieedo", Uniqueness.UNIQUE);
        setLore("Rodian Musician. Plays slitherhorn. Grew up in the streets of Mos Eisley. 'Frocked' jawas and stole ronto steaks as a child. Information broker. Was part of the Shawpee gang.");
        setGameText("Power +2 at Mos Eisley. Opponent's Jawas are power -1 here. Once during each of your control phases, may peak at top X cards of your Force Pile, reorder however you wish and replace, where X = number of other musicians at same site.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.MUSICIAN, Keyword.INFORMATION_BROKER);
        setSpecies(Species.RODIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, self, new AtCondition(self, Filters.Mos_Eisley), 2));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.opponents(self.getOwner()), Filters.Jawa, Filters.here(self)), -1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOnceDuringPlayersPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, playerId, Phase.CONTROL)
                && GameConditions.hasForcePile(game, playerId)) {
            int numOtherMusiciansAtSameSite = Filters.countActive(game, self, Filters.and(Filters.not(self), Filters.atSameSite(self), Filters.musician));
            if (numOtherMusiciansAtSameSite > 0) {
                TopLevelGameTextAction action = new TopLevelGameTextAction();
                action.setText("Peak at top " + numOtherMusiciansAtSameSite + " cards.");
                action.setActionMsg("Peak at top " + numOtherMusiciansAtSameSite + " cards.");
                action.appendUsage(new OncePerPhaseEffect(action));
                action.appendEffect(new PeekAtAndReorderTopCardsOForcePileEffect(action, playerId, numOtherMusiciansAtSameSite));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
