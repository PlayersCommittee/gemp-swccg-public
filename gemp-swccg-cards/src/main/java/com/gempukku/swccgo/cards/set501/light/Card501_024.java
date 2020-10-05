package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.TakeFirstBattleWeaponsSegmentActionEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
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
public class Card501_024 extends AbstractAlien {
    public Card501_024() {
        super(Side.LIGHT, 0, 3, 4, 3, 5, "Tobias Beckett", Uniqueness.UNIQUE);
        setLore("Glee Anselmian smuggler. Information broker, musician, and thief.");
        setGameText("Destiny +3 if Val, Rio, Vos, or v13 Han on table. Aurra Sing’s game text is canceled here. When lost may place out of play (for remainder of game, Han adds one battle destiny). If armed with a blaster at a site, you may take the first weapons phase action.");
        addPersona(Persona.BECKETT);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.MUSICIAN, Keyword.THIEF, Keyword.INFORMATION_BROKER);
        setSpecies(Species.GLEE_ANSELMIAN);
        setTestingText("Tobias Beckett");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DestinyModifier(self, self, new OnTableCondition(self, Filters.or(Filters.Val, Filters.Rio, Filters.Vos, Filters.and(Filters.icon(Icon.VIRTUAL_SET_13), Filters.Han))), 3));
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
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.isPresentAt(game, self, Filters.site)
                && GameConditions.isArmedWith(game, self, Filters.blaster)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take first weapons phase action");
            action.setActionMsg("Take first weapons phase action");
            // Perform result(s)
            action.appendEffect(
                    new TakeFirstBattleWeaponsSegmentActionEffect(action, playerId));
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
