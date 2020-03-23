package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Character
 * Subtype: Droid
 * Title: R2-D2 (Artoo-Detoo) (V)
 */
public class Card201_006 extends AbstractDroid {
    public Card201_006() {
        super(Side.LIGHT, 2, 1, 1, 4, "R2-D2 (Artoo-Detoo)", Uniqueness.UNIQUE);
        setAlternateDestiny(5);
        setVirtualSuffix(true);
        setLore("Fiesty. Loyal. Heroic. Insecure. Rebel spy. Excels at trouble. Incorrigible counterpart of a mindless philosopher. Has picked up a slight flutter. A bit eccentric.");
        setGameText("While aboard a starfighter, adds 2 to power, maneuver, and hyperspeed. While with a Scomp link, adds one [Light Side] icon here, and, once per game, if about to be lost, may place in Used Pile. Immune to Fire Extinguisher and Restraining Bolt.");
        addPersona(Persona.R2D2);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starfighterAboard = Filters.and(Filters.starfighter, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.sameLocation(self), new WithCondition(self, Icon.SCOMP_LINK), Icon.LIGHT_FORCE, 1));
        modifiers.add(new PowerModifier(self, starfighterAboard, 2));
        modifiers.add(new ManeuverModifier(self, starfighterAboard, 2));
        modifiers.add(new HyperspeedModifier(self, starfighterAboard, 2));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Fire_Extinguisher));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.R2_D2_V__GOES_TO_USED_WHEN_LOST;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isAtScompLink(game, self)
                && (TriggerConditions.isAboutToBeLost(game, effectResult, self)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, self))) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardAboutToLeaveTable();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile when forfeited");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, cardToBeLost));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
