package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Alien
 * Title: Dexter Jettster
 */
public class Card219_032 extends AbstractAlien {
    public Card219_032() {
        super(Side.LIGHT, 2, 3, 3, 3, 5, "Dexter Jettster", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLore("Besalisk. Information broker");
        setGameText("Deploys -1 to (and power +1 at) a cantina, diner, or night club. " +
                    "When deployed, may take a Jedi here into hand (if a bounty hunter on table, may instead [upload] Obi-Wan). " +
                    "Your aliens here are immune to Stunning Leader.");
        addKeywords(Keyword.INFORMATION_BROKER);
        setSpecies(Species.BESALISK);
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_19);
    }

    @Override
    public List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter venueFilter = Filters.or(Filters.titleContains("cantina"), Filters.titleContains("diner"), Filters.titleContains("night club"));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, venueFilter));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter venueFilter = Filters.or(Filters.titleContains("cantina"), Filters.titleContains("diner"), Filters.titleContains("night club"));
        Filter yourAliensHere = Filters.and(Filters.your(self), Filters.here(self), Filters.alien);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, venueFilter), 1));
        modifiers.add(new ImmuneToTitleModifier(self, yourAliensHere, Title.Stunning_Leader));

        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.DEXTER_JETTSTER__TAKE_JEDI_INTO_HAND_OR_UPLOAD_OBI;
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            Filter filter = Filters.and(Filters.Jedi, Filters.here(self));
            if (GameConditions.canTarget(game, self, filter)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take a Jedi into hand");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target a Jedi to be taken into hand", filter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Take " + GameUtils.getCardLink(targetedCard) + " into hand",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ReturnCardToHandFromTableEffect(action, targetedCard));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }

            if (GameConditions.canSpot(game, self, Filters.bounty_hunter)
                    && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take Obi-Wan into hand from Reserve Deck");
                action.setActionMsg("Take Obi-Wan into hand from Reserve Deck");

                action.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.ObiWan, true));
                actions.add(action);

            }
        }
        return actions;

    }
}
