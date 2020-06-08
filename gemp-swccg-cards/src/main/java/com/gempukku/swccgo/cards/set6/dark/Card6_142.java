package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Subtype: Immediate
 * Title: Cane Adiss
 */
public class Card6_142 extends AbstractImmediateEffect {
    public Card6_142() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Cane Adiss");
        setLore("Adventurous pilot. Boasts to Jabba that he has traveled to every uncharted planet in the galaxy. Has been hired by the Empire to keep an eye out for Rebel activity.");
        setGameText("If opponent just initiated a Force drain at a non-shielded planet location, deploy on that location. Your characters, vehicles and starships may deploy here regardless of presence and location deployment restrictions. (Immune to Control.)");
        addIcons(Icon.JABBAS_PALACE);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.and(Filters.planet_location, Filters.not(Filters.shielded_location)))) {
            PhysicalCard forceDrainLocation = game.getGameState().getForceDrainState().getLocation();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameLocationId(forceDrainLocation), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.vehicle, Filters.starship)), Filters.here(self)));
        return modifiers;
    }
}