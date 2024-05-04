package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 3
 * Type: Effect
 * Title: Aratech Corporation (V)
 */
public class Card601_191 extends AbstractNormalEffect {
    public Card601_191() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Aratech Corporation", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Aratech Corporation sent support staff to various Imperial outposts and garrisons. Gave advanced briefings and training to biker scout personnel.");
        setGameText("Deploy on table. AT-STs and speeder bikes are power and forfeit +1. During your deploy phase, may reveal an AT-ST or Speeder Bike from hand to [upload] a unique (•) [Endor] Imperial pilot of ability < 3 (or vice versa) and deploy both simultaneously. (Immune to Alter.)");
        addIcons(Icon.ENDOR, Icon.LEGACY_BLOCK_3);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.or(Filters.AT_ST, Filters.speeder_bike);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, filter, 1));
        modifiers.add(new ForfeitModifier(self, filter, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter atstOrSpeederBike = Filters.or(Filters.AT_ST, Filters.speeder_bike);
        Filter filter = Filters.and(Filters.or(Filters.and(Filters.unique, Icon.ENDOR, Filters.Imperial, Filters.pilot, Filters.abilityLessThan(3)), atstOrSpeederBike), Filters.isUniquenessOnTableNotReached);

        GameTextActionId gameTextActionId = GameTextActionId.ARATECH_CORPORATION__DEPLOY_ATST_SPEEDER_BIKE_OR_PILOT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal pilot, AT-ST, or Speeder Bike from hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (Filters.character.accepts(game, selectedCard)) {
                                action.setActionMsg("Take an AT-ST or Speeder Bike from Reserve Deck and deploy both simultaneously");
                                searchFilter = atstOrSpeederBike;
                            }
                            else {
                                action.setActionMsg("Take a unique (•) [Endor] Imperial pilot of ability < 3 from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.and(Filters.unique, Icon.ENDOR, Filters.Imperial, Filters.pilot, Filters.abilityLessThan(3));
                            }
                            // Perform result(s)
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}