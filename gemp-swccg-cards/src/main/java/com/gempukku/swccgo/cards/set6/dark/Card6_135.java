package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Wittin
 */
public class Card6_135 extends AbstractAlien {
    public Card6_135() {
        super(Side.DARK, 2, 3, 2, 2, 3, Title.Wittin, Uniqueness.UNIQUE);
        setLore("Male Jawa. Leader of a large tribe of Jawas. Plotting with Jabba to take control of a neighboring tribe's territory.");
        setGameText("Deploys only on Tatooine. Your Jawa Pack is not unique(•), is doubled, deploys free (or for 6 Force from each player) and cummulatively affects your Jawas' forfeit. While at Audience Chamber or Jawa Camp, all your other Jawas are power +2.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.MALE);
        setSpecies(Species.JAWA);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, playerId, Filters.Jawa_Pack)
                && GameConditions.canUseForce(game, playerId, 6)
                && GameConditions.canUseForce(game, game.getOpponent(playerId), 6)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new YesNoDecision("Deploy for 6 force from each player?") {
                                @Override
                                protected void yes() {
                                    action.appendEffect(
                                            new UseForceEffect(action, playerId, 6)
                                    );
                                    action.appendEffect(
                                            new UseForceEffect(action, game.getOpponent(playerId), 6)
                                    );
                                }
                            }
                    ));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamberOrJawaCamp = new AtCondition(self, Filters.or(Filters.Jawa_Camp, Filters.Audience_Chamber));
        Filter yourOtherJawas = Filters.and(Filters.your(self), Filters.other(self), Filters.Jawa);
        Filter jawaPack = Filters.Jawa_Pack;

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NotUniqueModifier(self, jawaPack));
        modifiers.add(new ModifyGameTextModifier(self, jawaPack, ModifyGameTextType.JAWA_PACK__DOUBLED_BY_WITTIN));
        modifiers.add(new PowerModifier(self, yourOtherJawas, atAudienceChamberOrJawaCamp, 2));
        return modifiers;
    }
}
