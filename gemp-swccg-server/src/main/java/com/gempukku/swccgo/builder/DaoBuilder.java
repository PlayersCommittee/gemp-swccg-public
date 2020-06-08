package com.gempukku.swccgo.builder;

import com.gempukku.swccgo.cache.CacheManager;
import com.gempukku.swccgo.collection.CachedCollectionDAO;
import com.gempukku.swccgo.collection.CachedTransferDAO;
import com.gempukku.swccgo.collection.CollectionSerializer;
import com.gempukku.swccgo.collection.TransferDAO;
import com.gempukku.swccgo.db.*;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.tournament.TournamentDAO;
import com.gempukku.swccgo.tournament.TournamentMatchDAO;
import com.gempukku.swccgo.tournament.TournamentPlayerDAO;

import java.lang.reflect.Type;
import java.util.Map;

public class DaoBuilder {
    public static void fillObjectMap(Map<Type, Object> objectMap) {
        DbAccess dbAccess = new DbAccess();
        CollectionSerializer collectionSerializer = new CollectionSerializer();

        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
        objectMap.put(SwccgCardBlueprintLibrary.class, library);
        objectMap.put(LeagueParticipationDAO.class, new DbLeagueParticipationDAO(dbAccess));
        objectMap.put(LeagueMatchDAO.class, new DbLeagueMatchDAO(dbAccess));
        objectMap.put(TournamentDAO.class, new DbTournamentDAO(dbAccess));
        objectMap.put(TournamentPlayerDAO.class, new DbTournamentPlayerDAO(dbAccess));
        objectMap.put(TournamentMatchDAO.class, new DbTournamentMatchDAO(dbAccess));

        DbMerchantDAO dbMerchantDao = new DbMerchantDAO(dbAccess);
        CachedMerchantDAO merchantDao = new CachedMerchantDAO(dbMerchantDao);
        objectMap.put(MerchantDAO.class, merchantDao);

        objectMap.put(LeagueDAO.class, new DbLeagueDAO(dbAccess));
        objectMap.put(GameHistoryDAO.class, new DbGameHistoryDAO(dbAccess));

        DbDeckDAO dbDeckDao = new DbDeckDAO(dbAccess, library);
        CachedDeckDAO deckDao = new CachedDeckDAO(dbDeckDao);
        objectMap.put(DeckDAO.class, deckDao);

        DbCollectionDAO dbCollectionDao = new DbCollectionDAO(dbAccess, collectionSerializer);
        CachedCollectionDAO collectionDao = new CachedCollectionDAO(dbCollectionDao);
        objectMap.put(CollectionDAO.class, collectionDao);

        DbPlayerDAO dbPlayerDao = new DbPlayerDAO(dbAccess);
        CachedPlayerDAO playerDao = new CachedPlayerDAO(dbPlayerDao);
        objectMap.put(PlayerDAO.class, playerDao);
        
        DbTransferDAO dbTransferDao = new DbTransferDAO(dbAccess);
        CachedTransferDAO transferDao = new CachedTransferDAO(dbTransferDao);
        objectMap.put(TransferDAO.class, transferDao);

        DbIpBanDAO dbIpBanDao = new DbIpBanDAO(dbAccess);
        CachedIpBanDAO ipBanDao = new CachedIpBanDAO(dbIpBanDao);
        objectMap.put(IpBanDAO.class, ipBanDao);

        CacheManager cacheManager = new CacheManager();
        cacheManager.addCache(merchantDao);
        cacheManager.addCache(deckDao);
        cacheManager.addCache(collectionDao);
        cacheManager.addCache(playerDao);
        cacheManager.addCache(transferDao);
        cacheManager.addCache(ipBanDao);
        objectMap.put(CacheManager.class, cacheManager);
    }
}
