package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttackEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Great Pit Of Carkoon
 */
public class Card6_170 extends AbstractSite {
    public Card6_170() {
        super(Side.DARK, Title.Great_Pit_Of_Carkoon, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLocationDarkSideGameText("During your control phase, may cause Sarlacc to immediately attack one captive present.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.JABBAS_PALACE, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.PIT);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, Phase.CONTROL)
                && !GameConditions.isDuringAttack(game)
                && !GameConditions.isDuringBattle(game)) {
            final PhysicalCard sarlacc = Filters.findFirstActive(game, self, Filters.Sarlacc);
            if (sarlacc != null) {
                Filter captiveFilter = Filters.and(Filters.captive, Filters.present(self), Filters.nonCreatureCanBeAttackedByCreature(sarlacc, true));
                if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, captiveFilter)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
                    action.setText("Make Sarlacc attack captive");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerOnDarkSideOfLocation, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, captiveFilter) {
                                @Override
                                protected void cardTargeted(int targetGroupId, final PhysicalCard captive) {
                                    action.addAnimationGroup(sarlacc);
                                    action.addAnimationGroup(captive);
                                    // Allow response(s)
                                    action.allowResponses("Make " + GameUtils.getCardLink(sarlacc) + " attack " + GameUtils.getCardLink(captive),
                                            new UnrespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new AttackEffect(action, captive, sarlacc));
                                                }
                                            }
                                    );
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