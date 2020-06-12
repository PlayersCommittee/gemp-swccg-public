package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Starship
 * Subtype: Capital
 * Title: Tantive IV, Consular Ship
 */
public class Card501_038 extends AbstractCapitalStarship {
    public Card501_038() {
        super(Side.LIGHT, 1, 4, 5, 5, null, 3, 7, "Tantive IV, Consular Ship", Uniqueness.UNIQUE);
        setLore("Royal House of Alderaan consular ship. Used by Princess Leia for Imperial Senate business (and secret Reb");
        setGameText("May add 2 pilots and 4 passengers. Permanent pilot provides ability of 2. During Battle, power +2 while a senator aboard, and once per game may place a senator aboard in used pile to exclude one leader(or droid) here from battle.");
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_13);
        addModelType(ModelType.CORELLIAN_CORVETTE);
        addPersona(Persona.TANTIVE_IV);
        setPilotCapacity(2);
        setPassengerCapacity(4);
        setTestingText("Tantive IV, Consular Ship");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AndCondition(new DuringBattleCondition(), new HasAboardCondition(self, Filters.senator)), 2));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TANTIVE_IV_CONSULAR_SHIP__EXCLUDE_LEADER_FROM_BATTLE;
        if(GameConditions.isInBattle(game, self)
                && GameConditions.hasAboard(game, self, Filters.senator)
                && GameConditions.canTarget(game, self, Filters.and(Filters.opponents(playerId), Filters.inBattleWith(self), Filters.or(Filters.leader, Filters.droid)))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)){
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Exclude character");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a senator aboard",  Filters.and(Filters.senator, Filters.aboard(self))) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard senator) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's leader or droid here to exclude", Filters.and(Filters.opponents(playerId), Filters.inBattleWith(self), Filters.or(Filters.leader, Filters.droid))) {
                                        @Override
                                        protected void cardTargeted(int targetGroupId, PhysicalCard opponentsCharacter) {
                                            action.appendEffect(
                                                    new PlaceCardInUsedPileFromTableEffect(action, senator)
                                            );
                                            action.appendEffect(
                                                    new ExcludeFromBattleEffect(action, opponentsCharacter)
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}