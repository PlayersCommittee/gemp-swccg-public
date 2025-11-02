package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.IsOnlyExcludedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.NotUniqueModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Kalit
 */
public class Card6_022 extends AbstractAlien {
    public Card6_022() {
        super(Side.LIGHT, 2, 3, 2, 2, 3, Title.Kalit, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Jawa leader. Seeking to peacefully settle a long-standing disagreement with his rival, Wittin. Wants Jabba to mediate their talks.");
        setGameText("Deploys only on Tatooine. Your Jawa Siesta is not unique(•), is doubled, deploys free (or for 6 Force from each player) and cummulatively affects your Jawas' forfeit. While at Audience Chamber or Jawa Camp, all your other Jawas are power +2.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.JAWA);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, playerId, Filters.Jawa_Siesta)
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
        Filter jawaSiesta = Filters.and(Filters.your(self), Filters.Jawa_Siesta);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NotUniqueModifier(self, jawaSiesta));
        modifiers.add(new ModifyGameTextModifier(self, jawaSiesta, ModifyGameTextType.JAWA_SIESTA__DOUBLED_BY_KALIT));
        modifiers.add(new PowerModifier(self, yourOtherJawas, atAudienceChamberOrJawaCamp, 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        //Excluded From Battle - special rules exception:
        //"being excluded will not cause ... other cards to be canceled or otherwise removed from table"
        Filter jawaSiesta = Filters.and(Filters.your(self), Filters.Jawa_Siesta);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        //prevent Jawa Siesta from being removed from table
        modifiers.add(new NotUniqueModifier(self, jawaSiesta, new IsOnlyExcludedCondition(self)));
        return modifiers;
    }
}