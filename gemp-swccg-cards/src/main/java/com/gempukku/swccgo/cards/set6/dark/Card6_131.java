package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Weequay Hunter
 */
public class Card6_131 extends AbstractAlien {
    public Card6_131() {
        super(Side.DARK, 3, 4, 3, 1, 2, "Weequay Hunter", Uniqueness.RESTRICTED_3, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("To maintain religious practices, Weequay hunters capture banthas. The beasts are then sacrificed as part of a battle ceremony. Tusken Raiders tend not to appreciate this.");
        setGameText("Deploys only on Tatooine. When present at the start of a battle, may sacrifice (lose) one of your Banthas present: adds 2 to power of each Weequay there for remainder of turn. (May not sacrifice if Tusken Raiders present outnumber Weequay present).");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.WEEQUAY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.your(self), Filters.bantha);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.wherePresent(self))
                && GameConditions.canTarget(game, self, TargetingReason.TO_BE_LOST, targetFilter)) {
            int numTuskenRaiders = Filters.countActive(game, self, Filters.and(Filters.Tusken_Raider, Filters.present(self)));
            int numWeequay = Filters.countActive(game, self, Filters.and(Filters.Weequay, Filters.present(self)));
            if (numTuskenRaiders <= numWeequay) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Sacrifice a bantha");
                // Pay cost(s)
                action.appendCost(
                        new ChooseCardToLoseFromTableEffect(action, playerId, true, targetFilter));
                // Perform result(s)
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                Collection<PhysicalCard> weequays = Filters.filterActive(game, self, Filters.and(Filters.Weequay, Filters.here(self)));
                                for (PhysicalCard weequay : weequays) {
                                    action.appendEffect(
                                            new ModifyPowerUntilEndOfTurnEffect(action, weequay, 2));
                                }
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
