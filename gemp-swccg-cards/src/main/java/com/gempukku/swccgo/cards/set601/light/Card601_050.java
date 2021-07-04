package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataNotSetCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Character
 * Subtype: Alien
 * Title: Lando Calrissian, Unlikely Hero
 */
public class Card601_050 extends AbstractAlien {
    public Card601_050() {
        super(Side.LIGHT, 3, 3, 3, 3, 5, "Lando Calrissian, Unlikely Hero", Uniqueness.UNIQUE);
        setLore("Ever since Lando's 'little' maneuver at the Battle of Taanab, his piloting skills had become legendary. Gambler. Scoundrel.");
        setGameText("Adds 3 to anything he pilots. May reveal from hand to take Lady Luck into hand from Reserve Deck; reshuffle; and deploy both simultaneously. While piloting (or with a female), adds one battle destiny. During battle, may draw destiny. If destiny < twice the number of scoundrels here, reset a participating card's forfeit to 0.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_8, Icon.CLOUD_CITY);
        addKeywords(Keyword.GAMBLER, Keyword.SCOUNDREL);
        addPersona(Persona.LANDO);
        setMatchingStarshipFilter(Filters.title("Lady Luck"));
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition condition = new InPlayDataNotSetCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new OrCondition(new PilotingCondition(self), new WithCondition(self, Filters.female)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LANDO__UPLOAD_AND_DEPLOY_LADY_LUCK;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, "Lady Luck")) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to deploy with Lady Luck");
            action.setActionMsg("Reveal to deploy with Lady Luck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, self, Filters.title("Lady Luck"), true));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId2)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.participatingInBattle, Filters.forfeitMayBeReduced))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);
            action.setText("Draw destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(new DrawDestinyEffect(action, playerId) {
                @Override
                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                    if (totalDestiny != null) {
                        int scoundrelCount = Filters.countActive(game, self, Filters.and(Filters.participatingInBattle, Filters.scoundrel, Filters.here(self)));
                        if (totalDestiny < 2*scoundrelCount) {
                            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target card to reset forfeit to 0", Filters.and(Filters.participatingInBattle, Filters.forfeitMayBeReduced)) {
                                @Override
                                protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                                    action.appendEffect(new ResetForfeitEffect(action, targetedCard, 0));
                                }
                            });
                        }
                    }
                }
            });
            Collections.singletonList(action);
        }

        return null;
    }
}