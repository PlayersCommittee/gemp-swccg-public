package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Tobias Beckett
 */
public class Card213_042 extends AbstractAlien {
    public Card213_042() {
        super(Side.LIGHT, 0, 3, 4, 3, 5, "Tobias Beckett", Uniqueness.UNIQUE);
        setLore("Glee Anselmian smuggler. Information broker, musician, and thief.");
        setGameText("If Vos or a [Set 13] smuggler on table, destiny +3 when drawn for destiny. Cancels Aurra's game text here. If just lost, may place out of play (for remainder of game, Han adds one battle destiny). If opponent just initiated a battle here, Beckett may fire a blaster.");
        addPersona(Persona.BECKETT);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.MUSICIAN, Keyword.THIEF, Keyword.INFORMATION_BROKER);
        setSpecies(Species.GLEE_ANSELMIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DestinyModifier(self, self, new OnTableCondition(self, Filters.or(Filters.Vos, Filters.and(Filters.icon(Icon.VIRTUAL_SET_13), Filters.smuggler))), 3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.Aurra, Filters.here(self))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, game.getOpponent(self.getOwner()), Filters.here(self))
                && GameConditions.isPresentAt(game, self, Filters.site)
                && GameConditions.isArmedWith(game, self, Filters.blaster)
        ) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Fire a blaster");
            action.setActionMsg("Fire a blaster");
            Filter weaponFilter = Filters.and(Filters.weapon, Filters.attachedTo(self), Filters.blaster, Filters.canBeFired(self, 0));
            // Perform result(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose weapon to fire", weaponFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard weapon) {
                            action.addAnimationGroup(weapon);
                            // Allow response(s)
                            action.allowResponses("Fire " + GameUtils.getCardLink(weapon),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new FireWeaponEffect(action, weapon, true, Filters.any));
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

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, self)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, self, false));
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action,
                            new AddsBattleDestinyModifier(self, new DuringBattleWithParticipantCondition(Filters.Han), 1, playerId, true), null)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
