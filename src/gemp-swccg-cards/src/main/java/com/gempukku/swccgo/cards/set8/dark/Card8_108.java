package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceRetrievalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Navy Trooper Fenson
 */
public class Card8_108 extends AbstractImperial {
    public Card8_108() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Navy Trooper Fenson", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Rendili native skilled at close-quarters combat. Worked with Trooper Vesden to monitor enemy activity on Endor.");
        setGameText("Subtracts 3 from Force opponent retrieves for On The Edge and Off The Edge. When at Bunker, adds 2 to deploy cost of each opponent's character (except Ewoks, spies and scouts) to Endor sites (except Rebel Landing Site).");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition atBunker = new AtCondition(self, Filters.Bunker);

        List<Modifier> modifiers = new LinkedList<Modifier>();

        modifiers.add(new DeployCostToLocationModifier(self, 
                		Filters.and(Filters.opponents(self), Filters.character, Filters.except(Filters.or(Filters.Ewok,Filters.spy,Filters.scout))),
                		atBunker, 
                		2, 
                		Filters.and(Filters.Endor_site, Filters.except(Filters.Rebel_Landing_Site))
        			));
                
        modifiers.add(new ForceRetrievalModifier(self, new CardMatchesEvaluator(0, -3, Filters.title("On The Edge")), opponent));
        modifiers.add(new ForceRetrievalModifier(self, new CardMatchesEvaluator(0, -3, Filters.title("Off The Edge")), opponent));

        return modifiers;
    }
}
