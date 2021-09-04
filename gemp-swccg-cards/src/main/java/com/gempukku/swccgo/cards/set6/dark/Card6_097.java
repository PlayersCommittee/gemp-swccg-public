package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Beedo
 */
public class Card6_097 extends AbstractAlien {
    public Card6_097() {
        super(Side.DARK, 3, null, 2, 2, 2, "Beedo", Uniqueness.UNIQUE);
        setLore("Rodian bounty hunter. Relative of Greedo. Taking Greedo's place in Jabba's court. Fearful of Jabba's wrath. Notorious sycophant.");
        setGameText("* Replaces any other male Rodian for free (Rodian goes to the Used Pile) or deploys for 3 Force. While at Audience Chamber, all your Rodians are power +2, and whenever Greedo threatens a smuggler, may add 2 to destiny draw.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.RODIAN);
        addPersona(Persona.BEEDO);
        setReplacementForSquadron(1, Filters.and(Filters.male, Filters.Rodian, Filters.not(Filters.persona(Persona.BEEDO))));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.Rodian), new AtCondition(self, Filters.Audience_Chamber), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isAtLocation(game, self, Filters.Audience_Chamber)
                && TriggerConditions.isDestinyJustDrawnFor(game, effectResult, Filters.Greedo)) {
            if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
                DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
                if (!destinyDrawnResult.isCanceled()
                        && destinyDrawnResult.getDestinyType() == DestinyType.THREATEN_DESTINY) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                    action.setText("Add 2 to 'threaten' destiny");
                    action.appendEffect(new ModifyDestinyEffect(action, 2));

                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
