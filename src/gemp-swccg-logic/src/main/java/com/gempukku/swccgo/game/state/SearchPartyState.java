package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// This class contains the state information for a
// Search party action within a game of Gemp-Swccg.
//
public class SearchPartyState {
    private List<PhysicalCard> _searchParty = new ArrayList<PhysicalCard>();
    private PhysicalCard _location;

    public SearchPartyState(Collection<PhysicalCard> searchParty, PhysicalCard location) {
        _searchParty.addAll(searchParty);
        _location = location;
    }

    public List<PhysicalCard> getSearchParty() {
        return _searchParty;
    }

    public PhysicalCard getLocation() {
        return _location;
    }
}
