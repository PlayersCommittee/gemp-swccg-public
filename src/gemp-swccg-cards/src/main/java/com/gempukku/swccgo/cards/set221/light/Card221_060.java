package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Galactic Republic Navy
 */
public class Card221_060 extends AbstractNormalEffect {
    public Card221_060() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Galactic Republic Navy", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setGameText("Deploy on table. Unique (•) [Clone Army] capital starships are deploy -1 and immunity to attrition +1. Once per turn, may reveal a [Clone Army] starship from hand to take an [Episode I] pilot character from Reserve Deck (or vice versa) and deploy both simultaneously; reshuffle. [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.unique, Icon.CLONE_ARMY, Filters.capital_starship), -1));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.unique, Icon.CLONE_ARMY, Filters.capital_starship), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        final Filter starship = Filters.and(Icon.CLONE_ARMY, Filters.starship);
        final Filter pilot = Filters.and(Icon.EPISODE_I, Filters.pilot);
        Filter filter = Filters.and(Filters.or(pilot, starship), Filters.isUniquenessOnTableNotReached);

        GameTextActionId gameTextActionId = GameTextActionId.GALACTIC_REPUBLIC_NAVY__DEPLOY_CLONE_ARMY_STARSHIP_AND_PILOT;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal starship or pilot from hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (pilot.accepts(game, selectedCard)) {
                                action.setActionMsg("Take a [Clone Army] starship from Reserve Deck and deploy both simultaneously");
                                searchFilter = starship;
                            }
                            else {
                                action.setActionMsg("Take an [Episode I] pilot from Reserve Deck and deploy both simultaneously");
                                searchFilter = pilot;
                            }
                            // Perform result(s)
                            action.appendEffect(
                                    new SendMessageEffect(action, playerId + " reveals " + GameUtils.getCardLink(selectedCard) + " with " + GameUtils.getCardLink(self)));
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}