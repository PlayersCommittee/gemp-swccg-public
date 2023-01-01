package com.gempukku.swccgo.cards.set106.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.InPlayDataSetCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Character
 * Subtype: Rebel
 * Title: Rebel Trooper Recruit
 */
public class Card106_006 extends AbstractRebel {
    public Card106_006() {
        super(Side.LIGHT, 3, 1, 1, 1, 1, "Rebel Trooper Recruit", Uniqueness.UNRESTRICTED, ExpansionSet.OTSD, Rarity.PM);
        setLore("New Rebel troopers gain invaluable experience while acting as support for seasoned troops. The Alliance prefers to assign rookies to soldiers on a one-to-one basis.");
        setGameText("Deploys free to same site as an Rebel leader. Adds 1 to power of one non-unique Rebel warrior present. When forfeited at same site as a Rebel 'veteran' (a leader or non-cadet trooper), also satisfies all remaining attrition against you.");
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.TROOPER, Keyword.RECRUIT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameSiteAs(self, Filters.Rebel_leader)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.isInCardInPlayData(self), new InPlayDataSetCondition(self), 1));
        modifiers.add(new SatisfiesAllAttritionWhenForfeitedModifier(self, new AtSameSiteAsCondition(self, Filters.Rebel_veteran)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            final Filter filter = Filters.and(Filters.non_unique, Filters.Rebel, Filters.warrior, Filters.present(self));
            PhysicalCard currentRebel = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
            if (currentRebel == null || !filter.accepts(game.getGameState(), game.getModifiersQuerying(), currentRebel)) {
                self.setWhileInPlayData(null);
                Collection<PhysicalCard> otherRebels = Filters.filterActive(game, self, filter);
                if (!otherRebels.isEmpty()) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setSingletonTrigger(true);
                    action.setText("Add 1 to a Rebel's power");
                    // Perform result(s)
                    action.appendEffect(
                            new ChooseCardOnTableEffect(action, self.getOwner(), "Choose Rebel", Filters.in(otherRebels)) {
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
