package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.route.Route;
import com.digicompass.backend.application.models.route.RouteGeometry;
import com.digicompass.backend.configuration.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface RouteService {
    List<Route> getRoutes();
    RouteGeometry getRouteById(Long id);
    List<Route> getFilteredRoutes(String type, String difficulty, Float distance);
    List<Route> searchRoutes(String keyword);
    List<Route> getLikedRoutesByUserId(Long userId);
    void saveRoute(Route route, List<MultipartFile> images, Long id) throws IOException;
    void deleteRoute(Long id, UserPrincipal principal);
//    void updateRoute(Route route, List<MultipartFile> images, Long id) throws IOException;
}
