package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Cloud City: Interrogation Room
 */
public class Card7_271 extends AbstractSite {
    public Card7_271() {
        super(Side.DARK, "Cloud City: Interrogation Room", Title.Bespin);
        setLocationDarkSideGameText("For each captive present, Force drain +1 (+1 more if captive is a unique Rebel).");
        setLocationLightSideGameText("Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
        addKeywords(Keyword.CLOUD_CITY_LOCATION);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, final SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        final int permCardId = self.getPermanentCardId();
        modifiers.add(new ForceDrainModifier(self,
                new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                        PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                        Collection<PhysicalCard> captivesPresent = Filters.filterActive(game, self,
                                SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captive, Filters.present(self)));
                        int bonus = 0;
                        for (PhysicalCard captivePresent : captivesPresent) {
                            bonus++;
                            if (Filters.and(Filters.unique, Filters.Rebel).accepts(gameState, modifiersQuerying, captivePresent)) {
                                bonus++;
                            }
                        }
                        return bonus;
                    }
                }, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, 1, playerOnLightSideOfLocation));
        return modifiers;
    }
}