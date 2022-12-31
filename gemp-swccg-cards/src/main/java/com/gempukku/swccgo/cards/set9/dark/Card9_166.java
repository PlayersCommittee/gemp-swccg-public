package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.InBombingRunBattleCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CollapsedSiteResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Scimitar 1
 */
public class Card9_166 extends AbstractStarfighter {
    public Card9_166() {
        super(Side.DARK, 2, 2, 1, null, 2, null, 4, Title.Scimitar_1, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Scimitar bombing group TIE bomber. Equipped with advanced targeting system to increase damage during planetary bombardment.");
        setGameText("May add 1 pilot. Power +3 during a Bombing Run battle. When proton bombs aboard 'collapse' a site, opponent loses 1 Force for each Rebel just lost.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.SCIMITAR_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TIE_SA);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new InBombingRunBattleCondition(self), 3));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.siteCollapsedBy(game, effectResult, Filters.and(Filters.proton_bombs, Filters.aboardExceptRelatedSites(self)))) {
            Collection<PhysicalCard> cardsLost = ((CollapsedSiteResult) effectResult).getCardsLost();
            int numForce = Filters.filter(cardsLost, game, Filters.Rebel).size();
            if (numForce > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " lose " + numForce + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, numForce));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
