package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Admiral's Order
 * Title: Let's Go Left
 */
public class Card14_002 extends AbstractAdmiralsOrder {
    public Card14_002() {
        super(Side.LIGHT, "Let's Go Left");
        setGameText("Non-pilot characters aboard starships are forfeit -4. Whenever a player's starship is 'hit', that player loses 1 Force (2 if starship was hit by Proton Torpedoes). At systems where you have only [Republic] starships, all of those starships are immune to attrition < 4. At sites related to a system you occupy, your battle destinies may not be canceled by opponent.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter republicStarships = Filters.and(Filters.your(playerId), Icon.REPUBLIC, Filters.starship, Filters.at(Filters.and(Filters.system,
                Filters.not(Filters.sameSystemAs(self, Filters.and(Filters.your(playerId), Filters.starship, Filters.not(Icon.REPUBLIC)))))));
        Filter sitesRelatedToSystemsYouOccupy = Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.non_pilot_character, Filters.aboardAnyStarship), -4));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, republicStarships, 4));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, sitesRelatedToSystemsYouOccupy, playerId, opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHit(game, effectResult, Filters.starship)) {
            String owner = ((HitResult) effectResult).getCardHit().getOwner();
            int forceToLose = TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.Proton_Torpedoes) ? 2 : 1;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + owner + " lose " + forceToLose + " Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, owner, forceToLose));
            return Collections.singletonList(action);
        }
        return null;
    }
}
