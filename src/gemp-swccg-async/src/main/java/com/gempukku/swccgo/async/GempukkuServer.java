package com.gempukku.swccgo.async;

import com.gempukku.swccgo.builder.DaoBuilder;
import com.gempukku.swccgo.builder.PackagedProductStorageBuilder;
import com.gempukku.swccgo.builder.ServerBuilder;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GempukkuServer {
    private final Map<Type, Object> context;

    public GempukkuServer() {
        Map<Type, Object> objects = new HashMap<>();

        var logger = LogManager.getLogger(GempukkuServer.class);

        //Libraries and other important prereq managers that are used by lots of other managers
        logger.info("GempukkuServer loading prerequisites...");
        ServerBuilder.CreatePrerequisites(objects);
        //Now bulk initialize various managers
        logger.info("GempukkuServer loading DAOs...");
        DaoBuilder.CreateDatabaseAccessObjects(objects);
        logger.info("GempukkuServer loading card products...");
        PackagedProductStorageBuilder.CreateProducts(objects);
        logger.info("GempukkuServer loading services...");
        ServerBuilder.CreateServices(objects);
        logger.info("GempukkuServer starting servers...");
        ServerBuilder.StartServers(objects);
        logger.info("GempukkuServer startup complete.");

        context = objects;
    }

    public Map<Type, Object> getContext() {
        return context;
    }
}
