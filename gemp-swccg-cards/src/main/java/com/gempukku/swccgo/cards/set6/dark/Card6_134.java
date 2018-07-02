package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.UnderNighttimeConditionConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
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
 * Title: Whiphid
 */
public class Card6_134 extends AbstractAlien {
    public Card6_134() {
        super(Side.DARK, 2, 4, 3, 1, 1, "Whiphid");
        setLore("Whipids originate from Toola in the Kaelta system, a planet extremely distant from its sun. Accustomed to hunting for prey in the near dark and the bitter cold.");
        setGameText("Power +2 on Hoth. Forfeit +2 under nighttime conditions. When at a site, if opponent cancels Sunsdown at the related system, causes Effect to go to Used Pile, and you may retrieve 2 Force.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.WHIPHID);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OnCondition(self, Title.Hoth), 2));
        modifiers.add(new ForfeitModifier(self, new UnderNighttimeConditionConditions(self), 2));
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self, Filters.and(Filters.Sunsdown, Filters.attachedTo(Filters.relatedSystem(self))),
                new AtCondition(self, Filters.site), game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justCanceledFromAttachedTo(game, effectResult, opponent, Filters.Sunsdown, Filters.relatedSystem(self))
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
