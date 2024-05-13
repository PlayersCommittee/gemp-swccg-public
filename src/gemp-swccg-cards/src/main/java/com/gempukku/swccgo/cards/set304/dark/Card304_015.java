package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.InPlayDataSetCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.SatisfiesAllAttritionWhenForfeitedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Doc
 */
public class Card304_015 extends AbstractAlien {
    public Card304_015() {
        super(Side.DARK, 3, 2, 1, 1, 2, "Doc", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("");
        setGameText("Deploys free to same site as Thran. Once per turn, may target squadmate (Thran or Thran's personal guard); target is power +1 for remainder of turn. When forfeited at same site as a squadmate, also satisfies all remaining attrition against you.");
        addKeywords(Keyword.THRAN_GUARD, Keyword.MALE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameSiteAs(self, Filters.Thran)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.isInCardInPlayData(self), new InPlayDataSetCondition(self), 1));
        modifiers.add(new SatisfiesAllAttritionWhenForfeitedModifier(self, new AtSameSiteAsCondition(self, Filters.or(Filters.Thran, Filters.THRAN_GUARD))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            final Filter filter = Filters.and(Filters.or(Filters.Thran, Filters.THRAN_GUARD), Filters.present(self));
            PhysicalCard currentRebel = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
            if (currentRebel == null || !filter.accepts(game.getGameState(), game.getModifiersQuerying(), currentRebel)) {
                self.setWhileInPlayData(null);
                Collection<PhysicalCard> otherRebels = Filters.filterActive(game, self, filter);
                if (!otherRebels.isEmpty()) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setSingletonTrigger(true);
                    action.setText("Add 1 to an Squadmate's power");
                    // Perform result(s)
                    action.appendEffect(
                            new ChooseCardOnTableEffect(action, self.getOwner(), "Choose Squadmate", Filters.in(otherRebels)) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    action.addAnimationGroup(selectedCard);
                                    action.setActionMsg(null);
                                    self.setWhileInPlayData(new WhileInPlayData(selectedCard));
                                    game.getGameState().sendMessage(GameUtils.getCardLink(self) + " is now adding 1 to " + GameUtils.getCardLink(selectedCard) + "'s power");
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
