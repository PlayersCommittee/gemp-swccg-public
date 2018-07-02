package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Tibanna Gas Miner
 */
public class Card5_102 extends AbstractAlien {
    public Card5_102() {
        super(Side.DARK, 2, 2, 1, 1, 2, Title.Tibanna_Gas_Miner);
        setLore("One of the many Cloud City miners who are willingly employed by greedy corporations exploiting planetary atmospheres. Unconcerned with environmental repercussions.");
        setGameText("When at a site, adds 1 to your Force drains at related cloud sectors. Also, during your activate phase, if at a Cloud City site and you control Bespin: Cloud City, may cumulatively activate 1 Force for every cloud sector on Bespin.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.GAS_MINER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.relatedCloudSector(self), new AtCondition(self, Filters.site), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.ACTIVATE)
                && GameConditions.canActivateForce(game, playerId)
                && GameConditions.isAtLocation(game, self, Filters.Cloud_City_site)
                && GameConditions.controls(game, playerId, Filters.Bespin_Cloud_City)) {
            int numToActivate = Filters.countTopLocationsOnTable(game, Filters.and(Filters.cloud_sector, Filters.partOfSystem(Title.Bespin)));
            if (numToActivate > 0) {
                if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.TIBANNA_GAS_MINER__DOUBLE_FORCE_ACTIVATED)) {
                    numToActivate *= 2;
                }

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Activate " + numToActivate + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ActivateForceEffect(action, playerId, numToActivate));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
