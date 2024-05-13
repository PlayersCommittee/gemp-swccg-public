package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Dark Jedi Master/Imperial
 * Title: The Emperor (V)
 */
public class Card219_019 extends AbstractDarkJediMasterImperial {
    public Card219_019() {
        super(Side.DARK, 1, 5, 4, 7, 9, "The Emperor", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leader. Secretive manipulator of the galaxy. Played Darth Vader and Prince Xizor off against one another in his relentless pursuit of 'young Skywalker'.");
        setGameText("Destiny +1 for each Black Sun agent or Jedi on table (limit +5) when drawn for destiny. " +
                    "Deploys only to Coruscant. Never deploys or moves (even if carried) to a site opponent occupies. " +
                    "May cancel Projection Of A Skywalker here. Immune to attrition.");
        addPersona(Persona.SIDIOUS);
        addIcons(Icon.REFLECTIONS_II, Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_19);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_at_Coruscant;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.or(self, Filters.hasAttachedWithRecursiveChecking(self)), siteOpponentOccupies));
        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, self, new MaxLimitEvaluator(new OnTableEvaluator(self, Filters.or(Filters.Black_Sun_agent, Filters.Jedi)), 5)));
        return modifiers;

    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.or(self, Filters.hasAttachedWithRecursiveChecking(self)), siteOpponentOccupies));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.canTarget(game, self, Filters.and(Filters.here(self), Filters.title(Title.Projection_Of_A_Skywalker)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Projection Of A Skywalker here");

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target Projection Of A Skywalker here", Filters.and(Filters.here(self), Filters.title(Title.Projection_Of_A_Skywalker))) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(
                                    new CancelCardOnTableEffect(action, finalTarget));
                        }
                    });
                }
            });
            actions.add(action);
        }

        return actions;
    }
}