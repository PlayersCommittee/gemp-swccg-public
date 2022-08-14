package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Imperial
 * Title: Maarek Stele, The Emperor's Reach
 */
public class Card200_085 extends AbstractImperial {
    public Card200_085() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Maarek Stele, The Emperor's Reach", Uniqueness.UNIQUE);
        setLore("Leader");
        setGameText("[Pilot] 3. Once per game may [upload] a TIE Defender. While piloting, opponent may not cancel or substitute battle destiny draws here. While piloting an Imperial starfighter, it is maneuver +2 and he draws on battle destiny if unable to otherwise.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_0);
        addPersona(Persona.MAAREK_STELE);
        addKeyword(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition whilePiloting = new PilotingCondition(self);
        Condition whilePilotingImperialStarfighter = new PilotingCondition(self, Filters.and(Filters.Imperial_starship, Filters.starfighter));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.here(self), whilePiloting, opponent));
        modifiers.add(new MayNotSubstituteBattleDestinyModifier(self, Filters.here(self), whilePiloting, opponent));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), whilePilotingImperialStarfighter, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, whilePilotingImperialStarfighter, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MAAREK_STELE_THE_EMPERORS_REACH__UPLOAD_TIE_DEFENDER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take TIE Defender into hand from Reserve Deck");
            action.setActionMsg("Take a TIE Defender into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.TIE_Defender, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
