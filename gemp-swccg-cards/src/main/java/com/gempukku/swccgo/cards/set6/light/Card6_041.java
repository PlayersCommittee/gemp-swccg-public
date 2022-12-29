package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.UnderNighttimeConditionConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Snivvian
 */
public class Card6_041 extends AbstractAlien {
    public Card6_041() {
        super(Side.LIGHT, 2, 2, 1, 1, 2, "Snivvian", Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Cadomai is the homeworld of the generally artistic Snivvians. Its cold, dark climate makes them excellent scouts.");
        setGameText("May deploy as a 'react'. Power +2 under 'nighttime conditions'. When at a site, if opponent cancels Nightfall on related system, causes Effect to go to Used Pile and may retrieve 2 Force.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.SNIVVIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new UnderNighttimeConditionConditions(self), 2));
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self, Filters.and(Filters.Nightfall, Filters.attachedTo(Filters.relatedSystem(self))),
                new AtCondition(self, Filters.site), game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justCanceledFromAttachedTo(game, effectResult, playerId, Filters.Nightfall, Filters.relatedSystem(self))
                && GameConditions.isAtLocation(game, self, Filters.site)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
