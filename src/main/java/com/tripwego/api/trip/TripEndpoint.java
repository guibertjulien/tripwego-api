package com.tripwego.api.trip;

import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.tripwego.dto.trip.Trip;

import java.util.List;
import java.util.logging.Logger;

/**
 * The Echo API which Endpoints will be exposing.
 */
// [START echo_api_annotation]
@Api(
        name = "tripendpoint",
        version = "v1",
        namespace =
        @ApiNamespace(
                ownerDomain = "tripwego.com",
                ownerName = "tripwego.com",
                packagePath = ""
        ),
        // [START_EXCLUDE]
        issuers = {
                @ApiIssuer(
                        name = "firebase",
                        issuer = "https://securetoken.google.com/tripwego-api",
                        jwksUri = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com")
        }
        // [END_EXCLUDE]
)
// [END echo_api_annotation]
public class TripEndpoint {

    private static final Logger _logger = Logger.getLogger(TripEndpoint.class.getName());

    private TripRepository tripRepository = new TripRepository();
    private TripQueries tripQueries = new TripQueries();

    /**
     * This inserts a new entity into App Engine datastore. If the entity
     * already exists in the datastore, an exception is thrown. It uses HTTP
     * POST method.
     *
     * @param trip the entity to be inserted.
     * @return The inserted entity.
     */
    @ApiMethod(name = "insertTrip")
    public Trip insertTrip(Trip trip) {
        return tripRepository.create(trip);
    }

    /**
     * This method gets the entity having primary key id. It uses HTTP GET
     * method.
     *
     * @param id the primary key of the java bean.
     * @return The entity with primary key id.
     */
    @ApiMethod(name = "getTrip")
    public Trip getTrip(@Named("id") String id) {
        return tripRepository.retrieveEager(id);
    }

    @ApiMethod(name = "get_trip", path = "get_trip", httpMethod = ApiMethod.HttpMethod.GET, apiKeyRequired = AnnotationBoolean.TRUE)
    public Trip retrieveTrip(@Named("id") String id) {
        return tripRepository.retrieveEager(id);
    }

    /**
     * This method is used for updating an existing entity. If the entity does
     * not exist in the datastore, an exception is thrown. It uses HTTP PUT
     * method.
     *
     * @param trip the entity to be updated.
     * @return The updated entity.
     */
    @ApiMethod(name = "updateTrip")
    public Trip updateTrip(Trip trip) {
        return tripRepository.update(trip);
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "findTripsByUser", path = "findTripsByUser", httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Trip> findTripsByUser(@Named("userId") String userId, @Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {
        final List<Trip> trips = tripQueries.findTripsByUser(userId);
        return CollectionResponse.<Trip>builder().setItems(trips).setNextPageToken(cursorString).build();
    }

    @SuppressWarnings({"unchecked", "unused"})
    @ApiMethod(name = "findAllTrips", path = "findAllTrips", httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Trip> findAllTrips(@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {
        // TODO cursor
        final List<Trip> trips = tripQueries.findAllTrips();
        return CollectionResponse.<Trip>builder().setItems(trips).setNextPageToken(cursorString).build();
    }

    @ApiMethod(name = "copyTrip", path = "copyTrip", httpMethod = ApiMethod.HttpMethod.POST)
    public Trip copyTrip(Trip trip) {
        return tripRepository.copy(trip);
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "deleteOrRestoreTrip", path = "deleteOrRestoreTrip", httpMethod = ApiMethod.HttpMethod.PUT)
    public void deleteOrRestoreTrip(Trip trip) {
        try {
            tripRepository.deleteOrRestore(trip);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "publishOrPrivateTrip", path = "publishOrPrivateTrip", httpMethod = ApiMethod.HttpMethod.PUT)
    public void publishOrPrivateTrip(Trip trip) {
        try {
            tripRepository.publishOrPrivate(trip);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "deleteTripsCancelled", path = "deleteTripsCancelled", httpMethod = ApiMethod.HttpMethod.GET)
    public void deleteTripsCancelled(@Named("userId") String userId) {
        tripRepository.deleteTripsCancelledFromUser(userId);
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "ping", path = "ping", httpMethod = ApiMethod.HttpMethod.GET)
    public void ping() {
        _logger.info("ping");
    }
}
