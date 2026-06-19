package com.pancaran.master.feature.routeplan.helper;

import com.pancaran.master.feature.routeplan.dto.RouteCoordinatesDto;
import org.locationtech.jts.geom.*;

import java.util.List;

public class GeometryUtil {

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * Membuat Point dari latitude dan longitude
     */
    public static Point createPoint(Double lat, Double lng) {

        if (lat == null || lng == null) {
            return null;
        }

        return GEOMETRY_FACTORY.createPoint(
                new Coordinate(lng, lat)
        );
    }

    /**
     * Membuat LineString dari kumpulan koordinat segment
     */
    public static LineString createLineString(List<RouteCoordinatesDto> coordinates) {

        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }

        Coordinate[] points = coordinates.stream()
                .map(x -> new Coordinate(x.getLng(), x.getLat()))
                .toArray(Coordinate[]::new);

        return GEOMETRY_FACTORY.createLineString(points);
    }
}
