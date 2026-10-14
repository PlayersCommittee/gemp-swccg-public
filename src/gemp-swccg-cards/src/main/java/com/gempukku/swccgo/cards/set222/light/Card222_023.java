package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandOrReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Set: Set 22
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: I'm Ready For Anything
 */
public class Card222_023 extends AbstractUsedOrLostInterrupt {
    public Card222_023() {
        super(Side.LIGHT, 5, "I'm Ready For Anything", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setLore("");
        setGameText("USED: For remainder of turn, each player may only fire one weapon at each Cantina or [Reflections II] site. " +
                "OR If your Mara in battle, add 1 to a just drawn battle destiny (2 if at a [Reflections II] site). " +
                "LOST: Deploy Mara and a lightsaber simultaneously from hand and/or Reserve Deck; reshuffle.");
        addIcons(Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.Mara_Jade))) {

            final int modifierAmount = GameConditions.isDuringBattleAt(game, Filters.and(Filters.icon(Icon.REFLECTIONS_II), Filters.site)) ? 2 : 1;

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add " + modifierAmount + " to destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, modifierAmount));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final PlayInterruptAction action1 = new PlayInterruptAction(game, self, CardSubtype.USED);
        action1.setText("Affect weapons");
        // Allow response(s)
        action1.allowResponses("Prevent players from firing more than one weapon at Cantina or [Ref2] sites for remainder of turn",
                new RespondablePlayCardEffect(action1) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        Filter siteFilter = Filters.or(Filters.Cantina, Filters.and(Filters.site, Filters.icon(Icon.REFLECTIONS_II)));
                        // Perform result(s)
                        // "One weapon" is a weapon identity: that weapon may still fire repeatedly.
                        action1.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action1,
                                        new MayNotBeFiredModifier(self, additionalWeaponsAfterOneFiredAtSite(game, playerId, siteFilter)),
                                        "You may not fire more than one weapon"));
                        action1.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action1,
                                        new MayNotBeFiredModifier(self, additionalWeaponsAfterOneFiredAtSite(game, opponent, siteFilter)),
                                        "Opponent may not fire more than one weapon"));
                    }
                }
        );
        actions.add(action1);

        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            final Filter mara = Filters.and(Filters.your(playerId), Filters.character, Filters.Mara_Jade);

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Deploy Mara with Anakin's Lightsaber");
            // Allow response(s)
            action.allowResponses("Deploy Mara with Anakin's Lightsaber",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ChooseCardFromHandOrReserveDeckEffect(action, playerId, mara, true, false) {
                                        @Override
                                        protected void cardSelected(SwccgGame game, final PhysicalCard firstCardToDeploy) {
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            action.appendEffect(
                                                                    new ChooseCardFromHandOrReserveDeckEffect(action, playerId, Filters.title(Title.Anakins_Lightsaber), true, false) {
                                                                        @Override
                                                                        protected void cardSelected(SwccgGame game, PhysicalCard secondCardToDeploy) {
                                                                            if (GameUtils.getZoneFromZoneTop(firstCardToDeploy.getZone()) == Zone.RESERVE_DECK) {
                                                                                action.appendEffect(
                                                                                        new DeployCardToLocationFromReserveDeckEffect(action, firstCardToDeploy, Filters.any, false, false, true));
                                                                            } else {
                                                                                action.appendEffect(
                                                                                        new DeployCardToLocationFromHandEffect(action, firstCardToDeploy, Filters.any, false, false));
                                                                            }

                                                                            if (GameUtils.getZoneFromZoneTop(secondCardToDeploy.getZone()) == Zone.RESERVE_DECK) {
                                                                                action.appendEffect(
                                                                                        new DeployCardToTargetFromReserveDeckEffect(action, secondCardToDeploy, mara, false, false, true));
                                                                            } else {
                                                                                action.appendEffect(
                                                                                        new DeployCardToTargetFromHandEffect(action, secondCardToDeploy, mara, false, false));
                                                                            }
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    /**
     * Weapons at the site after this player has already fired a different weapon there (since this interrupt resolved).
     * The first weapon fired may still fire repeatedly; other weapons may not.
     */
    static Filter additionalWeaponsAfterOneFiredAtSite(SwccgGame game, final String playerId, final Filter siteFilter) {
        final Set<Integer> alreadyFiredCardIds = new HashSet<Integer>();
        final Set<String> alreadyFiredPermIds = new HashSet<String>();
        for (PhysicalCard fired : game.getModifiersQuerying().getWeaponsFiredInBattleByPlayer(playerId, true)) {
            alreadyFiredCardIds.add(fired.getCardId());
        }
        for (SwccgBuiltInCardBlueprint perm : game.getModifiersQuerying().getPermanentWeaponsFiredInBattleByPlayer(playerId, true)) {
            PhysicalCard permCard = perm.getPhysicalCard(game);
            if (permCard != null) {
                alreadyFiredPermIds.add(permCard.getCardId() + "_" + perm.getBuiltInId());
            }
        }

        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.and(Filters.your(playerId), Filters.at(siteFilter)).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                String allowedId = allowedWeaponIdAtSameSite(gameState, modifiersQuerying, playerId, physicalCard, alreadyFiredCardIds, alreadyFiredPermIds);
                return allowedId != null && !String.valueOf(physicalCard.getCardId()).equals(allowedId);
            }

            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtIn) {
                PhysicalCard permCard = builtIn.getPhysicalCard(gameState.getGame());
                if (permCard == null) {
                    return false;
                }
                if (!Filters.and(Filters.your(playerId), Filters.at(siteFilter)).accepts(gameState, modifiersQuerying, permCard)
                        && !Filters.and(Filters.your(playerId), Filters.at(siteFilter)).accepts(gameState, modifiersQuerying, builtIn)) {
                    return false;
                }
                String allowedId = allowedWeaponIdAtSameSite(gameState, modifiersQuerying, playerId, permCard, alreadyFiredCardIds, alreadyFiredPermIds);
                String thisId = permCard.getCardId() + "_" + builtIn.getBuiltInId();
                return allowedId != null && !thisId.equals(allowedId);
            }
        };
    }

    private static String allowedWeaponIdAtSameSite(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId,
                                                    PhysicalCard candidate, Set<Integer> alreadyFiredCardIds, Set<String> alreadyFiredPermIds) {
        PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, candidate);
        if (location == null) {
            return null;
        }
        for (PhysicalCard fired : modifiersQuerying.getWeaponsFiredInBattleByPlayer(playerId, true)) {
            if (alreadyFiredCardIds.contains(fired.getCardId())) {
                continue;
            }
            PhysicalCard firedAt = modifiersQuerying.getLocationThatCardIsAt(gameState, fired);
            if (firedAt != null && firedAt.getCardId() == location.getCardId()) {
                return String.valueOf(fired.getCardId());
            }
        }
        for (SwccgBuiltInCardBlueprint perm : modifiersQuerying.getPermanentWeaponsFiredInBattleByPlayer(playerId, true)) {
            PhysicalCard permCard = perm.getPhysicalCard(gameState.getGame());
            if (permCard == null) {
                continue;
            }
            String permId = permCard.getCardId() + "_" + perm.getBuiltInId();
            if (alreadyFiredPermIds.contains(permId)) {
                continue;
            }
            PhysicalCard firedAt = modifiersQuerying.getLocationThatCardIsAt(gameState, permCard);
            if (firedAt != null && firedAt.getCardId() == location.getCardId()) {
                return permId;
            }
        }
        return null;
    }
}
