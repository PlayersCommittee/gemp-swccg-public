package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Zuckuss (v)
 */
public class Card501_007 extends AbstractAlien {
    public Card501_007() {
        super(Side.DARK, 1, 3, 2, 4, 4, "Zuckuss", Uniqueness.UNIQUE);
        setLore("Male Gand. Practitioner of ancient religious findsman vocation. Bounty hunter and scout. Gains surprisingly accurate information through mystical visions during meditation.");
        setGameText("Adds 2 to power and maneuver of anything he pilots (3 if Mist Hunter). Once per battle if with exactly one other bounty hunter (or if with 4-LOM), may take your just drawn destiny into hand. May move as a ‘react’. Immune to attrition < 3.");
        addPersona(Persona.ZUCKUSS);
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.SCOUT);
        setSpecies(Species.GAND);
        setVirtualSuffix(true);
        setTestingText("Zuckuss (v)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.persona(Persona.MIST_HUNTER))));
        modifiers.add(new ManeuverModifier(self, new CardMatchesEvaluator(2, 3, Filters.persona(Persona.MIST_HUNTER))));
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)
                && (GameConditions.isDuringBattleWithParticipant(game, Filters._4_LOM)
                || Filters.countActive(game, self, Filters.and(Filters.bounty_hunter, Filters.inBattleWith(self))) == 1)
                && GameConditions.isOncePerBattle(game, self, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take destiny card into hand");
            action.setActionMsg("Take just drawn destiny card, " + GameUtils.getCardLink(((DestinyDrawnResult) effectResult).getCard()) + ", into hand");
            // Perform result(s)
            action.appendUsage(
                    new OncePerBattleEffect(action)
            );
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
