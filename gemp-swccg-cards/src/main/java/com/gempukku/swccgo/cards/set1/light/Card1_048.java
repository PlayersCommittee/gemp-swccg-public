package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DisarmCharacterEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCarryModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Disarmed
 */
public class Card1_048 extends AbstractNormalEffect {
    public Card1_048() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Disarmed);
        setLore("When Dr. Evazan and Ponda Baba confronted Luke in the Cantina, Obi-Wan pointed out, 'This little one isn't worth the effort.' A brawl ensued.");
        setGameText("If both players have a character with a weapon present at same site, deploy on that opponent's character during any control phase. Character loses all weapons, is power -1 and may no longer carry weapons. (Immune to Alter.)");
        addKeywords(Keyword.DISARMING_CARD);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringEitherPlayersPhase(game, Phase.CONTROL);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.character_with_a_weapon, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DISARMED),
                Filters.presentAt(Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.character_with_a_weapon))));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard character = self.getAttachedTo();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            // Perform result(s)
            action.setText("Disarm " + GameUtils.getFullName(character));
            action.setActionMsg("Disarm " + GameUtils.getCardLink(character));
            // Perform result(s)
            action.appendEffect(
                    new DisarmCharacterEffect(action, character, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, hasAttached, -1));
        modifiers.add(new MayNotCarryModifier(self, hasAttached, Filters.weapon));
        return modifiers;
    }
}