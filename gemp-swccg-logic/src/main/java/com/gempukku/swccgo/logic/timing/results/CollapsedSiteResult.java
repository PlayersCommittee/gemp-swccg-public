package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;

/**
 * This effect result is triggered when a site is 'collapsed'.
 */
public class CollapsedSiteResult extends EffectResult {
    private PhysicalCard _collapsedBy;
    private PhysicalCard _siteCollapsed;
    private Collection<PhysicalCard> _cardsLost;

    /**
     * Creates an effect result that is triggered when a site is 'collapsed'.
     * @param performingPlayerId the performing player
     * @param collapsedBy the card that 'collapsed' the site
     * @param site the site
     * @param cardsLost the cards lost when site is 'collapsed'
     */
    public CollapsedSiteResult(String performingPlayerId, PhysicalCard collapsedBy, PhysicalCard site, Collection<PhysicalCard> cardsLost) {
        super(Type.COLLAPSED_SITE, performingPlayerId);
        _collapsedBy = collapsedBy;
        _siteCollapsed = site;
        _cardsLost = cardsLost;
    }

    /**
     * Gets the card that 'collapsed' the site.
     * @return the card
     */
    public PhysicalCard getCollapsedBy() {
        return _collapsedBy;
    }

    /**
     * Gets the site that was 'collapsed'.
     * @return the site
     */
    public PhysicalCard getSiteCollapsed() {
        return _siteCollapsed;
    }

    /**
     * Gets the cards that were lost when site was 'collapsed'.
     * @return the cards lost
     */
    public Collection<PhysicalCard> getCardsLost() {
        return _cardsLost;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Collapsed' " + GameUtils.getCardLink(_siteCollapsed);
    }
}
