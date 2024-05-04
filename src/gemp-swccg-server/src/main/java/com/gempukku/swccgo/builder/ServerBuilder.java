package com.gempukku.swccgo.builder;

import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.TransferDAO;
import com.gempukku.swccgo.db.*;
import com.gempukku.swccgo.game.GameHistoryService;
import com.gempukku.swccgo.game.GameRecorder;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.merchant.MerchantService;
import com.gempukku.swccgo.packagedProduct.DraftPackStorage;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;
import com.gempukku.swccgo.service.AdminService;
import com.gempukku.swccgo.service.LoggedUserHolder;
import com.gempukku.swccgo.tournament.*;

import java.lang.reflect.Type;
import java.util.Map;

public class ServerBuilder {

    public static void CreatePrerequisites(Map<Type, Object> objectMap) {
        LoggedUserHolder loggedUserHolder = new LoggedUserHolder();
        loggedUserHolder.start();
        objectMap.put(LoggedUserHolder.class, loggedUserHolder);
    }
    public static void CreateServices(Map<Type, Object> objectMap) {
        objectMap.put(SwccgoFormatLibrary.class,
                new SwccgoFormatLibrary(
                        extract(objectMap, SwccgCardBlueprintLibrary.class)));
        objectMap.put(GameHistoryService.class,
                new GameHistoryService(
                        extract(objectMap, GameHistoryDAO.class)));
        objectMap.put(GameRecorder.class,
                new GameRecorder(
                        extract(objectMap, GameHistoryService.class)));

        objectMap.put(CollectionsManager.class,
                new CollectionsManager(
                        extract(objectMap, PlayerDAO.class),
                        extract(objectMap, CollectionDAO.class),
                        extract(objectMap, TransferDAO.class),
                        extract(objectMap, SwccgCardBlueprintLibrary.class)));

        objectMap.put(LeagueService.class,
                new LeagueService(
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, LeagueDAO.class),
                        extract(objectMap, LeagueMatchDAO.class),
                        extract(objectMap, LeagueParticipationDAO.class),
                        extract(objectMap, CollectionsManager.class)));

        objectMap.put(AdminService.class,
                new AdminService(
                        extract(objectMap, PlayerDAO.class),
                        extract(objectMap, IpBanDAO.class),
                        extract(objectMap, LoggedUserHolder.class)
                ));

        TournamentPrizeSchemeRegistry tournamentPrizeSchemeRegistry = new TournamentPrizeSchemeRegistry();
        PairingMechanismRegistry pairingMechanismRegistry = new PairingMechanismRegistry();

        objectMap.put(TournamentService.class,
                new TournamentService(
                        extract(objectMap, CollectionsManager.class),
                        extract(objectMap, PackagedProductStorage.class),
                        new DraftPackStorage(),
                        pairingMechanismRegistry,
                        tournamentPrizeSchemeRegistry,
                        extract(objectMap, TournamentDAO.class),
                        extract(objectMap, TournamentPlayerDAO.class),
                        extract(objectMap, TournamentMatchDAO.class)));

        objectMap.put(MerchantService.class,
                new MerchantService(
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, CollectionsManager.class),
                        extract(objectMap, MerchantDAO.class)));

        objectMap.put(ChatServer.class, new ChatServer());

        objectMap.put(SwccgoServer.class,
                new SwccgoServer(
                        extract(objectMap, DeckDAO.class),
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, ChatServer.class),
                        extract(objectMap, GameRecorder.class),
                        extract(objectMap, InGameStatisticsDAO.class)));

        objectMap.put(HallServer.class,
                new HallServer(
                        extract(objectMap, SwccgoServer.class),
                        extract(objectMap, ChatServer.class),
                        extract(objectMap, LeagueService.class),
                        extract(objectMap, TournamentService.class),
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, SwccgoFormatLibrary.class),
                        extract(objectMap, CollectionsManager.class),
                        extract(objectMap, PlayerDAO.class),
                        extract(objectMap, IpBanDAO.class),
                        extract(objectMap, GempSettingDAO.class),
                        extract(objectMap, AdminService.class),
                        tournamentPrizeSchemeRegistry,
                        pairingMechanismRegistry
                ));
    }

    private static <T> T extract(Map<Type, Object> objectMap, Class<T> clazz) {
        T result = (T) objectMap.get(clazz);
        if (result == null)
            throw new RuntimeException("Unable to find class " + clazz.getName());
        return result;
    }

    public static void StartServers(Map<Type, Object> objectMap) {
        extract(objectMap, HallServer.class).startServer();
        extract(objectMap, SwccgoServer.class).startServer();
        extract(objectMap, ChatServer.class).startServer();
    }

    public static void StopServers(Map<Type, Object> objectMap) {
        extract(objectMap, HallServer.class).stopServer();
        extract(objectMap, SwccgoServer.class).stopServer();
        extract(objectMap, ChatServer.class).stopServer();
    }
}
